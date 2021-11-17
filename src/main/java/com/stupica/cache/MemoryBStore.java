package com.stupica.cache;


import java.util.concurrent.ConcurrentHashMap;


public class MemoryBStore extends MemoryBBase implements BStore {

    public MemoryBStore() {
        nCountOfElementsMax = nMAX_COUNT_ELEMENT_DEF;
        init();
    }
    public MemoryBStore(long anCountOfElementsMax) {
        nCountOfElementsMax = anCountOfElementsMax;
        init();
    }


    protected void init() {
        objCache = new ConcurrentHashMap<String, MemoryBBase.CacheObject>();
        nTsCleanUpLast = System.currentTimeMillis();
    }
    protected void init(long anCountOfElementsMax) {
        objCache = new ConcurrentHashMap<String, MemoryBBase.CacheObject>((int) anCountOfElementsMax);
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
            objCache.put(asKey, new MemoryBBase.CacheObject(aobjVal, adtValid, expiryTime));
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
//        String  sReturn;
//        long    iCountPrint = 0;
//        boolean bFirstEl = true;
//        Map.Entry<String, MemoryBBase.CacheObject> objMapEntry = null;
//
//        sReturn = "(Count: " + size() + "";
//        sReturn += "/Max.: " + nCountOfElementsMax + ")";
//        sReturn += " (Keys: ";
//        Iterator<Map.Entry<String, MemoryBBase.CacheObject>> objIt = objCache.entrySet().iterator();
//        while (objIt.hasNext()) {
//            objMapEntry = objIt.next();
//            iCountPrint++;
//            if (bFirstEl) bFirstEl = false;
//            else sReturn += "; ";
//            sReturn += objMapEntry.getKey();
//            if (iCountPrint > nCountOfElementsMax2Print) {
//                sReturn += "; ..";
//                break;
//            }
//        }
//        sReturn += ")";
//        return sReturn;
        return toStringWithElem(22);
    }


//    protected void cleanUp() {
//        List arrKey = new ArrayList<String>();
//        Map.Entry<String, MemoryBBase.CacheObject> objMapEntry = null;
//
//        //System.out.println("cleanUp(): Start ..  -  size: " + objCache.size());
//        Iterator<Map.Entry<String, MemoryBBase.CacheObject>> objIt = objCache.entrySet().iterator();
//        while (objIt.hasNext()) {
//            objMapEntry = objIt.next();
//            MemoryBBase.CacheObject objInCache = getCacheObjectNoCheck(objMapEntry.getKey());
//            if (objInCache != null) {
//                if (objInCache.isExpired()) arrKey.add(objMapEntry.getKey());
//            }
//        }
//        if (!arrKey.isEmpty()) {
//            for (Object sLoop : arrKey) {
//                //System.out.println("cleanUp(): remove: " + sLoop);
//                remove((String) sLoop);
//            }
//        }
//    }
}
