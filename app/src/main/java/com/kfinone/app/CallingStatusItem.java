package com.kfinone.app;

public class CallingStatusItem {
    private int id;
    private String callingStatus;

    public CallingStatusItem(int id, String callingStatus) {
        this.id = id;
        this.callingStatus = callingStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallingStatus() {
        return callingStatus;
    }

    public void setCallingStatus(String callingStatus) {
        this.callingStatus = callingStatus;
    }
} 
