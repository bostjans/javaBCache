package com.stupica.cache;


import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MemoryBCache extends MemoryBBase implements BCache {

    //private final ConcurrentHashMap<String, SoftReference<CacheObject>> objCache = new ConcurrentHashMap<>();


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
        init();
    }


    protected void init() {
        objCache = new ConcurrentHashMap<String, SoftReference<CacheObject>>();
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


    //@Override
    //public void remove(String asKey) {
    //    objCache.remove(asKey);
    //}

    protected CacheObject getCacheObject(String asKey) {
        CacheObject objInCache = null;
        SoftReference objInCacheR = (SoftReference) objCache.get(asKey);

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
    //@Override
    //public Object get(String asKey) {
        //return Optional.ofNullable(objCache.get(asKey)).map(SoftReference::get)
        //        .filter(cacheObject -> !cacheObject.isExpired())
        //        .map(CacheObject::getValue)
        //        .orElse(null);
    //    cleanUp();

    //    CacheObject objInCache = getCacheObject(asKey);
    //    if (objInCache != null) return objInCache.getValue();
    //    return null;
    //}

    //@Override
    //public void clear() {
    //    objCache.clear();
    //}

    //@Override
    //public long size() {
        //return objCache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
        //        .map(cacheObject -> !cacheObject.isExpired())
        //        .orElse(false))
        //        .count();
    //    cleanUp();
    //    return objCache.size();
    //}

    public String toString() {
        String  sReturn;
        boolean bTemp = true;
        Map.Entry<String, SoftReference<CacheObject>> objMapEntry = null;

        sReturn = "(Count: " + size() + "";
        sReturn += "/Max.: " + nCountOfElementsMax + ")";
        sReturn += " (Keys: ";
        Iterator<Map.Entry<String, SoftReference<CacheObject>>> objIt = objCache.entrySet().iterator();
        while (objIt.hasNext()) {
        //for (String sLoop : objCache.keySet()) {
            objMapEntry = objIt.next();
            //i += pair.getKey() + pair.getValue();
            if (bTemp) bTemp = false;
            else sReturn += "; ";
            sReturn += objMapEntry.getKey();
        }
        sReturn += ")";
        return sReturn;
    }


    protected void cleanUp() {
        List    arrKey = new ArrayList<String>();
        Map.Entry<String, SoftReference<CacheObject>> objMapEntry = null;

        //objCache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
        //        .map(CacheObject::isExpired)
        //        .orElse(false));
        if (objCache.size() < 1)
            return;
        Iterator<Map.Entry<String, SoftReference<CacheObject>>> objIt = objCache.entrySet().iterator();
        while (objIt.hasNext()) {
        //for (String sLoop : objCache.keySet()) {
            objMapEntry = objIt.next();
            CacheObject objInCache = getCacheObject(objMapEntry.getKey());
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
}
