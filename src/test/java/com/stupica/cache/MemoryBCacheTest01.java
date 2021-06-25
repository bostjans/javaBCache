package com.stupica.cache;


import static org.junit.Assert.assertEquals;


public class MemoryBCacheTest01 {

    private MemoryBCache    objCache;


    @org.junit.Before
    public void setUp() throws Exception {
        objCache = new MemoryBCache();
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void add11() {
        // Initialization
        System.out.println("--");
        System.out.println("Test: testAdd11() - " + this.getClass().getName());

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
