package com.kfinone.app;

public class PartnerUser {
    // Basic Information
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String aliasName;
    private String password;
    
    // Contact Information
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String alternativeMobileNumber;
    private String emailId;
    
    // Company Information
    private String companyName;
    private String partnerType;
    private String partnerTypeId;
    
    // Location Information
    private String state;
    private String location;
    private String address;
    private String branchState;
    private String branchLocation;
    private String branchStateNameId;
    private String branchLocationId;
    
    // Bank Information
    private String bankId;
    private String bankName;
    private String accountTypeId;
    private String accountType;
    private String accountNumber;
    private String ifscCode;
    
    // Address Information
    private String officeAddress;
    private String residentialAddress;
    
    // Identity Information
    private String aadhaarNumber;
    private String panNumber;
    private String panImg;
    private String aadhaarImg;
    private String photoImg;
    private String bankproofImg;
    
    // Employment Information
    private String department;
    private String designation;
    private String employeeNo;
    private String rank;
    private String reportingTo;
    private String status;
    
    // Creator Information
    private String createdUser;
    private String createdBy;
    private String creatorName;
    private String creatorDesignationId;
    private String creatorDesignationName;
    private String createdByName;
    
    // Timestamps
    private String createdAt;
    private String updatedAt;
    
    // Additional fields
    private String userId;
    private String visitingCard;

    // Default constructor
    public PartnerUser() {
        // Initialize with empty strings
        this.id = "";
        this.username = "";
        this.firstName = "";
        this.lastName = "";
        this.fullName = "";
        this.aliasName = "";
        this.password = "";
        this.phoneNumber = "";
        this.alternativePhoneNumber = "";
        this.alternativeMobileNumber = "";
        this.emailId = "";
        this.companyName = "";
        this.partnerType = "";
        this.partnerTypeId = "";
        this.state = "";
        this.location = "";
        this.address = "";
        this.branchState = "";
        this.branchLocation = "";
        this.branchStateNameId = "";
        this.branchLocationId = "";
        this.bankId = "";
        this.bankName = "";
        this.accountTypeId = "";
        this.accountType = "";
        this.accountNumber = "";
        this.ifscCode = "";
        this.officeAddress = "";
        this.residentialAddress = "";
        this.aadhaarNumber = "";
        this.panNumber = "";
        this.panImg = "";
        this.aadhaarImg = "";
        this.photoImg = "";
        this.bankproofImg = "";
        this.department = "";
        this.designation = "";
        this.employeeNo = "";
        this.rank = "";
        this.reportingTo = "";
        this.status = "";
        this.createdUser = "";
        this.createdBy = "";
        this.creatorName = "";
        this.creatorDesignationId = "";
        this.creatorDesignationName = "";
        this.createdByName = "";
        this.createdAt = "";
        this.updatedAt = "";
        this.userId = "";
        this.visitingCard = "";
    }

