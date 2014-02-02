package com.kaidoe.sojero;

import java.io.*;
import java.util.UUID;

/**
 * Pointer to a remote ServiceContext instance
 */
public class ServiceNode implements Serializable {

    private final String ipAddress;
    private final long pubPort;
    private UUID nodeUUID;
    private long pongTime;

    public static ServiceNode getFromByteArray(byte[] byteArray) throws ClassNotFoundException {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(byteArray);
            ObjectInputStream o = new ObjectInputStream(b);
            Object obj = o.readObject();
            if (obj.getClass() == ServiceNode.class)
                return (ServiceNode) obj;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw (new ClassNotFoundException());
    }

    public ServiceNode(String ipAddress, long pubPort)
    {
        this.nodeUUID = UUID.randomUUID();
        this.ipAddress = ipAddress;
        this.pubPort = pubPort;
        pongTime = System.currentTimeMillis();
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this);
        return b.toByteArray();

    }

    public String getIpAddress() {
        return ipAddress;
    }

    public long getPubPort() {
        return pubPort;
    }

    public String getNodeUUIDAsString() {
        return nodeUUID.toString();
    }

    public void pong() {

        pongTime = System.currentTimeMillis();

    }

    public long getPongTime()
    {

        return pongTime;

    }


}
