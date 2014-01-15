package com.kaidoe.sojero;

import org.junit.Test;


public class TestServiceInterface {

    @Test
    public void testClass()
    {
        ServiceInterface inf = new ServiceInterface();
        assert(inf instanceof ServiceInterface);
    }

}
