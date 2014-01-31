package com.kaidoe.sojero;

import java.util.ArrayList;
import org.zeromq.ZMQ;

public class ServiceContext
{

	private ArrayList<Service> servicesList = new ArrayList<Service>();;

    private ZMQ.Context zmqContext;
    public ZMQ.Socket zmqPublisher;
    public ZMQ.Socket zmqSubscriber;

	public ServiceContext()
	{

        zmqContext = ZMQ.context(1);

        zmqPublisher = zmqContext.socket(ZMQ.PUB);
        zmqPublisher.bind("tcp://*:14000");

        zmqSubscriber = zmqContext.socket(ZMQ.SUB);
        zmqSubscriber.connect("tcp://127.0.0.1:14000");
        zmqSubscriber.subscribe("Event".getBytes());

        ServiceContextPoller contextPoller = new ServiceContextPoller(this);

        Thread contextPollerThread = new Thread(contextPoller);

        // Start the poller
        contextPollerThread.start();

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

    private void triggerEventOnService(String theServiceID, ServiceEvent theServiceEvent) {

        for (Service thisService : servicesList) {
            if (thisService.getServiceID().equals(theServiceID)) thisService.onEvent(theServiceEvent);
        }

    }

    class ServiceContextPoller implements Runnable
    {

        private ServiceContext serviceContext;

        public ServiceContextPoller(ServiceContext theServiceContext)
        {
            super();
            serviceContext = theServiceContext;

        }

        public void run() {

            while (!Thread.currentThread ().isInterrupted ())
            {
                String msg = zmqSubscriber.recvStr();
                String serviceID = zmqSubscriber.recvStr();
                String eventID = zmqSubscriber.recvStr();
                String eventData = zmqSubscriber.recvStr();

                serviceContext.triggerEventOnService(serviceID, new ServiceEvent(eventID, eventData));

            }

        }

    }

}
