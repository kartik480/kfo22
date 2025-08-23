package com.kfinone.app;

public class BusinessHeadUser {
    private String id;
    private String username;
    private String aliasName;
    private String firstName;
    private String lastName;
    private String password;
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
    private String panImg;
    private String aadhaarImg;
    private String photoImg;
    private String bankproofImg;
    private String resumeFile;
    private String dataIcons;
    private String workIcons;
    private String userId;
    private String createdBy;
    private String createdAt;
    private String updatedAt;

    public BusinessHeadUser(String id, String username, String aliasName, String firstName, String lastName,
                           String password, String phoneNumber, String emailId, String alternativeMobileNumber,
                           String companyName, String branchStateNameId, String branchLocationId, String bankId,
                           String accountTypeId, String officeAddress, String residentialAddress, String aadhaarNumber,
                           String panNumber, String accountNumber, String ifscCode, String rank, String status,
                           String reportingTo, String panImg, String aadhaarImg, String photoImg, String bankproofImg,
                           String resumeFile, String dataIcons, String workIcons, String userId, String createdBy,
                           String createdAt, String updatedAt) {
        this.id = id;
        this.username = username;
        this.aliasName = aliasName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
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
        this.panImg = panImg;
        this.aadhaarImg = aadhaarImg;
        this.photoImg = photoImg;
        this.bankproofImg = bankproofImg;
        this.resumeFile = resumeFile;
        this.dataIcons = dataIcons;
        this.workIcons = workIcons;
        this.userId = userId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getAliasName() { return aliasName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
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
    public String getPanImg() { return panImg; }
    public String getAadhaarImg() { return aadhaarImg; }
    public String getPhotoImg() { return photoImg; }
    public String getBankproofImg() { return bankproofImg; }
    public String getResumeFile() { return resumeFile; }
    public String getDataIcons() { return dataIcons; }
    public String getWorkIcons() { return workIcons; }
    public String getUserId() { return userId; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setAliasName(String aliasName) { this.aliasName = aliasName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPassword(String password) { this.password = password; }
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
    public void setPanImg(String panImg) { this.panImg = panImg; }
    public void setAadhaarImg(String aadhaarImg) { this.aadhaarImg = aadhaarImg; }
    public void setPhotoImg(String photoImg) { this.photoImg = photoImg; }
    public void setBankproofImg(String bankproofImg) { this.bankproofImg = bankproofImg; }
    public void setResumeFile(String resumeFile) { this.resumeFile = resumeFile; }
    public void setDataIcons(String dataIcons) { this.dataIcons = dataIcons; }
    public void setWorkIcons(String workIcons) { this.workIcons = workIcons; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
