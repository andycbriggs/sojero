package com.kaidoe.sojero;

import org.zeromq.ZMsg;

public class ServiceMsg {

    public static final Integer EVENT = 1;
    public static final Integer COMMAND = 2;
    public static final Integer REQUEST = 3;
    public static final Integer RESPONSE = 4;

    private String serviceID;
    private Integer msgType;
    private String methodID;
    private byte[] msgData;

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
        msgData = theEventData;
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
        msgData = zMsg.pop().getData();
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
        ret.add(msgData);

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

    public byte[] getMsgData() {
        return msgData;
    }

    public void setMsgData(byte[] msgData) {
        this.msgData = msgData;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

}
