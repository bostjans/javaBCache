package com.stupica.cache;

import java.util.concurrent.ConcurrentHashMap;


public class MemoryBStore extends MemoryBMap implements BCache {

    public MemoryBStore() {
        nCountOfElementsMax = nMAX_COUNT_ELEMENT_DEF;
        init();
    }
    public MemoryBStore(long anCountOfElementsMax) {
        nCountOfElementsMax = anCountOfElementsMax;
        init();
    }


    protected <T> void init() {
        objCache = new ConcurrentHashMap<T, CacheObject>();
        nTsCleanUpLast = System.currentTimeMillis();
    }
    protected <T> void init(long anCountOfElementsMax) {
        objCache = new ConcurrentHashMap<T, CacheObject>((int) anCountOfElementsMax);
        nTsCleanUpLast = System.currentTimeMillis();
    }


    protected <T> boolean addInternal(T atKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
        if (atKey == null)
            return false;
        if (objCache.size() >= nCountOfElementsMax)
            return false;

        if (aobjVal == null) {
            objCache.remove(atKey);
        } else {
            long expiryTime = System.currentTimeMillis() + aiPeriodInMillis;
            objCache.put(atKey, new CacheObject(aobjVal, adtValid, expiryTime));
        }
        return true;
    }

    public <T> boolean addNotExist(T atKey, Object aobjVal, long aiPeriodInMillis) {
        cleanUp();

        if (atKey == null) {
            return false;
        }
        if (objCache.containsKey(atKey)) {
            return false;
        }
        return addInternal(atKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    }


    public String toString() {
        return toStringWithElem(22);
    }
}
