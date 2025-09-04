package com.kfinone.app;

public class RbhUserItem {
    private String id;
    private String username;
    private String fullName;
    private String creatorName;
    private String creatorDesignation;
    
    public RbhUserItem(String id, String username, String fullName, String creatorName, String creatorDesignation) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.creatorName = creatorName;
        this.creatorDesignation = creatorDesignation;
    }
    
    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getCreatorName() { return creatorName; }
    public String getCreatorDesignation() { return creatorDesignation; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public void setCreatorDesignation(String creatorDesignation) { this.creatorDesignation = creatorDesignation; }
}
