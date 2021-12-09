package com.stupica.cache;


public class MemoryBBase {

    public final int      nPERIOD_05min_InMILLIS = 1000 * 60 * 05;
    public final int      nPERIOD_10min_InMILLIS = 1000 * 60 * 10;
    public final int      nPERIOD_20min_InMILLIS = 1000 * 60 * 20;

    protected final int   nMAX_COUNT_ELEMENT_DEF = 10000;
    protected final int   nPERIOD_RETENTION_SEC_DEF = 60 * 10;    // = 10 min;
    //protected static final int CLEAN_UP_PERIOD_IN_SEC = 5;

    protected final long    nCountOfElementsMax2Print = 100;
    protected long          nCountOfElementsMax;

    //protected long          nTsCreated = 0L;
    //protected long          nTsModifiedLast = 0L;
    protected long          nTsCleanUpLast = 0L;
    protected long          nTsCleanUpDeltaMax = 1000L;


    public void setCountOfElementsMax(long anCountOfElementsMax) {
        nCountOfElementsMax = anCountOfElementsMax;
    }
}
