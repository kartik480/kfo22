package com.kfinone.app;

public class RbhUser {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String status;
    private String designationName;
    private String departmentName;
    private String fullName;
    private String displayName;

    public RbhUser(String id, String username, String firstName, String lastName, 
                   String email, String phone, String status, String designationName, 
                   String departmentName, String fullName, String displayName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.designationName = designationName;
        this.departmentName = departmentName;
        this.fullName = fullName;
        this.displayName = displayName;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getDesignationName() { return designationName; }
    public String getDepartmentName() { return departmentName; }
    public String getFullName() { return fullName; }
    public String getDisplayName() { return displayName; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(String status) { this.status = status; }
    public void setDesignationName(String designationName) { this.designationName = designationName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    @Override
    public String toString() {
        return displayName != null ? displayName : fullName;
    }
} 