package com.kfinone.app;

public class AgentItem {
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

    public AgentItem(String fullName, String companyName, String phoneNumber, 
                    String alternativePhoneNumber, String emailId, String partnerType, 
                    String state, String location, String address, String visitingCard,
                    String createdUser, String createdBy) {
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
    }

    // Getters
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
} 
