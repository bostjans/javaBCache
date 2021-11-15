package com.stupica.cache;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MemoryBCacheTest02 {

    //private long aiPeriodInMillis;
    private MemoryBCache    objCache;


    @BeforeEach
    public void setUp() {
        objCache = new MemoryBCache();
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void add11() {
        boolean bResult = false;

        // Initialization
        System.out.println("--");
        System.out.println("Test: testAdd11() - " + this.getClass().getName());

        System.out.println(".. before add > TS: " + Long.valueOf(System.currentTimeMillis()).toString() + "");
        for (long i = 1L; i < 100000; i++) {
            bResult = objCache.add(Long.valueOf(i).toString(), Integer.valueOf(1));
            if (!bResult) {
                System.out.println("Fail to add! .. after add: " + i + " > size: " + objCache.size());
                break;
            }
            if ((i % 1000) == 0)
                System.out.println(".. after add: " + i + " > size: " + objCache.size());
        }
        System.out.println("TS: " + Long.valueOf(System.currentTimeMillis()).toString() + ".. after add > size: " + objCache.size()
                + "\n\tCache: " + objCache.toString());
        //assertEquals(0, objCache.size());
    }
}
