package com.stupica.cache;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MemoryBCacheTest01 {

    private MemoryBCache    objCache;


    @BeforeEach
    public void setUp() throws Exception {
        objCache = new MemoryBCache();
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void add11() {
        // Initialization
        System.out.println("--");
        System.out.println("Test: testAdd11() - " + this.getClass().getName());

        System.out.println(".. before add > TS: " + Long.valueOf(System.currentTimeMillis()).toString() + "");
        objCache.add("A", Integer.valueOf(1), 1);
        System.out.println(".. after add > size: " + objCache.size() + " .. sleep ..");
        try { // Pause for X MilliSecond(s)
            Thread.sleep(501);
        } catch (Exception ex) {
            System.err.println("test(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
        }
        System.out.println("TS: " + Long.valueOf(System.currentTimeMillis()).toString() + ".. after add > size: " + objCache.size()
                + "\n\tCache: " + objCache.toString());
        assertEquals(0, objCache.size());
    }
}
