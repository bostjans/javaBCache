package com.stupica.cache;


import java.util.*;


public class MemoryBMap extends MemoryBBase{

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

    protected CacheObject getCacheObjectNoCheck(String asKey) {
        return (CacheObject) objCache.get(asKey);
    }
    protected CacheObject getCacheObject(String asKey) {
        CacheObject objInCache = (CacheObject) objCache.get(asKey);
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


    public String toStringShort() {
        return toStringWithElem(2);
    }
    public <E> String toStringWithElem(int anCountOfElementsMax2Print) {
        String  sReturn;
        long    iCountPrint = 0;
        long    iCountPrintMax = anCountOfElementsMax2Print;
        boolean bFirstEl = true;
        Map.Entry<String, E> objMapEntry = null;

        if (iCountPrintMax > nCountOfElementsMax2Print)
            iCountPrintMax = nCountOfElementsMax2Print;
        sReturn = "(Count: " + size() + "";
        sReturn += "/Max.: " + nCountOfElementsMax + ")";
        sReturn += " (Keys: ";
        Iterator<Map.Entry<String, E>> objIt = objCache.entrySet().iterator();
        while (objIt.hasNext()) {
            objMapEntry = objIt.next();
            iCountPrint++;
            //i += pair.getKey() + pair.getValue();
            if (bFirstEl) bFirstEl = false;
            else sReturn += "; ";
            sReturn += objMapEntry.getKey();
            if (iCountPrint > iCountPrintMax) {
                sReturn += "; ..";
                break;
            }
        }
        sReturn += ")";
        return sReturn;
    }


//    protected static class CacheObject {
//
//        private long addTime;
//        private long lastUsedTime;
//        private long expiryTime;
//        private long countUsed = 0L;
//        private Object value;
//
//        public CacheObject(Object aobjVal, long anAddTime, long anExpiryTime) {
//            //addTime = System.currentTimeMillis();
//            addTime = anAddTime;
//            expiryTime = anExpiryTime;
//            value = aobjVal;
//        }
//        public long getAddTime() { return addTime; }
//        public long getLastUsedTime() { return lastUsedTime; }
//        public long getExpiryTime() { return expiryTime; }
//        public long getCountUsed() { return countUsed; }
//
//        public void setExpiryTime(long anTime) { expiryTime = anTime; }
//        public void setLastUsedTimeNow() { lastUsedTime = System.currentTimeMillis(); }
//        public void incCount() { countUsed++; }
//
//        public Object getValue() { return value; }
//        public void setValue(Object aobjVal) { value = aobjVal; }
//
//        boolean isExpired() {
//            //System.out.println("isExpired(): TS: " + System.currentTimeMillis() + "; expiryTime: " + expiryTime);
//            return System.currentTimeMillis() > expiryTime;
//        }
//    }
}