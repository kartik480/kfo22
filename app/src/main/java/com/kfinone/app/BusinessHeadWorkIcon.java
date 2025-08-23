package com.kfinone.app;

public class BusinessHeadWorkIcon {
    private String id;
    private String iconName;
    private String iconUrl;
    private String iconImage;
    private String iconDescription;

    public BusinessHeadWorkIcon(String id, String iconName, String iconUrl, String iconImage, String iconDescription) {
        this.id = id;
        this.iconName = iconName;
        this.iconUrl = iconUrl;
        this.iconImage = iconImage;
        this.iconDescription = iconDescription;
    }

    // Getters
    public String getId() { return id; }
    public String getIconName() { return iconName; }
    public String getIconUrl() { return iconUrl; }
    public String getIconImage() { return iconImage; }
    public String getIconDescription() { return iconDescription; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public void setIconImage(String iconImage) { this.iconImage = iconImage; }
    public void setIconDescription(String iconDescription) { this.iconDescription = iconDescription; }
}
