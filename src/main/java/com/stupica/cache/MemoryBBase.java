package com.stupica.cache;


import java.util.Map;
import java.util.Set;


public class MemoryBBase {

    protected final int   nMAX_COUNT_ELEMENT_DEF = 1000;
    protected final int   nPERIOD_RETENTION_SEC_DEF = 60 * 10;    // = 10 min;
    protected static final int CLEAN_UP_PERIOD_IN_SEC = 5;

    protected long          nCountOfElementsMax;

    protected Map objCache = null;
    //protected final ConcurrentHashMap objCache = null;


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

    protected void cleanUp() {
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
