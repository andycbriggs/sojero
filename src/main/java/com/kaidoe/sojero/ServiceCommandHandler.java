package com.kaidoe.sojero;


public class ServiceCommandHandler {

    private String commandID;

    public ServiceCommandHandler(String theCommandID)
    {
        commandID = theCommandID;
    }

    public String getCommandID()
    {
        return commandID;
    }

    protected void onServiceCommand(ServiceMsg theCommandMsg)
    {



    }

}
