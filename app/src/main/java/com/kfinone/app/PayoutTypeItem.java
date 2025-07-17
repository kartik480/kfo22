package com.kfinone.app;

public class PayoutTypeItem {
    private String payoutName;
    private String status;

    public PayoutTypeItem(String payoutName, String status) {
        this.payoutName = payoutName;
        this.status = status;
    }

    public String getPayoutName() {
        return payoutName;
    }

    public void setPayoutName(String payoutName) {
        this.payoutName = payoutName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 
