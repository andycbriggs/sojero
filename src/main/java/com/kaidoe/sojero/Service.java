package com.kaidoe.sojero;

import java.util.ArrayList;

public class Service {
 
	private String serviceID;
	private ServiceContext serviceContext;

    private ArrayList<ServiceEventHandler> eventHandlers = new ArrayList<ServiceEventHandler>();

	/**
	 * Constructor for new Service.
	 *
	 * @param theServiceContext parent Service interface
	 * @param theServiceID string identifier for the service
	 */
	public Service(ServiceContext theServiceContext, String theServiceID)
	{

		serviceID = theServiceID;
        serviceContext = theServiceContext;

	}

	/**
	 * Get this Service ID.
	 *
	 * @return ID of the Service
	 */
	public String getServiceID()
	{

		return serviceID;

	}

    public void trigger(ServiceEvent event)
    {
        serviceContext.zmqPublisher.sendMore("Event");
        serviceContext.zmqPublisher.sendMore(serviceID);
        serviceContext.zmqPublisher.sendMore(event.getEventID());
        serviceContext.zmqPublisher.send(event.getData());
    }

    public void addEventHandler(ServiceEventHandler eventHandler)
    {

        eventHandlers.add(eventHandler);

    }

    public void onEvent(ServiceEvent event)
    {

        String eventID = event.getEventID();

        for (ServiceEventHandler thisEventHandler : eventHandlers) {
            if (thisEventHandler.getEventID().equals(eventID)) thisEventHandler.onServiceEvent(event);
        }


    }
}