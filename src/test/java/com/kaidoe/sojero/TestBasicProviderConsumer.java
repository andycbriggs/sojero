package com.kaidoe.sojero;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Basic Provider / Consumer Test
 */

public class TestBasicProviderConsumer
{

    class TestProvider implements Runnable
    {

        @Override
        public void run() {

            System.out.println("TestProvider starting");

            ServiceContext ctx = new ServiceContext();

            Service provider = ctx.getService("Provider");

            // basic events
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i=1; i<11; i++)
            {

                System.out.println("TestProvider triggering message " + i);

                ServiceMsg messageEvent = provider.getEventMsg("Message", "Hello World".getBytes());

                // Trigger an event on the service
                provider.trigger(messageEvent);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            ctx.close();

        }
    }

    class CounterEventHandler extends ServiceEventHandler
    {

        TestConsumer tc;

        public CounterEventHandler(String theEventID, TestConsumer theTC) {
            super(theEventID);
            tc = theTC;
        }

        @Override
        public void onServiceEvent(ServiceMsg event)
        {
            tc.count++;
            String data = null;
            try {
                data = new String(event.getEventData(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("CounterEventHandler: Message " + tc.count + " received - " + data);

        }

    }

    class TestConsumer implements Runnable
    {

        public int count = 0;

        @Override
        public void run() {

            System.out.println("TestConsumer starting");

            ServiceContext ctx = new ServiceContext();

            ctx.registerNode("tcp://127.0.0.1:14000");

            Service provider = ctx.getService("Provider");

            provider.addEventHandler(new CounterEventHandler("Message", this));

            while ((!Thread.currentThread ().isInterrupted ()) &&
                    count < 10)
            {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("TestConsumer " + count + " messages recieved");

            ctx.close();

            assertTrue(count == 10);

        }
    }


    @Test
    public void main() throws InterruptedException {

        TestProvider p = new TestProvider();
        TestConsumer c = new TestConsumer();
        Thread t2 = new Thread(c, "Consumer");
                t2.start();
        Thread t1 = new Thread(p, "Provider");
        t1.start();
        t1.join();
        t2.join();

    }

}
