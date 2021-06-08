package com.stupica.cache;


import static org.junit.Assert.*;


public class MemoryBCacheTest {

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

        objCache.add("A", Integer.valueOf(1));
        System.out.println(".. after add > size: " + objCache.size());
        assertEquals(1, objCache.size());
    }
    @org.junit.Test
    public void add12() {
        // Initialization
        System.out.println("--");
        System.out.println("Test: testAdd11() - " + this.getClass().getName());

        objCache.add("A", Integer.valueOf(1));
        objCache.add("A", Integer.valueOf(2));
        System.out.println(".. after add > size: " + objCache.size());
        System.out.println(".. value: " + (Integer) objCache.get("A"));
        assertEquals(1, objCache.size());
    }

    @org.junit.Test
    public void clear() {
        // Initialization
        System.out.println("--");
        System.out.println("Test: testClear11() - " + this.getClass().getName());

        objCache.add("A", Integer.valueOf(1));
        objCache.clear();
        System.out.println(".. after clear > size: " + objCache.size());
        assertEquals(0, objCache.size());
    }

    @org.junit.Test
    public void size() {
        // Initialization
        System.out.println("--");
        System.out.println("Test: testSize11() - " + this.getClass().getName());

        System.out.println(".. before any operation > size: " + objCache.size() + "\n\t" + objCache.toString());
        objCache.add("A", Integer.valueOf(1));
        System.out.println(".. after add > size: " + objCache.size() + "\n\t" + objCache.toString());
        assertEquals(1, objCache.size());
    }
}