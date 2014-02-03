package com.kaidoe.sojero;

import java.util.ArrayList;
import java.util.Iterator;

public class Service {
 
	private String serviceID;
	private ServiceContext serviceContext;

    private ArrayList<ServiceHandler> handlers = new ArrayList<ServiceHandler>();

	/**
	 * Constructor for new Service.
	 *
	 * @param serviceContext parent Service interface
	 * @param serviceID string identifier for the service
	 */
	public Service(ServiceContext serviceContext, String serviceID)
	{

		this.serviceID = serviceID;
        this.serviceContext = serviceContext;

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

    private void emitServiceMsg(ServiceMsg serviceMsg) {

        // fire event across the network
        serviceContext.emitServiceMsg(serviceMsg);

        // fire any local handlers
        onServiceMsg(serviceMsg);

    }

    /**
     * Sugar for event on the network
     *
     */
    public void trigger(String eventID, byte[] data)
    {

        emitServiceMsg(new ServiceMsg(serviceID, ServiceMsg.MsgType.EVENT, eventID, data));

    }

    /**
     * Sugar for command on the network
     *
     */
    public void execute(String commandtID, byte[] data)
    {

        emitServiceMsg(new ServiceMsg(serviceID, ServiceMsg.MsgType.COMMAND, commandtID, data));

    }

    public void execute(String commandtID)
    {

        emitServiceMsg(new ServiceMsg(serviceID, ServiceMsg.MsgType.COMMAND, commandtID, new byte[0]));

    }

    /**
     * Register an event handler on the service
     *
     * @param handler ServiceHandler
     */
    public void addHandler(ServiceHandler handler)
    {
        registerAsSubscriber();
        handlers.add(handler);
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
     * @param serviceMsg ServiceMsg
     */
    public void onServiceMsg(ServiceMsg serviceMsg)
    {

        Iterator<ServiceHandler> i = handlers.iterator();

        while (i.hasNext()) {

            ServiceHandler sh;
            sh = i.next();
            sh.handleServiceMsg(serviceMsg);

        }

    }

}