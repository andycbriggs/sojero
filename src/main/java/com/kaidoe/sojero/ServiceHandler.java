package com.kaidoe.sojero;

public abstract class ServiceHandler {

    private String methodID;

    private ServiceMsg.MsgType acceptsMsgType;

    public ServiceHandler(String methodID, ServiceMsg.MsgType acceptsMsgType)
    {
        this.methodID = methodID;
        this.acceptsMsgType = acceptsMsgType;
    }

    public String getMethodID()
    {
        return methodID;
    }

    public ServiceMsg.MsgType getAcceptsMsgType()
    {
        return acceptsMsgType;
    }

    protected void handleServiceMsg(ServiceMsg serviceMsg) {

        if (serviceMsg.getMsgType() == acceptsMsgType &&
                serviceMsg.getMethodID().equals(methodID)) onServiceMsg(serviceMsg);

    }

    protected void onServiceMsg(ServiceMsg serviceMsg)
    {

    }


}
