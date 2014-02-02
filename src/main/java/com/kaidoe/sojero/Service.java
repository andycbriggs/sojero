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

    /**
     * Emit an event on the network
     *
     * @param event SeviceMsg
     */
    public void trigger(ServiceMsg event)
    {

        serviceContext.emitEvent(event);

    }

    /**
     * Register an event handler on the service
     *
     * @param eventHandler ServiceEventHandler
     */
    public void addEventHandler(ServiceEventHandler eventHandler)
    {

        registerAsSubscriber();

        eventHandlers.add(eventHandler);

    }

    /**
     * Subscribes the context to the topic for this service
     *
     */
    private void registerAsSubscriber() {

        serviceContext.registerSubscriber(serviceID);

    }

    /**
     * Handle incoming events from the context
     *
     * @param event ServiceMsg
     */
    public void onEvent(ServiceMsg event)
    {
        String eventID = event.getMethodID();
        for (ServiceEventHandler thisEventHandler : eventHandlers) {
            if (thisEventHandler.getEventID().equals(eventID)) thisEventHandler.onServiceEvent(event);
        }
    }

    /**
     * Factory methods for ServiceMsg objects
     *
     * @param eventID String
     * @param data byte[]
     * @return ServiceMsg
     */
    public ServiceMsg getEventMsg(String eventID, byte[] data)
    {
        return new ServiceMsg(serviceID, ServiceMsg.EVENT, eventID, data);
    }

    public ServiceMsg getCommandMsg(String commandtID, byte[] data)
    {
        return new ServiceMsg(serviceID, ServiceMsg.COMMAND, commandtID, data);
    }

    public ServiceMsg getRequestMsg(String requestID, byte[] data)
    {
        return new ServiceMsg(serviceID, ServiceMsg.REQUEST, requestID, data);
    }

    public ServiceMsg getResponseMsg(String requestID, byte[] data)
    {
        return new ServiceMsg(serviceID, ServiceMsg.RESPONSE, requestID, data);
    }


}