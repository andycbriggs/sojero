package com.kaidoe.sojero;

import java.io.*;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Pointer to a remote ServiceContext instance
 */
public class ServiceNode implements Serializable {

    private InetAddress ipAddress;
    private long pubPort;
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

    public ServiceNode(InetAddress ipAddress, long pubPort)
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

    public InetAddress getInetAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress)
    {
        this.ipAddress = ipAddress;
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
