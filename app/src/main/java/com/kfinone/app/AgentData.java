package com.kfinone.app;

public class AgentData {
    private String id;
    private String fullName;
    private String companyName;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String emailId;
    private String partnerType;
    private String state;
    private String location;
    private String address;
    private String visitingCard;
    private String createdUser;
    private String createdBy;
    private String status;
    private String createdAt;
    private String updatedAt;
    
    // Creator information
    private String creatorFirstName;
    private String creatorLastName;
    private String creatorUsername;
    private String creatorFullName;

    public AgentData(String id, String fullName, String companyName, String phoneNumber,
                     String alternativePhoneNumber, String emailId, String partnerType,
                     String state, String location, String address, String visitingCard,
                     String createdUser, String createdBy, String status, String createdAt,
                     String updatedAt, String creatorFirstName, String creatorLastName,
                     String creatorUsername, String creatorFullName) {
        this.id = id;
        this.fullName = fullName;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.alternativePhoneNumber = alternativePhoneNumber;
        this.emailId = emailId;
        this.partnerType = partnerType;
        this.state = state;
        this.location = location;
        this.address = address;
        this.visitingCard = visitingCard;
        this.createdUser = createdUser;
        this.createdBy = createdBy;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.creatorFirstName = creatorFirstName;
        this.creatorLastName = creatorLastName;
        this.creatorUsername = creatorUsername;
        this.creatorFullName = creatorFullName;
    }

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getCompanyName() { return companyName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
    public String getEmailId() { return emailId; }
    public String getPartnerType() { return partnerType; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public String getAddress() { return address; }
    public String getVisitingCard() { return visitingCard; }
    public String getCreatedUser() { return createdUser; }
    public String getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getCreatorFirstName() { return creatorFirstName; }
    public String getCreatorLastName() { return creatorLastName; }
    public String getCreatorUsername() { return creatorUsername; }
    public String getCreatorFullName() { return creatorFullName; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAlternativePhoneNumber(String alternativePhoneNumber) { this.alternativePhoneNumber = alternativePhoneNumber; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setPartnerType(String partnerType) { this.partnerType = partnerType; }
    public void setState(String state) { this.state = state; }
    public void setLocation(String location) { this.location = location; }
    public void setAddress(String address) { this.address = address; }
    public void setVisitingCard(String visitingCard) { this.visitingCard = visitingCard; }
    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setCreatorFirstName(String creatorFirstName) { this.creatorFirstName = creatorFirstName; }
    public void setCreatorLastName(String creatorLastName) { this.creatorLastName = creatorLastName; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }
    public void setCreatorFullName(String creatorFullName) { this.creatorFullName = creatorFullName; }
}
