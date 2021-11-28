package com.stupica.cache;

import java.util.concurrent.ConcurrentHashMap;


public class MemoryBStore extends MemoryBMap implements BStore {

    public MemoryBStore() {
        nCountOfElementsMax = nMAX_COUNT_ELEMENT_DEF;
        init();
    }
    public MemoryBStore(long anCountOfElementsMax) {
        nCountOfElementsMax = anCountOfElementsMax;
        init();
    }


    protected void init() {
        objCache = new ConcurrentHashMap<String, CacheObject>();
        nTsCleanUpLast = System.currentTimeMillis();
    }
    protected void init(long anCountOfElementsMax) {
        objCache = new ConcurrentHashMap<String, CacheObject>((int) anCountOfElementsMax);
        nTsCleanUpLast = System.currentTimeMillis();
    }


    protected boolean addInternal(String asKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
        if (asKey == null)
            return false;
        if (objCache.size() >= nCountOfElementsMax)
            return false;

        if (aobjVal == null) {
            objCache.remove(asKey);
        } else {
            long expiryTime = System.currentTimeMillis() + aiPeriodInMillis;
            objCache.put(asKey, new CacheObject(aobjVal, adtValid, expiryTime));
        }
        return true;
    }

    @Override
    public boolean addNotExist(String asKey, Object aobjVal, long aiPeriodInMillis) {
        cleanUp();

        if (asKey == null) {
            return false;
        }
        if (objCache.containsKey(asKey)) {
            return false;
        }
        return addInternal(asKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    }


    public String toString() {
        return toStringWithElem(22);
    }
}
