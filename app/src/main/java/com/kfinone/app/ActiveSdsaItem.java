package com.kfinone.app;

public class ActiveSdsaItem {
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
    private String employeeNo;
    private String department;
    private String designation;
    private String branchState;
    private String branchLocation;
    private String bankName;
    private String accountType;
    private String panImg;
    private String aadhaarImg;
    private String photoImg;
    private String bankProofImg;
    private int id;

    public ActiveSdsaItem(String aliasName, String firstName, String lastName, String password,
                         String phoneNumber, String emailId, String alternativeMobileNumber,
                         String companyName, String branchStateNameId, String branchLocationId,
                         String bankId, String accountTypeId, String officeAddress,
                         String residentialAddress, String aadhaarNumber, String panNumber,
                         String accountNumber, String ifscCode, String rank, String status,
                         String reportingTo, String employeeNo, String department,
                         String designation, String branchState, String branchLocation,
                         String bankName, String accountType, String panImg, String aadhaarImg, 
                         String photoImg, String bankProofImg, int id) {
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
        this.employeeNo = employeeNo;
        this.department = department;
        this.designation = designation;
        this.branchState = branchState;
        this.branchLocation = branchLocation;
        this.bankName = bankName;
        this.accountType = accountType;
        this.panImg = panImg;
        this.aadhaarImg = aadhaarImg;
        this.photoImg = photoImg;
        this.bankProofImg = bankProofImg;
        this.id = id;
    }

    // Getters
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
    public String getEmployeeNo() { return employeeNo; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public String getBranchState() { return branchState; }
    public String getBranchLocation() { return branchLocation; }
    public String getBankName() { return bankName; }
    public String getAccountType() { return accountType; }
    public String getPanImg() { return panImg; }
    public String getAadhaarImg() { return aadhaarImg; }
    public String getPhotoImg() { return photoImg; }
    public String getBankProofImg() { return bankProofImg; }
    public int getId() { return id; }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 
