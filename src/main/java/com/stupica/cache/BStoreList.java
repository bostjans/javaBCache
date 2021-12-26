package com.stupica.cache;

import java.util.Collection;
import java.util.List;


public interface BStoreList {

    void setCountOfElementsMax(long anCountOfElementsMax);
    void setShouldDeleteOldest(boolean abVal);

    boolean add(Object aobjValue, long aiPeriodInMillis);
    boolean add2begin(Object aobjValue, long aiPeriodInMillis);
    boolean addAll(Collection aarrElement, long aiPeriodInMillis);

    void remove(int aiIndex);

    Object get(int aiIndex);
    List getList();

    void clear();

    long size();

    String toStringShort();
}
