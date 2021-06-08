package com.stupica.cache;


import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class MemoryBCache implements BCache {

    private final int   nMAX_COUNT_ELEMENT_DEF = 1000;
    private final int   nPERIOD_RETENTION_SEC_DEF = 60 * 10;    // = 10 min;
    private static final int CLEAN_UP_PERIOD_IN_SEC = 5;

    protected long          nCountOfElementsMax;

    private final ConcurrentHashMap<String, SoftReference<CacheObject>> objCache = new ConcurrentHashMap<>();

    public MemoryBCache() {
        nCountOfElementsMax = nMAX_COUNT_ELEMENT_DEF;
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
    }


    public boolean add(String asKey, Object aobjVal) {
        return add(asKey, aobjVal, nPERIOD_RETENTION_SEC_DEF * 1000);
    }

    private boolean addInternal(String asKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
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

    @Override
    public boolean add(String asKey, Object aobjVal, long aiPeriodInMillis) {
        cleanUp();
        return addInternal(asKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
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


    @Override
    public void remove(String asKey) {
        objCache.remove(asKey);
    }

    private CacheObject getCacheObject(String asKey) {
        CacheObject objInCache = null;
        SoftReference objInCacheR = objCache.get(asKey);

        if (objInCacheR != null)
            objInCache = (CacheObject) objInCacheR.get();
        if (objInCache != null) {
            if (objInCache.isExpired()) return null;
            objInCache.setLastUsedTimeNow();
            objInCache.incCount();
            return objInCache;
        }
        return null;
    }
    @Override
    public Object get(String asKey) {
        //return Optional.ofNullable(objCache.get(asKey)).map(SoftReference::get)
        //        .filter(cacheObject -> !cacheObject.isExpired())
        //        .map(CacheObject::getValue)
        //        .orElse(null);
        cleanUp();

        CacheObject objInCache = getCacheObject(asKey);
        if (objInCache != null) return objInCache.getValue();
        return null;
    }

    @Override
    public void clear() {
        objCache.clear();
    }

    @Override
    public long size() {
        //return objCache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
        //        .map(cacheObject -> !cacheObject.isExpired())
        //        .orElse(false))
        //        .count();
        cleanUp();
        return objCache.size();
    }

    public String toString() {
        String  sReturn;
        boolean bTemp = true;

        sReturn = "(Count: " + size() + "";
        sReturn += "/Max.: " + nCountOfElementsMax + ")";
        sReturn += " (Keys: ";
        for (String sLoop : objCache.keySet()) {
            if (bTemp) bTemp = false;
            else sReturn += "; ";
            sReturn += sLoop;
        }
        sReturn += ")";
        return sReturn;
    }


    protected void cleanUp() {
        List    arrKey = new ArrayList<String>();

        //objCache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
        //        .map(CacheObject::isExpired)
        //        .orElse(false));
        for (String sLoop : objCache.keySet()) {
            CacheObject objInCache = getCacheObject(sLoop);
            if (objInCache != null) {
                if (objInCache.isExpired()) arrKey.add(sLoop);
            }
        }
        if (!arrKey.isEmpty()) {
            for (Object sLoop : arrKey) {
                remove((String) sLoop);
            }
        }
    }


    private static class CacheObject {

        private long addTime;
        private long lastUsedTime;
        private long expiryTime;
        private long countUsed = 0L;
        private Object value;

        public CacheObject(Object aobjVal, long anAddTime, long anExpiryTime) {
            //addTime = System.currentTimeMillis();
            addTime = anAddTime;
            expiryTime = anExpiryTime;
            value = aobjVal;
        }
        public long getAddTime() { return addTime; }
        public long getLastUsedTime() { return lastUsedTime; }
        public long getExpiryTime() { return expiryTime; }
        public long getCountUsed() { return countUsed; }

        public void setExpiryTime(long anTime) { expiryTime = anTime; }
        public void setLastUsedTimeNow() { lastUsedTime = System.currentTimeMillis(); }
        public void incCount() { countUsed++; }

        public Object getValue() { return value; }
        public void setValue(Object aobjVal) { value = aobjVal; }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
