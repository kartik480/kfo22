package com.kfinone.app;

public class SdsaUser {
    private String id;
    private String username;
    private String aliasName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailId;
    private String alternativeMobileNumber;
    private String companyName;
    private String branchStateNameId;
    private String branchLocationId;
    private String bankId;
    private String accountTypeId;
    private String officeAddress;
    private String residentialAddress;
    private String aadhaarNumber;
    private String panNumber;
    private String accountNumber;
    private String ifscCode;
    private String rank;
    private String status;
    private String reportingTo;
    private String employeeNo;
    private String department;
    private String designation;
    private String branchstate;
    private String branchloaction;
    private String bankName;
    private String accountType;
    private String userId;
    private String createdBy;
    private String createdAt;
    private String updatedAt;
    private String fullName;
    private String displayName;
    private String password;
    private String panImg;
    private String aadhaarImg;
    private String photoImg;
    private String bankproofImg;

    public SdsaUser(String id, String username, String aliasName, String firstName, String lastName,
                    String phoneNumber, String emailId, String alternativeMobileNumber, String companyName,
                    String branchStateNameId, String branchLocationId, String bankId, String accountTypeId,
                    String officeAddress, String residentialAddress, String aadhaarNumber, String panNumber,
                    String accountNumber, String ifscCode, String rank, String status, String reportingTo,
                    String employeeNo, String department, String designation, String branchstate,
                    String branchloaction, String bankName, String accountType, String userId,
                    String createdBy, String createdAt, String updatedAt, String fullName, String displayName,
                    String password, String panImg, String aadhaarImg, String photoImg, String bankproofImg) {
        this.id = id;
        this.username = username;
        this.aliasName = aliasName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.alternativeMobileNumber = alternativeMobileNumber;
        this.companyName = companyName;
        this.branchStateNameId = branchStateNameId;
        this.branchLocationId = branchLocationId;
        this.bankId = bankId;
        this.accountTypeId = accountTypeId;
        this.officeAddress = officeAddress;
        this.residentialAddress = residentialAddress;
        this.aadhaarNumber = aadhaarNumber;
        this.panNumber = panNumber;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.rank = rank;
        this.status = status;
        this.reportingTo = reportingTo;
        this.employeeNo = employeeNo;
        this.department = department;
        this.designation = designation;
        this.branchstate = branchstate;
        this.branchloaction = branchloaction;
        this.bankName = bankName;
        this.accountType = accountType;
        this.userId = userId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fullName = fullName;
        this.displayName = displayName;
        this.password = password;
        this.panImg = panImg;
        this.aadhaarImg = aadhaarImg;
        this.photoImg = photoImg;
        this.bankproofImg = bankproofImg;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getAliasName() { return aliasName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmailId() { return emailId; }
    public String getAlternativeMobileNumber() { return alternativeMobileNumber; }
    public String getCompanyName() { return companyName; }
    public String getBranchStateNameId() { return branchStateNameId; }
    public String getBranchLocationId() { return branchLocationId; }
    public String getBankId() { return bankId; }
    public String getAccountTypeId() { return accountTypeId; }
    public String getOfficeAddress() { return officeAddress; }
    public String getResidentialAddress() { return residentialAddress; }
    public String getAadhaarNumber() { return aadhaarNumber; }
    public String getPanNumber() { return panNumber; }
    public String getAccountNumber() { return accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public String getRank() { return rank; }
    public String getStatus() { return status; }
    public String getReportingTo() { return reportingTo; }
    public String getEmployeeNo() { return employeeNo; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public String getBranchstate() { return branchstate; }
    public String getBranchloaction() { return branchloaction; }
    public String getBankName() { return bankName; }
    public String getAccountType() { return accountType; }
    public String getUserId() { return userId; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getFullName() { return fullName; }
    public String getDisplayName() { return displayName; }
    public String getPassword() { return password; }
    public String getPanImg() { return panImg; }
    public String getAadhaarImg() { return aadhaarImg; }
    public String getPhotoImg() { return photoImg; }
    public String getBankproofImg() { return bankproofImg; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setAliasName(String aliasName) { this.aliasName = aliasName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setAlternativeMobileNumber(String alternativeMobileNumber) { this.alternativeMobileNumber = alternativeMobileNumber; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setBranchStateNameId(String branchStateNameId) { this.branchStateNameId = branchStateNameId; }
    public void setBranchLocationId(String branchLocationId) { this.branchLocationId = branchLocationId; }
    public void setBankId(String bankId) { this.bankId = bankId; }
    public void setAccountTypeId(String accountTypeId) { this.accountTypeId = accountTypeId; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public void setRank(String rank) { this.rank = rank; }
    public void setStatus(String status) { this.status = status; }
    public void setReportingTo(String reportingTo) { this.reportingTo = reportingTo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setBranchstate(String branchstate) { this.branchstate = branchstate; }
    public void setBranchloaction(String branchloaction) { this.branchloaction = branchloaction; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setPassword(String password) { this.password = password; }
    public void setPanImg(String panImg) { this.panImg = panImg; }
    public void setAadhaarImg(String aadhaarImg) { this.aadhaarImg = aadhaarImg; }
    public void setPhotoImg(String photoImg) { this.photoImg = photoImg; }
    public void setBankproofImg(String bankproofImg) { this.bankproofImg = bankproofImg; }
} 