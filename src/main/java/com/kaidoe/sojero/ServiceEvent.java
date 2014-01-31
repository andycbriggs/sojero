package com.kaidoe.sojero;

import org.zeromq.ZMsg;

public class ServiceEvent {

    private String eventID;
    private String eventData;

    public ServiceEvent(String theID)
    {

        eventID = theID;

    }

    public ServiceEvent(String theID, String theEventData)
    {

        eventID = theID;
        eventData = theEventData;

    }

    public void set(String value)
    {
        eventData = value;

    }

    public String getData()
    {

        return eventData;

    }

    public String getEventID() {
        return eventID;
    }
}
