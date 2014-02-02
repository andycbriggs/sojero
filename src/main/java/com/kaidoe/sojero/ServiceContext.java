package com.kaidoe.sojero;

import java.util.ArrayList;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class ServiceContext
{

	private ArrayList<Service> servicesList = new ArrayList<Service>();

    private ZContext zmqContext;
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
        zmqSubscriber = zmqContext.createSocket(ZMQ.SUB);
        contextPoller = new ServiceContextPoller(this);
        contextPoller.start();

        ServiceNode selfServiceNode = new ServiceNode("127.0.0.1", zmqPubPort);

        // service discovery
        serviceDiscovery = new ServiceDiscovery(this, selfServiceNode);

    }

    public void close()
    {

        contextPoller.setFlagStop();
        try {
            contextPoller.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        for (Service thisService : servicesList) {
            if (thisService.getServiceID().equals(theServiceID)) return thisService;
        }

		// otherwise create service
		Service theService = new Service(this, theServiceID);
		servicesList.add(theService);
		return theService;
	}

    private synchronized void triggerEventOnService(String theServiceID, ServiceMsg theServiceMsg)
    {

        for (Service thisService : servicesList) {
            if (thisService.getServiceID().equals(theServiceID)) thisService.onEvent(theServiceMsg);
        }

    }

    public void emitEvent(ServiceMsg event)
    {

        event.toZMsg().send(zmqPublisher, true);

    }

    public void registerSubscriber(String theServiceID)
    {

        zmqSubscriber.subscribe(theServiceID.getBytes());

    }

    public void connectServiceNode(ServiceNode serviceNode)
    {

        zmqSubscriber.connect("tcp://" + serviceNode.getIpAddress() + ":" + serviceNode.getPubPort());

    }

    public void disconnectServiceNode(ServiceNode serviceNode)
    {

        zmqSubscriber.disconnect("tcp://" + serviceNode.getIpAddress() + ":" + serviceNode.getPubPort());

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
                //  Tick once per second, pulling in arriving messages

                ZMQ.poll(items, 10);
                if (items[0].isReadable()) {
                    ZMsg msg = ZMsg.recvMsg(zmqSubscriber);

                    ServiceMsg serviceMsg = new ServiceMsg(msg);
                    msg.destroy();
                    serviceContext.triggerEventOnService(serviceMsg.getServiceID(), serviceMsg);

                }

                if (flagStop) break;



            }

        }

    }

}
