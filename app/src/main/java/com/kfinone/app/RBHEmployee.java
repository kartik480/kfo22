package com.kfinone.app;

public class RBHEmployee {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String employeeId;
    private String dob;
    private String joiningDate;
    private String status;
    private String reportingTo;
    private String officialPhone;
    private String officialEmail;
    private String workState;
    private String workLocation;
    private String aliasName;
    private String residentialAddress;
    private String officeAddress;
    private String panNumber;
    private String aadhaarNumber;
    private String alternativeMobile;
    private String companyName;
    private String lastWorkingDate;
    private String leavingReason;
    private String reJoiningDate;
    private String createdAt;
    private String updatedAt;
    private String designation;
    private String department;
    private String branchStateName;
    private String branchLocationName;

    // Constructor
    public RBHEmployee() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportingTo() {
        return reportingTo;
    }

    public void setReportingTo(String reportingTo) {
        this.reportingTo = reportingTo;
    }

    public String getOfficialPhone() {
        return officialPhone;
    }

    public void setOfficialPhone(String officialPhone) {
        this.officialPhone = officialPhone;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getAlternativeMobile() {
        return alternativeMobile;
    }

    public void setAlternativeMobile(String alternativeMobile) {
        this.alternativeMobile = alternativeMobile;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLastWorkingDate() {
        return lastWorkingDate;
    }

    public void setLastWorkingDate(String lastWorkingDate) {
        this.lastWorkingDate = lastWorkingDate;
    }

    public String getLeavingReason() {
        return leavingReason;
    }

    public void setLeavingReason(String leavingReason) {
        this.leavingReason = leavingReason;
    }

    public String getReJoiningDate() {
        return reJoiningDate;
    }

    public void setReJoiningDate(String reJoiningDate) {
        this.reJoiningDate = reJoiningDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBranchStateName() {
        return branchStateName;
    }

    public void setBranchStateName(String branchStateName) {
        this.branchStateName = branchStateName;
    }

    public String getBranchLocationName() {
        return branchLocationName;
    }

    public void setBranchLocationName(String branchLocationName) {
        this.branchLocationName = branchLocationName;
    }

    // Get initials for avatar
    public String getInitials() {
        String first = firstName != null && !firstName.isEmpty() ? firstName.substring(0, 1).toUpperCase() : "";
        String last = lastName != null && !lastName.isEmpty() ? lastName.substring(0, 1).toUpperCase() : "";
        return first + last;
    }

    // Check if employee is active
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
} 