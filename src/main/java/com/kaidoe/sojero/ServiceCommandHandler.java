package com.kaidoe.sojero;

public class ServiceCommandHandler extends ServiceHandler {

    public ServiceCommandHandler(String commandID)
    {
        super(commandID, ServiceMsg.MsgType.COMMAND);
    }

}
