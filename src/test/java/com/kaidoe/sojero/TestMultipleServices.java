package com.kaidoe.sojero;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Basic Provider / Consumer Test
 */

public class TestMultipleServices
{

    class TestProvider implements Runnable
    {

        private String msg;

        public TestProvider(String msg) {
            super();

            this.msg = msg;

        }

        @Override
        public void run() {

            System.out.println("TestProvider starting");

            ServiceContext ctx = new ServiceContext();

            Service provider = ctx.getService(msg);

            // basic events
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i=1; i<11; i++)
            {

                System.out.println("TestProvider triggering message " + i);

                //ServiceMsg messageEvent = provider.getEventMsg("Message", "Hello World".getBytes());

                // Trigger an event on the service
                provider.trigger("Message", msg.getBytes());

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            System.out.println("TestProvider closing");

            ctx.close();

            System.out.println("TestProvider closed");

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
        public void onServiceMsg(ServiceMsg event)
        {
            tc.count++;
            String data = null;
            try {
                data = new String(event.getMsgData(), "UTF-8");
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

            Service p1 = ctx.getService("P1");
            Service p2 = ctx.getService("P2");
            Service p3 = ctx.getService("P3");

            p1.addHandler(new CounterEventHandler("Message", this));
            p2.addHandler(new CounterEventHandler("Message", this));
            p3.addHandler(new CounterEventHandler("Message", this));

            while (!Thread.currentThread().isInterrupted())
            {
                try {
                    Thread.sleep(100);
                    //System.out.println("TestConsumer sleep");

                } catch (InterruptedException e) {
                    // reset interrupt flag
                    break;
                }
            }

            System.out.println("TestConsumer " + count + " messages recieved");

            System.out.println("TestConsumer closing");

            ctx.close();

            System.out.println("TestConsumer closed");

            //assertTrue(count == 10);

        }
    }

    @Test
    public void main() throws InterruptedException {

        TestProvider p1 = new TestProvider("P1");
        TestProvider p2 = new TestProvider("P2");
        TestConsumer c = new TestConsumer();

        Thread t1 = new Thread(c, "Consumer");
        t1.start();
        Thread t2 = new Thread(p1, "Provider1");
        t2.start();
        Thread t3 = new Thread(p2, "Provider2");
        t3.start();

        t3.join();
        t2.join();

        TestProvider p3 = new TestProvider("P3");
        Thread t4 = new Thread(p3, "Provider3");
        t4.start();
        t4.join();

        t2.interrupt();
        t2.join();

    }

}
