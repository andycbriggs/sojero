package com.kaidoe.sojero;

import java.util.ArrayList;

public class Service {
 
	private String serviceID;
	private ServiceContext serviceContext;

    private ArrayList<ServiceCommandHandler> commandHandlers = new ArrayList<ServiceCommandHandler>();
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

    public void execute(ServiceMsg command)
    {

        serviceContext.emitCommand(command);

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

    public void addCommandHandler(ServiceCommandHandler commandHandler) {

        registerAsSubscriber();

        commandHandlers.add(commandHandler);

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
    public void onServiceMsg(ServiceMsg event)
    {

        Integer msgType = event.getMsgType();

        if (msgType.equals(ServiceMsg.EVENT)) {
            for (ServiceEventHandler thisEventHandler : eventHandlers) {
                if (thisEventHandler.getEventID().equals(event.getMethodID())) thisEventHandler.onServiceEvent(event);
            }
        } else if (msgType.equals(ServiceMsg.COMMAND)) {
            for (ServiceCommandHandler thisCommandHandler : commandHandlers) {
                if (thisCommandHandler.getCommandID().equals(event.getMethodID())) thisCommandHandler.onServiceCommand(event);
            }
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