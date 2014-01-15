package com.kaidoe.sojero;

import java.util.ArrayList;
import java.util.Iterator;

import org.zeromq.ZMQ;

public class ServiceInterface
{

	private ArrayList<Service> servicesList;
	private ZMQ.Context zmqContext;

	private ZMQ.Socket zmqPublisher;
	private ZMQ.Socket zmqSubscriber;

	public ServiceInterface()
	{

		servicesList = new ArrayList<Service>();
		zmqContext = ZMQ.context(1);

		zmqPublisher = context.Socket()

	}

	/**
	 * Get a service from the interface or create a new one if it does not exist.
	 *
	 * @param theServiceID string identifier for the service
	 * @return Service reference if the service exists, otherwise false
	 * @throws Exception if no Service can be found
	 */
	public Service getService(String theServiceID)
	{

		// Get service
		Iterator<Service> iterator = servicesList.iterator();
		while (iterator.hasNext()) {
			Service thisService = iterator.next();
			if (thisService.getID() == theServiceID) return thisService;
		}

		// Create service
		Service theService = new Service("TestService");
		servicesList.add(theService);
		return theService;

	}	

	/**
	 * Checks if a service of that name currently exists.
	 *
	 * @param theServiceID string identifier for the service
	 * @return Service reference if the service exists, otherwise false
	 * @throws Exception if no Service can be found
	 */
	public boolean existsService(String theServiceID)
	{

		Iterator<Service> iterator = servicesList.iterator();

		while (iterator.hasNext()) {
			Service thisService = iterator.next();
			if (thisService.getID() == theServiceID) return true;
		}

		return false;

	}


}
