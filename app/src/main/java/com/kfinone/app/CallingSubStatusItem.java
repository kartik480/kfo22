package com.kfinone.app;

public class CallingSubStatusItem {
    private final int id;
    private final String callingStatus;
    private final String callingSubStatus;
    private final String status;

    public CallingSubStatusItem(int id, String callingStatus, String callingSubStatus, String status) {
        this.id = id;
        this.callingStatus = callingStatus;
        this.callingSubStatus = callingSubStatus;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCallingStatus() {
        return callingStatus;
    }

    public String getCallingSubStatus() {
        return callingSubStatus;
    }

    public String getStatus() {
        return status;
    }
} 
