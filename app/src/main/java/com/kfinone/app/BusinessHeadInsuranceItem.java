package com.kfinone.app;

public class BusinessHeadInsuranceItem {
    private String id;
    private String customerName;
    private String companyName;
    private String mobile;
    private String state;
    private String location;
    private String actions; // Placeholder for action buttons/text

    public BusinessHeadInsuranceItem(String id, String customerName, String companyName, String mobile, String state, String location, String actions) {
        this.id = id;
        this.customerName = customerName;
        this.companyName = companyName;
        this.mobile = mobile;
        this.state = state;
        this.location = location;
        this.actions = actions;
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCompanyName() { return companyName; }
    public String getMobile() { return mobile; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public String getActions() { return actions; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setState(String state) { this.state = state; }
    public void setLocation(String location) { this.location = location; }
    public void setActions(String actions) { this.actions = actions; }
}
