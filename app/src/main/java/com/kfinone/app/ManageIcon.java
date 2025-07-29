package com.kfinone.app;

public class ManageIcon {
    private int id;
    private String iconName;
    private String iconUrl;
    private String iconImage;
    private String iconDescription;
    private String status;

    public ManageIcon(int id, String iconName, String iconUrl, String iconImage, String iconDescription, String status) {
        this.id = id;
        this.iconName = iconName;
        this.iconUrl = iconUrl;
        this.iconImage = iconImage;
        this.iconDescription = iconDescription;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getIconName() {
        return iconName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getIconImage() {
        return iconImage;
    }

    public String getIconDescription() {
        return iconDescription;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public void setIconDescription(String iconDescription) {
        this.iconDescription = iconDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 