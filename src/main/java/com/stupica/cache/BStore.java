package com.stupica.cache;


public interface BStore {

    void setCountOfElementsMax(long anCountOfElementsMax);

    boolean add(String asKey, Object aobjValue, long aiPeriodInMillis);
    boolean addNotExist(String asKey, Object aobjValue, long aiPeriodInMillis);

    void remove(String asKey);

    Object get(String asKey);

    void clear();

    long size();
}
