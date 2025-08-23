package com.kfinone.app;

public class DataIcon {
    private int id;
    private String iconName;
    private String iconUrl;
    private String iconImage;
    private String iconDescription;

    public DataIcon(int id, String iconName, String iconUrl, String iconImage, String iconDescription) {
        this.id = id;
        this.iconName = iconName;
        this.iconUrl = iconUrl;
        this.iconImage = iconImage;
        this.iconDescription = iconDescription;
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

    @Override
    public String toString() {
        return "DataIcon{" +
                "id=" + id +
                ", iconName='" + iconName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", iconImage='" + iconImage + '\'' +
                ", iconDescription='" + iconDescription + '\'' +
                '}';
    }
}
