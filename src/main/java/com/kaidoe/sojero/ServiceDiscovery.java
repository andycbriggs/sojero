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

    private static long TIMEOUT = 5000;

    protected static final int discoveryPort = 12880;

    public ServiceDiscovery(ServiceContext serviceContext, ServiceNode selfServiceNode)
    {
        this.serviceContext = serviceContext;

        this.selfServiceNode = selfServiceNode;

        serviceNodeList = new ArrayList<ServiceNode>();

        ServiceDiscoveryPoller sdp = new ServiceDiscoveryPoller(this);
        sdp.start();

        Timer timer = new Timer();
        timer.schedule(new PingPongTask(), 0, ServiceDiscovery.TIMEOUT / 2);
    }

    public static void setTimeoutMillis(long timeoutMillis) {

        ServiceDiscovery.TIMEOUT = timeoutMillis;

    }

    public synchronized void foundNode(ServiceNode serviceNode)
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

    public synchronized void addServiceNode(ServiceNode serviceNode)
    {

        serviceNodeList.add(serviceNode);
        serviceContext.connectServiceNode(serviceNode);

    }

    public synchronized void removeTimedOutNodes()
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

    public synchronized List<ServiceNode> getServiceNodeList()
    {

        return serviceNodeList;
    }

    public synchronized void emitBeacon()
    {
        emitBeacon(selfServiceNode);
    }

    public synchronized void emitBeacon(ServiceNode serviceNode)
    {

        try {
        DatagramSocket s = new DatagramSocket();

        byte[] beaconBytes = serviceNode.toByteArray();
        DatagramPacket dp = new DatagramPacket(beaconBytes, beaconBytes.length);
        s.setBroadcast(true);
        s.connect(new InetSocketAddress("255.255.255.255", discoveryPort));
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

            while (!Thread.currentThread().isInterrupted())
            {

                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(incoming);
                    ServiceNode receivedNode = ServiceNode.getFromByteArray(incoming.getData());
                    selfServiceNode.setIpAddress(incoming.getAddress());
                    serviceDiscovery.foundNode(receivedNode);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }



            }

        }

    }
}
