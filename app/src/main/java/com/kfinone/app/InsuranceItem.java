package com.kfinone.app;

public class InsuranceItem {
    private String id;
    private String customerName;
    private String companyName;
    private String mobile;
    private String state;
    private String location;
    private String status;
    private String policyNumber;
    private String policyType;
    private String premiumAmount;
    private String startDate;
    private String endDate;

    public InsuranceItem() {
        // Default constructor
    }

    public InsuranceItem(String id, String customerName, String companyName, String mobile, 
                        String state, String location, String status, String policyNumber,
                        String policyType, String premiumAmount, String startDate, String endDate) {
        this.id = id;
        this.customerName = customerName;
        this.companyName = companyName;
        this.mobile = mobile;
        this.state = state;
        this.location = location;
        this.status = status;
        this.policyNumber = policyNumber;
        this.policyType = policyType;
        this.premiumAmount = premiumAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }

    public String getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(String premiumAmount) { this.premiumAmount = premiumAmount; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    // Helper methods
    public String getDisplayName() {
        if (customerName != null && !customerName.isEmpty()) {
            return customerName;
        }
        return "Unknown Customer";
    }

    public String getFormattedMobile() {
        if (mobile != null && !mobile.isEmpty()) {
            return mobile;
        }
        return "N/A";
    }

    public String getFormattedLocation() {
        if (state != null && location != null) {
            return state + ", " + location;
        } else if (state != null) {
            return state;
        } else if (location != null) {
            return location;
        }
        return "N/A";
    }
}
