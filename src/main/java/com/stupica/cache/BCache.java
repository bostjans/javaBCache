package com.stupica.cache;


public interface BCache {

    void setCountOfElementsMax(long anCountOfElementsMax);

    <T> boolean add(T atKey, Object aobjValue, long aiPeriodInMillis);
    <T> boolean addNotExist(T atKey, Object aobjValue, long aiPeriodInMillis);

    <T> void remove(T atKey);

    <T> Object get(T atKey);

    void clear();

    long size();

    String toStringShort();
}
