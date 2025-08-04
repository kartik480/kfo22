package com.kfinone.app;

public class RegionalBusinessHeadUser {
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String emailId;
    private String status;

    public RegionalBusinessHeadUser(String firstName, String lastName, String username, 
                                   String phoneNumber, String emailId, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.status = status;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getStatus() {
        return status;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmployeeId() {
        return "EMP" + username; // Generate employee ID from username
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 