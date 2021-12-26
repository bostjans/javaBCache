package com.stupica.cache;


public class CacheObject {

    private long addTime;
    private long lastUsedTime;
    private long expiryTime;
    private long countUsed = 0L;
    private Object value;

    public CacheObject(Object aobjVal, long anAddTime, long anExpiryTime) {
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
        return System.currentTimeMillis() > expiryTime;
    }
}
