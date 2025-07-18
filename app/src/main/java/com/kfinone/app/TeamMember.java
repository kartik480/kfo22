package com.kfinone.app;

public class TeamMember {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String designation;
    private String email;
    private String mobile;
    private String employeeNo;
    private String managerName;
    private String status;

    // Default constructor
    public TeamMember() {
    }

    // Constructor with parameters
    public TeamMember(String id, String firstName, String lastName, String designation, 
                     String email, String mobile, String employeeNo, String managerName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.designation = designation;
        this.email = email;
        this.mobile = mobile;
        this.employeeNo = employeeNo;
        this.managerName = managerName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", designation='" + designation + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", employeeNo='" + employeeNo + '\'' +
                ", managerName='" + managerName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
} 