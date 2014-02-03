package com.kaidoe.sojero;

import org.zeromq.ZMsg;

import java.nio.ByteBuffer;

public class ServiceMsg {

    public static enum MsgType {

        EVENT(1),
        COMMAND(2),
        REQUEST(3),
        RESPONSE(4);

        public final int type;
        MsgType(int type) { this.type = type; }

        public static MsgType fromBytes(byte[] bytes)
        {

            int i = ByteBuffer.wrap(bytes).getInt();

            return fromInt(i);

        }

        public byte[] getBytes() {

            ByteBuffer b = ByteBuffer.allocate(4);
            return b.putInt(type).array();

        }

        public static MsgType fromInt(int i) throws IllegalArgumentException {
            switch(i) {
                case 1:
                    return EVENT;
                case 2:
                    return COMMAND;
                case 3:
                    return REQUEST;
                case 4:
                    return RESPONSE;
                default:
                    throw new IllegalArgumentException("MsgType not found");
            }
        }

    }

    private String serviceID;
    private MsgType msgType;
    private String methodID;
    private byte[] msgData;

    /**
     * Constructor for outgoing messages
     * @param serviceID
     * @param msgType
     * @param methodID
     * @param msgData
     */
    public ServiceMsg(String serviceID, MsgType msgType, String methodID, byte[] msgData)
    {
        this.serviceID = serviceID;
        this.msgType = msgType;
        this.methodID = methodID;
        this.msgData = msgData;
    }

    /**
     * Constructor for incoming messages
     * @param zMsg
     */
    public ServiceMsg(ZMsg zMsg)
    {
        serviceID = zMsg.popString();
        msgType = MsgType.fromBytes(zMsg.pop().getData());
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
        ret.add(msgType.getBytes());
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

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

}
