package com.kfinone.app;

public class SalariedItem {
    private String id;
    private String mobileNumber;
    private String leadName;
    private String emailId;
    private String createdBy;
    private String status;
    private String createdAt;
    private String updatedAt;

    public SalariedItem(String id, String mobileNumber, String leadName, String emailId, String createdBy, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.leadName = leadName;
        this.emailId = emailId;
        this.createdBy = createdBy;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getMobileNumber() { return mobileNumber; }
    public String getLeadName() { return leadName; }
    public String getEmailId() { return emailId; }
    public String getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public void setLeadName(String leadName) { this.leadName = leadName; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
