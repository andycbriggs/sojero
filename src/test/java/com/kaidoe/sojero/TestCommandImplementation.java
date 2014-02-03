package com.kaidoe.sojero;

import org.junit.Test;

public class TestCommandImplementation {

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

        Service clientService = client.getService("Counter");

        clientService.addCommandHandler(new ServiceCommandHandler("Increment") {
            @Override
            public void onServiceCommand(ServiceMsg theCommand) {
                System.out.println("RX: Command " +
                        new String(theCommand.getMsgData()) +
                " received");
                incrementCount();
            }
        });

        Service serverService = server.getService("Counter");

        System.out.println("Wait for services to discover each other");

        Thread.sleep(1000);

        System.out.println("Server found: " + server.countServiceNodes());

        assert( server.countServiceNodes() == 1 );

        System.out.println("Client found: " + client.countServiceNodes());

        assert( client.countServiceNodes() == 1 );

        System.out.println("Triggering Data " + LOOPS + " times");

        for (int i = 0; i < LOOPS; i++) {
            System.out.println("TX Command: " + String.valueOf(i));
            serverService.execute(
                    serverService.getCommandMsg("Increment", String.valueOf(i).getBytes()));

        }

        Thread.sleep(500);

        assert(getReplyCount() == LOOPS);

        System.out.println(getReplyCount().toString() + " out of " + LOOPS + " commands received successfully"); 


    }

}