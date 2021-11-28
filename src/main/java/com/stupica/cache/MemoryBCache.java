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


    protected void init() {
        objCache = new ConcurrentHashMap<String, SoftReference<CacheObject>>();
        nTsCleanUpLast = System.currentTimeMillis();
    }
    protected void init(long anCountOfElementsMax) {
        objCache = new ConcurrentHashMap<String, SoftReference<CacheObject>>((int) anCountOfElementsMax);
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
            objCache.put(asKey, new SoftReference<>(new CacheObject(aobjVal, adtValid, expiryTime)));
        }
        return true;
    }

    //@Override
    //public boolean add(String asKey, Object aobjVal, long aiPeriodInMillis) {
    //    cleanUp();
    //    return addInternal(asKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    //}
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
    //@Override
    public boolean addIfNewer(String asKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
        cleanUp();

        if (asKey == null) {
            return false;
        }
        CacheObject objInCache = getCacheObject(asKey);
        if (objInCache != null) {
            if (objInCache.getAddTime() < adtValid) {
                return addInternal(asKey, aobjVal, adtValid, aiPeriodInMillis);
            }
        }
        return addInternal(asKey, aobjVal, adtValid, aiPeriodInMillis);
    }


    protected CacheObject getCacheObjectNoCheck(String asKey) {
        CacheObject objInCache = null;
        SoftReference objInCacheR = (SoftReference) objCache.get(asKey);

        if (objInCacheR != null)
            objInCache = (CacheObject) objInCacheR.get();
        return objInCache;
    }
    protected CacheObject getCacheObject(String asKey) {
        CacheObject objInCache = getCacheObjectNoCheck(asKey);

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
