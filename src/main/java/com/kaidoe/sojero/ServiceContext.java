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

	public ServiceContext()
	{

      initialize("tcp://*:14000");

	}

    public ServiceContext(String subAddress)
    {

        initialize(subAddress);

    }

    public void initialize(String subAddress)
    {
        zmqContext = new ZContext();

        zmqPublisher = zmqContext.createSocket(ZMQ.PUB);

        // TODO: Service Discovery / Definition

        zmqPublisher.bind(subAddress);

        zmqSubscriber = zmqContext.createSocket(ZMQ.SUB);

        ServiceContextPoller contextPoller = new ServiceContextPoller(this);
        Thread contextPollerThread = new Thread(contextPoller);

        // Start the poller
        contextPollerThread.start();

    }

    public void close()
    {

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

    private void triggerEventOnService(String theServiceID, ServiceMsg theServiceMsg)
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

    public void addServiceNode(ServiceNode serviceNode)
    {

        zmqSubscriber.connect("tcp://" + serviceNode.getIpAddress() + ":" + serviceNode.getPubPort());

    }

    public void removeServiceNode(ServiceNode serviceNode)
    {

        zmqSubscriber.disconnect("tcp://" + serviceNode.getIpAddress() + ":" + serviceNode.getPubPort());

    }

    class ServiceContextPoller implements Runnable
    {

        private ServiceContext serviceContext;
        private boolean flagStop = false;

        public ServiceContextPoller(ServiceContext theServiceContext)
        {
            super();
            serviceContext = theServiceContext;

        }

        public void run() {

            while (!Thread.currentThread().isInterrupted() && !flagStop)
            {

                ZMQ.PollItem[] items = new ZMQ.PollItem[] {
                        new ZMQ.PollItem(zmqSubscriber, ZMQ.Poller.POLLIN)
                };

                while (!Thread.currentThread().isInterrupted()) {
                    //  Tick once per second, pulling in arriving messages

                    ZMQ.poll(items, 10);
                    if (items[0].isReadable()) {
                        ZMsg msg = ZMsg.recvMsg(zmqSubscriber);

                        ServiceMsg serviceMsg = new ServiceMsg(msg);
                        msg.destroy();
                        serviceContext.triggerEventOnService(serviceMsg.getServiceID(), serviceMsg);

                    }

                }

            }

        }

    }

}
