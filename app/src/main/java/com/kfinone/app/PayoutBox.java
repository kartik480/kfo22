package com.kfinone.app;

public class PayoutBox {
    private String id;
    private String userId;
    private String payoutTypeId;
    private String loanTypeId;
    private String vendorBankId;
    private String categoryId;
    private String payout;
    private String status;
    private String createdBy;
    private String createdUser;
    private String createdAt;
    private String updatedAt;
    private String payoutName;
    private String payoutTypeTableId;

    public PayoutBox() {
        // Default constructor
    }

    public PayoutBox(String id, String userId, String payoutTypeId, String loanTypeId, 
                    String vendorBankId, String categoryId, String payout, String status,
                    String createdBy, String createdUser, String createdAt, String updatedAt,
                    String payoutName, String payoutTypeTableId) {
        this.id = id;
        this.userId = userId;
        this.payoutTypeId = payoutTypeId;
        this.loanTypeId = loanTypeId;
        this.vendorBankId = vendorBankId;
        this.categoryId = categoryId;
        this.payout = payout;
        this.status = status;
        this.createdBy = createdBy;
        this.createdUser = createdUser;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.payoutName = payoutName;
        this.payoutTypeTableId = payoutTypeTableId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPayoutTypeId() { return payoutTypeId; }
    public void setPayoutTypeId(String payoutTypeId) { this.payoutTypeId = payoutTypeId; }

    public String getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(String loanTypeId) { this.loanTypeId = loanTypeId; }

    public String getVendorBankId() { return vendorBankId; }
    public void setVendorBankId(String vendorBankId) { this.vendorBankId = vendorBankId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getPayout() { return payout; }
    public void setPayout(String payout) { this.payout = payout; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedUser() { return createdUser; }
    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getPayoutName() { return payoutName; }
    public void setPayoutName(String payoutName) { this.payoutName = payoutName; }

    public String getPayoutTypeTableId() { return payoutTypeTableId; }
    public void setPayoutTypeTableId(String payoutTypeTableId) { this.payoutTypeTableId = payoutTypeTableId; }

    // Helper method to get display name
    public String getDisplayName() {
        if (payoutName != null && !payoutName.isEmpty()) {
            return payoutName;
        }
        return "Payout " + id;
    }

    // Helper method to get formatted payout amount
    public String getFormattedPayout() {
        if (payout != null && !payout.isEmpty()) {
            try {
                double amount = Double.parseDouble(payout);
                return String.format("₹%.2f", amount);
            } catch (NumberFormatException e) {
                return "₹" + payout;
            }
        }
        return "₹0.00";
    }
}
