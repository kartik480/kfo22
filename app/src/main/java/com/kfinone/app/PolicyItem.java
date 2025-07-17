package com.kfinone.app;

public class PolicyItem {
    private int id;
    private int vendorBankId;
    private int loanTypeId;
    private String vendorBankName;
    private String loanType;
    private String image;
    private String content;

    public PolicyItem(int id, int vendorBankId, int loanTypeId, String vendorBankName, String loanType, String image, String content) {
        this.id = id;
        this.vendorBankId = vendorBankId;
        this.loanTypeId = loanTypeId;
        this.vendorBankName = vendorBankName;
        this.loanType = loanType;
        this.image = image;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendorBankId() {
        return vendorBankId;
    }

    public void setVendorBankId(int vendorBankId) {
        this.vendorBankId = vendorBankId;
    }

    public int getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(int loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public String getVendorBankName() {
        return vendorBankName;
    }

    public void setVendorBankName(String vendorBankName) {
        this.vendorBankName = vendorBankName;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 
