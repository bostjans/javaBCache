package com.stupica.cache;


import java.util.*;


public class MemoryBBase {

    protected final int   nMAX_COUNT_ELEMENT_DEF = 10000;
    protected final int   nPERIOD_RETENTION_SEC_DEF = 60 * 10;    // = 10 min;
    //protected static final int CLEAN_UP_PERIOD_IN_SEC = 5;

    protected final long    nCountOfElementsMax2Print = 100;
    protected long          nCountOfElementsMax;

    //protected long          nTsCreated = 0L;
    //protected long          nTsModifiedLast = 0L;
    protected long          nTsCleanUpLast = 0L;
    protected long          nTsCleanUpDeltaMax = 1000L;

    protected Map objCache = null;


    protected boolean addInternal(String asKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
        return false;
    }

    public boolean add(String asKey, Object aobjVal) {
        return add(asKey, aobjVal, nPERIOD_RETENTION_SEC_DEF * 1000);
    }
    //@Override
    public boolean add(String asKey, Object aobjVal, long aiPeriodInMillis) {
        cleanUp();
        return addInternal(asKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    }

    //@Override
    public void remove(String asKey) {
        objCache.remove(asKey);
    }

    protected MemoryBBase.CacheObject getCacheObjectNoCheck(String asKey) {
        MemoryBBase.CacheObject objInCache = (MemoryBBase.CacheObject) objCache.get(asKey);
        return objInCache;
    }
    protected MemoryBBase.CacheObject getCacheObject(String asKey) {
        MemoryBBase.CacheObject objInCache = (MemoryBBase.CacheObject) objCache.get(asKey);
        if (objInCache != null) {
            if (objInCache.isExpired()) return null;
            objInCache.setLastUsedTimeNow();
            objInCache.incCount();
            return objInCache;
        }
        return null;
    }
    //@Override
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

    public Set getKeyAll() {
        cleanUp();
        return objCache.keySet();
    }

    //@Override
    public void clear() {
        objCache.clear();
        nTsCleanUpLast = System.currentTimeMillis();
    }

    //@Override
    public long size() {
        //return objCache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
        //        .map(cacheObject -> !cacheObject.isExpired())
        //        .orElse(false))
        //        .count();
        cleanUp();
        return objCache.size();
    }

    protected <E> void cleanUp() {
        List                    arrKey = new ArrayList<String>();
        Map.Entry<String, E>    objMapEntry = null;
        long                    nTsCleanUpDelta;

        nTsCleanUpDelta = System.currentTimeMillis() - nTsCleanUpLast;
        if (nTsCleanUpDelta < nTsCleanUpDeltaMax) {
            nTsCleanUpLast = System.currentTimeMillis();
            return;
        }
        nTsCleanUpLast = System.currentTimeMillis();
        //objCache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
        //        .map(CacheObject::isExpired)
        //        .orElse(false));
        if (objCache.size() < 1)
            return;
        Iterator<Map.Entry<String, E>> objIt = objCache.entrySet().iterator();
        while (objIt.hasNext()) {
            objMapEntry = objIt.next();
            CacheObject objInCache = getCacheObjectNoCheck(objMapEntry.getKey());
            if (objInCache != null) {
                if (objInCache.isExpired()) arrKey.add(objMapEntry.getKey());
            }
        }
        if (!arrKey.isEmpty()) {
            for (Object sLoop : arrKey) {
                remove((String) sLoop);
            }
        }
    }


    protected static class CacheObject {

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
            //System.out.println("isExpired(): TS: " + System.currentTimeMillis() + "; expiryTime: " + expiryTime);
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