    // Constructor with all parameters
    public PartnerUser(String id, String username, String firstName, String lastName, String fullName,
                      String aliasName, String password, String phoneNumber, String alternativePhoneNumber,
                      String alternativeMobileNumber, String emailId, String companyName, String partnerType,
                      String partnerTypeId, String state, String location, String address, String branchState,
                      String branchLocation, String branchStateNameId, String branchLocationId, String bankId,
                      String bankName, String accountTypeId, String accountType, String accountNumber,
                      String ifscCode, String officeAddress, String residentialAddress, String aadhaarNumber,
                      String panNumber, String panImg, String aadhaarImg, String photoImg, String bankproofImg,
                      String department, String designation, String employeeNo, String rank, String reportingTo,
                      String status, String createdUser, String createdBy, String creatorName,
                      String creatorDesignationId, String creatorDesignationName, String createdByName,
                      String createdAt, String updatedAt, String userId, String visitingCard) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.aliasName = aliasName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.alternativePhoneNumber = alternativePhoneNumber;
        this.alternativeMobileNumber = alternativeMobileNumber;
        this.emailId = emailId;
        this.companyName = companyName;
        this.partnerType = partnerType;
        this.partnerTypeId = partnerTypeId;
        this.state = state;
        this.location = location;
        this.address = address;
        this.branchState = branchState;
        this.branchLocation = branchLocation;
        this.branchStateNameId = branchStateNameId;
        this.branchLocationId = branchLocationId;
        this.bankId = bankId;
        this.bankName = bankName;
        this.accountTypeId = accountTypeId;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.officeAddress = officeAddress;
        this.residentialAddress = residentialAddress;
        this.aadhaarNumber = aadhaarNumber;
        this.panNumber = panNumber;
        this.panImg = panImg;
        this.aadhaarImg = aadhaarImg;
        this.photoImg = photoImg;
        this.bankproofImg = bankproofImg;
        this.department = department;
        this.designation = designation;
        this.employeeNo = employeeNo;
        this.rank = rank;
        this.reportingTo = reportingTo;
        this.status = status;
        this.createdUser = createdUser;
        this.createdBy = createdBy;
        this.creatorName = creatorName;
        this.creatorDesignationId = creatorDesignationId;
        this.creatorDesignationName = creatorDesignationName;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.visitingCard = visitingCard;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public String getAliasName() { return aliasName; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
    public String getAlternativeMobileNumber() { return alternativeMobileNumber; }
    public String getEmailId() { return emailId; }
    public String getCompanyName() { return companyName; }
    public String getPartnerType() { return partnerType; }
    public String getPartnerTypeId() { return partnerTypeId; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public String getAddress() { return address; }
    public String getBranchState() { return branchState; }
    public String getBranchLocation() { return branchLocation; }
    public String getBranchStateNameId() { return branchStateNameId; }
    public String getBranchLocationId() { return branchLocationId; }
    public String getBankId() { return bankId; }
    public String getBankName() { return bankName; }
    public String getAccountTypeId() { return accountTypeId; }
    public String getAccountType() { return accountType; }
    public String getAccountNumber() { return accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public String getOfficeAddress() { return officeAddress; }
    public String getResidentialAddress() { return residentialAddress; }
    public String getAadhaarNumber() { return aadhaarNumber; }
    public String getPanNumber() { return panNumber; }
    public String getPanImg() { return panImg; }
    public String getAadhaarImg() { return aadhaarImg; }
    public String getPhotoImg() { return photoImg; }
    public String getBankproofImg() { return bankproofImg; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public String getEmployeeNo() { return employeeNo; }
    public String getRank() { return rank; }
    public String getReportingTo() { return reportingTo; }
    public String getStatus() { return status; }
    public String getCreatedUser() { return createdUser; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatorName() { return creatorName; }
    public String getCreatorDesignationId() { return creatorDesignationId; }
    public String getCreatorDesignationName() { return creatorDesignationName; }
    public String getCreatedByName() { return createdByName; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getUserId() { return userId; }
    public String getVisitingCard() { return visitingCard; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setAliasName(String aliasName) { this.aliasName = aliasName; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAlternativePhoneNumber(String alternativePhoneNumber) { this.alternativePhoneNumber = alternativePhoneNumber; }
    public void setAlternativeMobileNumber(String alternativeMobileNumber) { this.alternativeMobileNumber = alternativeMobileNumber; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setPartnerType(String partnerType) { this.partnerType = partnerType; }
    public void setPartnerTypeId(String partnerTypeId) { this.partnerTypeId = partnerTypeId; }
    public void setState(String state) { this.state = state; }
    public void setLocation(String location) { this.location = location; }
    public void setAddress(String address) { this.address = address; }
    public void setBranchState(String branchState) { this.branchState = branchState; }
    public void setBranchLocation(String branchLocation) { this.branchLocation = branchLocation; }
    public void setBranchStateNameId(String branchStateNameId) { this.branchStateNameId = branchStateNameId; }
    public void setBranchLocationId(String branchLocationId) { this.branchLocationId = branchLocationId; }
    public void setBankId(String bankId) { this.bankId = bankId; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setAccountTypeId(String accountTypeId) { this.accountTypeId = accountTypeId; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public void setPanImg(String panImg) { this.panImg = panImg; }
    public void setAadhaarImg(String aadhaarImg) { this.aadhaarImg = aadhaarImg; }
    public void setPhotoImg(String photoImg) { this.photoImg = photoImg; }
    public void setBankproofImg(String bankproofImg) { this.bankproofImg = bankproofImg; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public void setRank(String rank) { this.rank = rank; }
    public void setReportingTo(String reportingTo) { this.reportingTo = reportingTo; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public void setCreatorDesignationId(String creatorDesignationId) { this.creatorDesignationId = creatorDesignationId; }
    public void setCreatorDesignationName(String creatorDesignationName) { this.creatorDesignationName = creatorDesignationName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setVisitingCard(String visitingCard) { this.visitingCard = visitingCard; }

    // Helper methods
    public boolean isActive() {
        return status != null && (status.equals("Active") || status.equals("1"));
    }

    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        } else if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (username != null) {
            return username;
        }
        return "Unknown User";
    }

    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            contact.append("Phone: ").append(phoneNumber);
        }
        if (emailId != null && !emailId.trim().isEmpty()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("Email: ").append(emailId);
        }
        return contact.toString();
    }

    // Additional helper methods for compatibility
    public String getDisplayCompany() {
        return companyName != null && !companyName.trim().isEmpty() ? companyName : "No company";
    }

    public String getDisplayPhone() {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        return alternativePhoneNumber != null ? alternativePhoneNumber : "No phone";
    }

    public String getDisplayEmail() {
        return emailId != null && !emailId.trim().isEmpty() ? emailId : "No email";
    }
} 