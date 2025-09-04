package com.kfinone.app;

public class RbhUser {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String designationId;
    private String designationName;
    private String fullName;
    private String displayName;

    public RbhUser(String id, String username, String firstName, String lastName, 
                   String designationId, String designationName, String fullName, String displayName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.designationId = designationId;
        this.designationName = designationName;
        this.fullName = fullName;
        this.displayName = displayName;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDesignationId() { return designationId; }
    public String getDesignationName() { return designationName; }
    public String getFullName() { return fullName; }
    public String getDisplayName() { return displayName; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDesignationId(String designationId) { this.designationId = designationId; }
    public void setDesignationName(String designationName) { this.designationName = designationName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    @Override
    public String toString() {
        return displayName != null ? displayName : fullName;
    }
}