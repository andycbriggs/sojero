package com.kaidoe.sojero;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Handles Node Discovery
 */
public class ServiceDiscovery {

    private final ArrayList<ServiceNode> serviceNodeList;

    private ServiceNode selfServiceNode;
    private ServiceContext serviceContext;
    private ServiceDiscoveryPoller sdp;
    private Timer timer;

    private static long TIMEOUT = 5000;

    protected static final int discoveryPort = 12880;

    public ServiceDiscovery(ServiceContext serviceContext, long zmqPubPort) throws UnknownHostException {

        this.serviceContext = serviceContext;

        serviceNodeList = new ArrayList<ServiceNode>();

        this.selfServiceNode = new ServiceNode(InetAddress.getByName("127.0.0.1"), zmqPubPort);

        sdp = new ServiceDiscoveryPoller(this);
        sdp.start();

        timer = new Timer();
        timer.schedule(new PingPongTask(), 0, ServiceDiscovery.TIMEOUT / 2);
    }

    public static void setTimeoutMillis(long timeoutMillis) {

        ServiceDiscovery.TIMEOUT = timeoutMillis;

    }

    public void foundNode(ServiceNode serviceNode)
    {

        // if the node already exists, pong it!
        Iterator<ServiceNode> i = serviceNodeList.iterator();

        while(i.hasNext()) {
            ServiceNode sn = i.next();
            if (sn.getNodeUUIDAsString().equals(serviceNode.getNodeUUIDAsString())) {
               sn.pong();
               return;
            }
        }

        // if the node is this node, ignore it.
        if (selfServiceNode.getNodeUUIDAsString().equals(serviceNode.getNodeUUIDAsString()))
            return;

        // otherwise add a new node
        addServiceNode(serviceNode);

    }

    public void addServiceNode(ServiceNode serviceNode)
    {

        serviceNodeList.add(serviceNode);
        serviceContext.connectServiceNode(serviceNode);

    }

    public void removeTimedOutNodes()
    {

        long currentTime = System.currentTimeMillis();

        Iterator<ServiceNode> i = serviceNodeList.iterator();

        while(i.hasNext()) {
            ServiceNode sn = i.next();
            if (currentTime - sn.getPongTime() > ServiceDiscovery.TIMEOUT) {
                serviceContext.disconnectServiceNode(sn);
                i.remove();
            }
        }

    }

    public List<ServiceNode> getServiceNodeList()
    {

        return serviceNodeList;
    }

    public void emitBeacon()
    {
        emitBeacon(selfServiceNode);
    }

    public void emitBeacon(ServiceNode serviceNode)
    {

        try {
            DatagramSocket s = new DatagramSocket();

            byte[] beaconBytes = serviceNode.toByteArray();
            DatagramPacket dp = new DatagramPacket(beaconBytes, beaconBytes.length);
            s.setBroadcast(true);
            s.connect(new InetSocketAddress(InetAddress.getByName("255.255.255.255"), discoveryPort));
            s.send(dp);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setSelfServiceNode(ServiceNode selfServiceNode) {

        this.selfServiceNode = selfServiceNode;

    }

    public int countServiceNodes() {

        return serviceNodeList.size();

    }

    public void setFlagStop() {

        timer.cancel();
        timer.purge();
        sdp.setFlagStop(true);
        sdp.interrupt();
        try {
            sdp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handles removing timed out nodes
     */
    class PingPongTask extends TimerTask {
        public void run() {

            emitBeacon();
            removeTimedOutNodes();

        }
    }

    /**
     * Handles incoming ServiceNode beacons
     */
    class ServiceDiscoveryPoller extends Thread
    {

        private ServiceDiscovery serviceDiscovery;

        private DatagramSocket socket;

        private boolean flagStop;

        public ServiceDiscoveryPoller(ServiceDiscovery serviceDiscovery)
        {
            super("ServiceDiscoveryPoller");
            this.serviceDiscovery = serviceDiscovery;

            try {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(discoveryPort));

            } catch (SocketException e) {
                e.printStackTrace();
            }

        }

        public void run() {

            byte[] buffer = new byte[2048];

            while (!Thread.currentThread().isInterrupted() && !flagStop)
            {

                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(incoming);

                    // check if the thread has been interrupted since calling receive
                    if (Thread.currentThread().isInterrupted() || flagStop) break;

                    ServiceNode receivedNode = ServiceNode.getFromByteArray(incoming.getData());
                    receivedNode.setIpAddress(incoming.getAddress());
                    serviceDiscovery.foundNode(receivedNode);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            socket.close();

        }

        public void setFlagStop(boolean flagStop) {
            this.flagStop = flagStop;
        }
    }
}
