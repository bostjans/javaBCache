package com.stupica.cache;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MemoryBListTest02 {

    private BStoreList    objCache;


    @BeforeEach
    public void setUp() throws Exception {
        objCache = new MemoryBList(3);
        objCache.setShouldDeleteOldest(true);
    }


    @Test
    public void add11() {
        boolean bResult = false;
        long    iCount = 0L;

        // Initialization
        System.out.println("--");
        System.out.println("Test: add11() - " + this.getClass().getName());

        assertNotNull(objCache);
        for (; iCount < 5; iCount++) {
            System.out.println(".. adding element: " + iCount);
            bResult = objCache.add2begin(Long.valueOf(iCount), MemoryBBase.nPERIOD_01min_InMILLIS);
            assertTrue(bResult);
            try { // Pause for X MiliSecond(s)
                Thread.sleep(500);
            } catch (Exception ex) {
                System.err.println("process(): Interrupt exception!!" + " Msg.: " + ex.getMessage());
            }
        }
        assertEquals(3, objCache.size());

        System.out.println(objCache.toStringShort());
    }

}
