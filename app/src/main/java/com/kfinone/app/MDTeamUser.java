package com.kfinone.app;

public class MDTeamUser {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String designationId;
    private String designationName;
    private String fullName;
    private String emailId;
    private String mobile;
    private String status;

    public MDTeamUser(String id, String username, String firstName, String lastName,
                      String designationId, String designationName, String fullName,
                      String emailId, String mobile, String status) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.designationId = designationId;
        this.designationName = designationName;
        this.fullName = fullName;
        this.emailId = emailId;
        this.mobile = mobile;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDesignationId() { return designationId; }
    public String getDesignationName() { return designationName; }
    public String getFullName() { return fullName; }
    public String getEmailId() { return emailId; }
    public String getMobile() { return mobile; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDesignationId(String designationId) { this.designationId = designationId; }
    public void setDesignationName(String designationName) { this.designationName = designationName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setStatus(String status) { this.status = status; }

    // Helper method to get display name for dropdown
    public String getDisplayName() {
        return fullName + " (" + designationName + ")";
    }
}
