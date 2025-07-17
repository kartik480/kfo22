package com.kfinone.app;

public class BranchLocationItem {
    private int id;
    private String branchLocation;
    private int branchStateId;
    private String branchStateName;
    private String status;
    private String createdAt;

    public BranchLocationItem(int id, String branchLocation, int branchStateId, 
                             String branchStateName, String status, String createdAt) {
        this.id = id;
        this.branchLocation = branchLocation;
        this.branchStateId = branchStateId;
        this.branchStateName = branchStateName;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getBranchLocation() { return branchLocation; }
    public int getBranchStateId() { return branchStateId; }
    public String getBranchStateName() { return branchStateName; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setBranchLocation(String branchLocation) { this.branchLocation = branchLocation; }
    public void setBranchStateId(int branchStateId) { this.branchStateId = branchStateId; }
    public void setBranchStateName(String branchStateName) { this.branchStateName = branchStateName; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
} 
