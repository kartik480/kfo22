package com.kfinone.app;

public class Portfolio {
    private String id;
    private String customerName;
    private String companyName;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String emailId;
    private String state;
    private String location;
    private String subLocation;
    private String pinCode;
    private String customerType;
    private String industryType;
    private String businessType;
    private String birthDate;
    private String address;
    private String createdBy;
    private String status;
    private String createdAt;
    private String updatedAt;

    public Portfolio(String id, String customerName, String companyName, String phoneNumber, 
                    String alternativePhoneNumber, String emailId, String state, String location,
                    String subLocation, String pinCode, String customerType, String industryType,
                    String businessType, String birthDate, String address, String createdBy, 
                    String status, String createdAt, String updatedAt) {
        this.id = id;
        this.customerName = customerName;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.alternativePhoneNumber = alternativePhoneNumber;
        this.emailId = emailId;
        this.state = state;
        this.location = location;
        this.subLocation = subLocation;
        this.pinCode = pinCode;
        this.customerType = customerType;
        this.industryType = industryType;
        this.businessType = businessType;
        this.birthDate = birthDate;
        this.address = address;
        this.createdBy = createdBy;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCompanyName() { return companyName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
    public String getEmailId() { return emailId; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public String getSubLocation() { return subLocation; }
    public String getPinCode() { return pinCode; }
    public String getCustomerType() { return customerType; }
    public String getIndustryType() { return industryType; }
    public String getBusinessType() { return businessType; }
    public String getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public String getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAlternativePhoneNumber(String alternativePhoneNumber) { this.alternativePhoneNumber = alternativePhoneNumber; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setState(String state) { this.state = state; }
    public void setLocation(String location) { this.location = location; }
    public void setSubLocation(String subLocation) { this.subLocation = subLocation; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
    public void setIndustryType(String industryType) { this.industryType = industryType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setAddress(String address) { this.address = address; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
