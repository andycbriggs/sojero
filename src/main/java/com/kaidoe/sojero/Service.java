package com.kaidoe.sojero;

public class Service 
{
 
	private String id;
	private ServiceInterface serviceInterface;

	/**
	 * Constructor for new Service.
	 *
	 * @param theServiceInterface parent Service interface
	 * @param theServiceID string identifier for the service
	 */
	public Service(ServiceInterface theServiceInterface, String theServiceID)
	{

		id = theServiceID;
		inf = serviceInterface;

	}

	/**
	 * Get this Service ID.
	 *
	 * @return ID of the Service
	 */
	public String getID()
	{

		return id;

	}

}