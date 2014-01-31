package com.kaidoe.sojero;

import org.zeromq.ZMsg;

public class ServiceMsg {

    public static Integer EVENT = 1;
    public static Integer COMMAND = 2;
    public static Integer REQUEST = 3;
    public static Integer RESPONSE = 4;

    private String serviceID;
    private Integer msgType;
    private String methodID;
    private byte[] eventData;

    /**
     * Constructor for outgoing messages
     * @param theServiceID
     * @param theMsgType
     * @param theMethodID
     * @param theEventData
     */
    public ServiceMsg(String theServiceID, int theMsgType, String theMethodID, byte[] theEventData)
    {
        serviceID = theServiceID;
        msgType = theMsgType;
        methodID = theMethodID;
        eventData = theEventData;
    }

    /**
     * Constructor for incoming messages
     * @param zMsg
     */
    public ServiceMsg(ZMsg zMsg)
    {
        serviceID = zMsg.popString();
        msgType = Integer.parseInt(zMsg.popString());
        methodID = zMsg.popString();
        eventData = zMsg.pop().getData();
    }

    /**
     * Factory method for to convert to ZMsg for transmission
     * @return ZMsg
     */
    public ZMsg toZMsg()
    {
        ZMsg ret = new ZMsg();

        ret.addString(serviceID);
        ret.addString(msgType.toString());
        ret.addString(methodID);
        ret.add(eventData);

        return ret;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getMethodID() {
        return methodID;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public byte[] getEventData() {
        return eventData;
    }

    public void setEventData(byte[] eventData) {
        this.eventData = eventData;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

}
