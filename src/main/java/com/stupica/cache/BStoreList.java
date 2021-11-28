package com.stupica.cache;

import java.util.List;


public interface BStoreList {

    void setCountOfElementsMax(long anCountOfElementsMax);

    boolean add(Object aobjValue, long aiPeriodInMillis);

    void remove(int aiIndex);

    Object get(int aiIndex);
    List getList();

    void clear();

    long size();

    String toStringShort();
}
