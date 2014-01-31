package com.kaidoe.sojero;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Full API Test
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
                ServiceEvent messageEvent = new ServiceEvent("Message");
                messageEvent.set("Hello World");
                // Trigger an event on the service
                provider.trigger(messageEvent);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

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
        public void onServiceEvent(ServiceEvent event)
        {
            tc.count++;
            String data = event.getData();
            System.out.println("CounterEventHandler: Message " + tc.count + " received");

        }

    }

    class TestConsumer implements Runnable
    {

        public int count = 0;

        @Override
        public void run() {

            System.out.println("TestConsumer starting");

            ServiceContext ctx = new ServiceContext();
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

            assertTrue(count == 10);

        }
    }


    @Test
    public void main() throws InterruptedException {

        TestProvider p = new TestProvider();
        TestConsumer c = new TestConsumer();
        Thread t2 = new Thread(c);
                t2.start();
        Thread t1 = new Thread(p);
        t1.start();
        t2.join();

    }

}
