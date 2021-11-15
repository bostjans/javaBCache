package com.stupica.cache;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class MemoryBCacheTest03 {

    //private long aiPeriodInMillis;
    private final long      aiNumberOfElementMax = 10000000;
    private MemoryBCache    objCache;


    @BeforeEach
    public void setUp() throws Exception {
        //aiPeriodInMillis = 1000 * 60 * 10; // 10 min;
        objCache = new MemoryBCache(aiNumberOfElementMax);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * https://ria101.wordpress.com/2011/12/12/concurrenthashmap-avoid-a-common-misuse/
     * https://www.fatalerrors.org/a/0N581Do.html
     */
    @DisplayName("Add large number of Objects - #11")
    @Test
    public void add11() {
        boolean bResult = false;
        long    dtStart, dtStartLoop;

        // Initialization
        System.out.println("--");
        System.out.println("Test: testAdd11() - " + this.getClass().getName());

        dtStart = System.currentTimeMillis();
        dtStartLoop = dtStart;
        System.out.println(".. before add > TS: " + Long.valueOf(dtStart).toString() + "");
        for (long i = 1L; i < aiNumberOfElementMax + 1; i++) {
            bResult = objCache.add(Long.valueOf(i).toString(), "String" + Long.valueOf(i).toString());
            if (!bResult) {
                System.out.println("Fail to add! .. after add: " + i + " > size: " + objCache.size());
                break;
            }
            if ((i % 10000) == 0) {
                System.out.println(".. after add: " + i + " > size: " + objCache.size()
                        + "; elapse(ms): " + (System.currentTimeMillis() - dtStartLoop));
                dtStartLoop = System.currentTimeMillis();
            }
        }
        System.out.println("TS: " + Long.valueOf(System.currentTimeMillis()).toString() + ".. after add > size: " + objCache.size()
                + "\n\telapse(ms): " + (System.currentTimeMillis() - dtStart)
                + "\n\tCache: " + objCache.toString());
        //assertEquals(0, objCache.size());
    }
}
