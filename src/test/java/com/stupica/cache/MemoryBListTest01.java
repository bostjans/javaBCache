package com.stupica.cache;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class MemoryBListTest01 {

    private BStoreList    objCache;


    @BeforeEach
    public void setUp() throws Exception {
        objCache = new MemoryBList();
    }


    @Test
    public void add11() {
        // Initialization
        System.out.println("--");
        System.out.println("Test: addThread11() - " + this.getClass().getName());

        assertNotNull(objCache);
        objCache.add(Long.valueOf(1L), MemoryBBase.nPERIOD_05sec_InMILLIS);
        assertEquals(1, objCache.size());
    }

    @Test
    public void add15() {
        long    iCount = 0L;

        // Initialization
        System.out.println("--");
        System.out.println("Test: addThread11() - " + this.getClass().getName());

        assertNotNull(objCache);
        for (; iCount < 5; iCount++) {
            System.out.println(".. adding element: " + iCount);
            objCache.add(Long.valueOf(iCount), MemoryBBase.nPERIOD_01min_InMILLIS);
            try { // Pause for X MiliSecond(s)
                Thread.sleep(500);
            } catch (Exception ex) {
                System.err.println("process(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
            }
        }
        assertEquals(5, objCache.size());

        System.out.println(objCache.toStringShort());
    }

    @Test
    public void add16() {
        long    iCount = 0L;

        // Initialization
        System.out.println("--");
        System.out.println("Test: addThread11() - " + this.getClass().getName());

        assertNotNull(objCache);
        for (; iCount < 5; iCount++) {
            System.out.println(".. adding element: " + iCount);
            objCache.add2begin(Long.valueOf(iCount), MemoryBBase.nPERIOD_01min_InMILLIS);
            try { // Pause for X MiliSecond(s)
                Thread.sleep(500);
            } catch (Exception ex) {
                System.err.println("process(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
            }
        }
        assertEquals(5, objCache.size());

        System.out.println(objCache.toStringShort());
    }
}
