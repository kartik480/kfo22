package com.kfinone.app;

public class User {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String emailId;
    private String mobile;
    private String employeeNo;
    private String designationName;
    private String departmentName;
    private String status;

    public User() {
    }

    public User(String id, String username, String firstName, String lastName, String fullName, 
                String emailId, String mobile, String employeeNo, String designationName, 
                String departmentName, String status) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.emailId = emailId;
        this.mobile = mobile;
        this.employeeNo = employeeNo;
        this.designationName = designationName;
        this.departmentName = departmentName;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public String getEmailId() { return emailId; }
    public String getMobile() { return mobile; }
    public String getEmployeeNo() { return employeeNo; }
    public String getDesignationName() { return designationName; }
    public String getDepartmentName() { return departmentName; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public void setDesignationName(String designationName) { this.designationName = designationName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return fullName + " (" + designationName + ")";
    }
} 