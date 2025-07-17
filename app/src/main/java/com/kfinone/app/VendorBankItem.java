package com.kfinone.app;

public class VendorBankItem {
    private String name;
    private String status;

    public VendorBankItem(String name, String status) {
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
