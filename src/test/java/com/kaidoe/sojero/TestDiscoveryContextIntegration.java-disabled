package com.kaidoe.sojero;

import org.junit.Test;

public class TestDiscoveryContextIntegration {

    public Integer replyCount = 0;
    public static int LOOPS = 10;

    public synchronized void incrementCount()
    {
        replyCount++;
    }

    public synchronized Integer getReplyCount()
    {
        return replyCount;
    }

    @Test
    public void testServiceContext() throws InterruptedException {

        ServiceContext server = new ServiceContext();
        ServiceContext client = new ServiceContext();

        Service clientService = client.getService("Number");

        clientService.addEventHandler(new ServiceEventHandler("Data") {
            @Override
            public void onServiceEvent(ServiceMsg theEvent)
            {
                System.out.println("RX Data: " + getReplyCount().toString() + " of " + new String(theEvent.getEventData()));
                assert(getReplyCount().toString().equals(new String(theEvent.getEventData())));
                incrementCount();
            }
        });

        Service serverService = server.getService("Number");

        System.out.println("Wait for services to discover each other");

        Thread.sleep(1000);

        System.out.println("Server found: " + server.countServiceNodes());

        assert( server.countServiceNodes() == 1 );

        System.out.println("Client found: " + client.countServiceNodes());

        assert( client.countServiceNodes() == 1 );

        System.out.println("Triggering Data " + LOOPS + " times");

        for (int i = 0; i < LOOPS; i++) {
            System.out.println("TX Data: " + String.valueOf(i));
            serverService.trigger(
                    serverService.getEventMsg("Data", String.valueOf(i).getBytes()));

        }

        Thread.sleep(500);

        System.out.println(getReplyCount().toString() + " out of " + LOOPS + " messages passed successfully");
        assert(replyCount == LOOPS);


    }

}
