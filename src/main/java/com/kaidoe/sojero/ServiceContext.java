package com.kaidoe.sojero;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import zmq.Poller;

public class ServiceContext
{

	private ArrayList<Service> services = new ArrayList<Service>();

    public ZContext zmqContext;
    public ZMQ.Socket zmqPublisher;
    public ZMQ.Socket zmqSubscriber;

    private ServiceContextPoller contextPoller;
    private ServiceDiscovery serviceDiscovery;

	public ServiceContext()
	{

        initialize();

	}

    public void initialize()
    {
        zmqContext = new ZContext();

        // pub sub
        zmqPublisher = zmqContext.createSocket(ZMQ.PUB);
        long zmqPubPort = zmqPublisher.bindToRandomPort("tcp://*");
        zmqPublisher.setLinger(0);

        // service discovery
        try {
            serviceDiscovery = new ServiceDiscovery(this, zmqPubPort);
        } catch (UnknownHostException e) {
            // this node doesn't have a network
            // TODO: the library should work without a network
            e.printStackTrace();
        }

        zmqSubscriber = zmqContext.createSocket(ZMQ.SUB);
        zmqSubscriber.setLinger(0);
        contextPoller = new ServiceContextPoller(this);
        contextPoller.start();

    }

    public void close()
    {

        contextPoller.setFlagStop();
        try {
            contextPoller.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serviceDiscovery.setFlagStop();

        zmqContext.destroySocket(zmqSubscriber);
        zmqContext.destroySocket(zmqPublisher);

        zmqContext.destroy();

    }

	/**
	 * Get a service from the interface or create a new one if it does not exist.
	 *
	 * @param theServiceID string identifier for the service
	 * @return Service reference if the service exists, otherwise false
	 */
	public Service getService(String theServiceID)
	{
		// get service
        for (Service thisService : services) {
            if (thisService.getServiceID().equals(theServiceID)) return thisService;
        }

		// otherwise create service
		Service theService = new Service(this, theServiceID);
		services.add(theService);
		return theService;
	}

    private synchronized void triggerEventOnService(String serviceID, ServiceMsg serviceMsg)
    {

        for (Service service : services) {
            if (service.getServiceID().equals(serviceID)) service.onServiceMsg(serviceMsg);
        }

    }

    public void emitServiceMsg(ServiceMsg event)
    {

        event.toZMsg().send(zmqPublisher, true);

    }


    public void registerSubscriber(String serviceID)
    {

        zmqSubscriber.subscribe(serviceID.getBytes());

    }

    public void connectServiceNode(ServiceNode serviceNode)
    {

        zmqSubscriber.connect("tcp://" + serviceNode.getInetAddress().getHostAddress() + ":" + serviceNode.getPubPort());

    }

    public void disconnectServiceNode(ServiceNode serviceNode)
    {

        //zmqSubscriber.disconnect("tcp://" + serviceNode.getInetAddress().getHostAddress() + ":" + serviceNode.getPubPort());

    }

    public int countServiceNodes() {

        return serviceDiscovery.countServiceNodes();

    }

    class ServiceContextPoller extends Thread
    {

        private ServiceContext serviceContext;
        private boolean flagStop;

        public ServiceContextPoller(ServiceContext theServiceContext)
        {
            super("ServiceContextPoller");
            serviceContext = theServiceContext;
            flagStop = false;
        }

        public synchronized void setFlagStop()
        {
            flagStop = true;
        }

        public void run() {

            ZMQ.PollItem[] items = new ZMQ.PollItem[] {
                    new ZMQ.PollItem(zmqSubscriber, ZMQ.Poller.POLLIN)
            };

            while (!this.isInterrupted() && !flagStop) {

                ZMQ.poll(items, 100);

                if (this.isInterrupted() && flagStop) break;

                if (items[0].isReadable()) {
                    ZMsg msg = ZMsg.recvMsg(zmqSubscriber);
                    ServiceMsg serviceMsg = new ServiceMsg(msg);
                    msg.destroy();
                    msg = null;
                    serviceContext.triggerEventOnService(serviceMsg.getServiceID(), serviceMsg);
                }

            }

        }

    }

}
