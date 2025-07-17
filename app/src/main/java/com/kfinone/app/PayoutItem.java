package com.kfinone.app;

public class PayoutItem {
    private int id;
    private int userId;
    private String payoutTypeName;
    private String vendorBankName;
    private String loanTypeName;
    private String categoryName;
    private String payout;

    public PayoutItem(int id, int userId, String payoutTypeName, String vendorBankName, 
                     String loanTypeName, String categoryName, String payout) {
        this.id = id;
        this.userId = userId;
        this.payoutTypeName = payoutTypeName;
        this.vendorBankName = vendorBankName;
        this.loanTypeName = loanTypeName;
        this.categoryName = categoryName;
        this.payout = payout;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getPayoutTypeName() { return payoutTypeName; }
    public String getVendorBankName() { return vendorBankName; }
    public String getLoanTypeName() { return loanTypeName; }
    public String getCategoryName() { return categoryName; }
    public String getPayout() { return payout; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setPayoutTypeName(String payoutTypeName) { this.payoutTypeName = payoutTypeName; }
    public void setVendorBankName(String vendorBankName) { this.vendorBankName = vendorBankName; }
    public void setLoanTypeName(String loanTypeName) { this.loanTypeName = loanTypeName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setPayout(String payout) { this.payout = payout; }
} 