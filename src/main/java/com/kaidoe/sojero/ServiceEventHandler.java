package com.kaidoe.sojero;

public class ServiceEventHandler extends ServiceHandler {

    public ServiceEventHandler(String eventID)
    {
        super(eventID, ServiceMsg.MsgType.EVENT);
    }

}
