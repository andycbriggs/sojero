package com.kaidoe.sojero;

public class ServiceEventHandler {

    private String eventID;

    public ServiceEventHandler(String theEventID)
    {
        eventID = theEventID;
    }

    public String getEventID()
    {
        return eventID;
    }

    protected void onServiceEvent(ServiceMsg theEvent)
    {


    }

}
