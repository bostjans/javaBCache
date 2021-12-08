package com.stupica.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;


public class MemoryBCache extends MemoryBMap implements BCache {

    public MemoryBCache() {
        nCountOfElementsMax = nMAX_COUNT_ELEMENT_DEF;
        init();
//        Thread cleanerThread = new Thread(() -> {
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * 1000);
//                    cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get).map(CacheObject::isExpired).orElse(false));
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        });
//        cleanerThread.setDaemon(true);
//        cleanerThread.start();
    }
    public MemoryBCache(long anCountOfElementsMax) {
        nCountOfElementsMax = anCountOfElementsMax;
        init(anCountOfElementsMax);
    }


    protected <T> void init() {
        objCache = new ConcurrentHashMap<T, SoftReference<CacheObject>>();
        nTsCleanUpLast = System.currentTimeMillis();
    }
    protected <T> void init(long anCountOfElementsMax) {
        objCache = new ConcurrentHashMap<T, SoftReference<CacheObject>>((int) anCountOfElementsMax);
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
            objCache.put(atKey, new SoftReference<>(new CacheObject(aobjVal, adtValid, expiryTime)));
        }
        return true;
    }

    //@Override
    //public boolean add(String asKey, Object aobjVal, long aiPeriodInMillis) {
    //    cleanUp();
    //    return addInternal(asKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    //}
    @Override
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
    public <T> boolean addIfNewer(T atKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
        cleanUp();

        if (atKey == null) {
            return false;
        }
        CacheObject objInCache = getCacheObject(atKey);
        if (objInCache != null) {
            if (objInCache.getAddTime() < adtValid) {
                return addInternal(atKey, aobjVal, adtValid, aiPeriodInMillis);
            }
        }
        return addInternal(atKey, aobjVal, adtValid, aiPeriodInMillis);
    }


    protected <T> CacheObject getCacheObjectNoCheck(T atKey) {
        CacheObject objInCache = null;
        SoftReference objInCacheR = (SoftReference) objCache.get(atKey);

        if (objInCacheR != null)
            objInCache = (CacheObject) objInCacheR.get();
        return objInCache;
    }
    protected <T> CacheObject getCacheObject(T atKey) {
        CacheObject objInCache = getCacheObjectNoCheck(atKey);

        if (objInCache != null) {
            if (objInCache.isExpired()) return null;
            objInCache.setLastUsedTimeNow();
            objInCache.incCount();
            return objInCache;
        }
        return null;
    }


    public String toString() {
        return toStringWithElem(22);
    }
}
