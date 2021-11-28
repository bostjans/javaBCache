package com.stupica.cache;


import java.util.*;


public class MemoryBList extends MemoryBBase implements BStoreList {

    protected List objCache = null;


    public MemoryBList() {
        nCountOfElementsMax = nMAX_COUNT_ELEMENT_DEF;
        init();
    }
    public MemoryBList(long anCountOfElementsMax) {
        nCountOfElementsMax = anCountOfElementsMax;
        init();
    }


    protected void init() {
        objCache = Collections.synchronizedList(new ArrayList<Object>());
        nTsCleanUpLast = System.currentTimeMillis();
    }
    protected void init(long anCountOfElementsMax) {
        objCache = Collections.synchronizedList(new ArrayList<Object>((int) anCountOfElementsMax));
        nTsCleanUpLast = System.currentTimeMillis();
    }


    protected boolean addInternal(Object aobjVal, long adtValid, long aiPeriodInMillis) {
        return false;
    }

    public boolean add(Object aobjVal) {
        return add(aobjVal, nPERIOD_RETENTION_SEC_DEF * 1000);
    }
    //@Override
    public boolean add(Object aobjVal, long aiPeriodInMillis) {
        cleanUp();
        return addInternal(aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    }

    //@Override
    public void remove(int aiIndex) {
        objCache.remove(aiIndex);
    }

    protected CacheObject getCacheObjectNoCheck(int aiIndex) {
        CacheObject objInCache = null;
        if (aiIndex < objCache.size())
            objInCache = (CacheObject) objCache.get(aiIndex);
        return objInCache;
    }
    protected CacheObject getCacheObject(int aiIndex) {
        CacheObject objInCache = getCacheObjectNoCheck(aiIndex);
        if (objInCache != null) {
            if (objInCache.isExpired()) return null;
            objInCache.setLastUsedTimeNow();
            objInCache.incCount();
            return objInCache;
        }
        return null;
    }
    public Object get(int aiIndex) {
        cleanUp();

        CacheObject objInCache = getCacheObject(aiIndex);
        if (objInCache != null) return objInCache.getValue();
        return null;
    }

    public List getList() {
        return objCache;
    }

    //@Override
    public void clear() {
        objCache.clear();
        nTsCleanUpLast = System.currentTimeMillis();
    }

    public long size() {
        cleanUp();
        return objCache.size();
    }

    protected void cleanUp() {
        List                    arrIndex = new ArrayList<Integer>();
        int                     nIndex;
        long                    nTsCleanUpDelta;

        nIndex = 0;
        nTsCleanUpDelta = System.currentTimeMillis() - nTsCleanUpLast;
        if (nTsCleanUpDelta < nTsCleanUpDeltaMax) {
            nTsCleanUpLast = System.currentTimeMillis();
            return;
        }
        nTsCleanUpLast = System.currentTimeMillis();
        if (objCache.size() < 1)
            return;
        for (Object objLoop : objCache) {
            CacheObject objInCache = (CacheObject)objLoop;
            if (objInCache != null) {
                if (objInCache.isExpired()) arrIndex.add(nIndex);
            }
            nIndex++;
        }
        Collections.sort(arrIndex);
        for (int i = arrIndex.size() - 1; i < arrIndex.size(); i--) {
            Integer objIndex = (Integer) arrIndex.get(i);
            remove(objIndex.intValue());
        }
    }


    public String toStringShort() {
        return toStringWithElem(2);
    }
    public String toStringWithElem(int anCountOfElementsMax2Print) {
        String  sReturn;
        long    iCountPrint = 0;
        long    iCountPrintMax = anCountOfElementsMax2Print;
        boolean bFirstEl = true;

        if (iCountPrintMax > nCountOfElementsMax2Print)
            iCountPrintMax = nCountOfElementsMax2Print;
        sReturn = "(Count: " + size() + "";
        sReturn += "/Max.: " + nCountOfElementsMax + ")";
        sReturn += " (Data: ";
        for (Object objLoop : objCache) {
            CacheObject objInCache = (CacheObject)objLoop;
            iCountPrint++;
            if (bFirstEl) bFirstEl = false;
            else sReturn += "; ";
            if (objInCache != null) {
                sReturn += objInCache.getValue();
                if (iCountPrint > iCountPrintMax) {
                    sReturn += "; ..";
                    break;
                }
            }
        }
        sReturn += ")";
        return sReturn;
    }
}
