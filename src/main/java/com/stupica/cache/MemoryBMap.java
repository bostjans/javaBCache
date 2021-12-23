package com.stupica.cache;

import java.util.*;


public class MemoryBMap extends MemoryBBase {

    protected Map objCache = null;


    protected <T> boolean addInternal(T atKey, Object aobjVal, long adtValid, long aiPeriodInMillis) {
        return false;
    }

    public <T> boolean add(T atKey, Object aobjVal) {
        return add(atKey, aobjVal, nPERIOD_RETENTION_SEC_DEF * 1000);
    }
    //@Override
    public <T> boolean add(T atKey, Object aobjVal, long aiPeriodInMillis) {
        cleanUp();
        return addInternal(atKey, aobjVal, System.currentTimeMillis(), aiPeriodInMillis);
    }

    //@Override
    public <T> void remove(T atKey) {
        objCache.remove(atKey);
    }

    protected <T> CacheObject getCacheObjectNoCheck(T atKey) {
        return (CacheObject) objCache.get(atKey);
    }
    protected <T> CacheObject getCacheObject(T atKey) {
        CacheObject objInCache = (CacheObject) objCache.get(atKey);
        if (objInCache != null) {
            if (objInCache.isExpired()) return null;
            objInCache.setLastUsedTimeNow();
            objInCache.incCount();
            return objInCache;
        }
        return null;
    }
    public <T> Object get(T atKey) {
        //return Optional.ofNullable(objCache.get(asKey)).map(SoftReference::get)
        //        .filter(cacheObject -> !cacheObject.isExpired())
        //        .map(CacheObject::getValue)
        //        .orElse(null);
        cleanUp();

        if (atKey == null)
            return null;
        CacheObject objInCache = getCacheObject(atKey);
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

    protected <K, V> void cleanUp() {
        List            arrKey = new ArrayList<K>();
        Map.Entry<K, V> objMapEntry = null;
        long            nTsCleanUpDelta;

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
        Iterator<Map.Entry<K, V>> objIt = objCache.entrySet().iterator();
        while (objIt.hasNext()) {
            objMapEntry = objIt.next();
            CacheObject objInCache = getCacheObjectNoCheck(objMapEntry.getKey());
            if (objInCache != null) {
                if (objInCache.isExpired()) arrKey.add(objMapEntry.getKey());
            }
        }
        if (!arrKey.isEmpty()) {
            for (Object objLoop : arrKey) {
                remove((K) objLoop);
            }
        }
    }


    public String toStringShort() {
        return toStringWithElem(3);
    }
    public <K, V> String toStringWithElem(int anCountOfElementsMax2Print) {
        StringBuilder   sReturn = new StringBuilder();
        long            iCountPrint = 0;
        long            iCountPrintMax = anCountOfElementsMax2Print;
        boolean         bFirstEl = true;
        Map.Entry<K, V> objMapEntry = null;

        if (iCountPrintMax > nCountOfElementsMax2Print)
            iCountPrintMax = nCountOfElementsMax2Print;
        sReturn.append("(Count: ").append(size());
        sReturn.append("/Max.: ").append(nCountOfElementsMax).append(")");
        sReturn.append(" (Keys: ");
        Iterator<Map.Entry<K, V>> objIt = objCache.entrySet().iterator();
        while (objIt.hasNext()) {
            objMapEntry = objIt.next();
            iCountPrint++;
            if (bFirstEl) bFirstEl = false;
            else sReturn.append("; ");
            sReturn.append(objMapEntry.getKey().toString());
            if (iCountPrint > iCountPrintMax) {
                sReturn.append("; ..");
                break;
            }
        }
        sReturn.append(")");
        return sReturn.toString();
    }
}
