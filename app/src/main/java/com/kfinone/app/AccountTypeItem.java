package com.kfinone.app;

public class AccountTypeItem {
    private String name;
    private String status;

    public AccountTypeItem(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 
