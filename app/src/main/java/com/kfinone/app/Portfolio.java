package com.kfinone.app;

public class Portfolio {
    private String id;
    private String customerName;
    private String companyName;
    private String mobile;
    private String state;
    private String location;
    private String createdBy;
    private String status;
    private String email;
    private String address;
    private String createdDate;
    private String updatedDate;

    public Portfolio(String id, String customerName, String companyName, String mobile, 
                    String state, String location, String createdBy, String status,
                    String email, String address, String createdDate, String updatedDate) {
        this.id = id;
        this.customerName = customerName;
        this.companyName = companyName;
        this.mobile = mobile;
        this.state = state;
        this.location = location;
        this.createdBy = createdBy;
        this.status = status;
        this.email = email;
        this.address = address;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCompanyName() { return companyName; }
    public String getMobile() { return mobile; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public String getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getCreatedDate() { return createdDate; }
    public String getUpdatedDate() { return updatedDate; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setState(String state) { this.state = state; }
    public void setLocation(String location) { this.location = location; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setStatus(String status) { this.status = status; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
}
