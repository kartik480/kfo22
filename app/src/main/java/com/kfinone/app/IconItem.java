package com.kfinone.app;

public class IconItem {
    private String name;
    private String description;
    private String imageUrl;
    private String urlName;

    public IconItem(String name, String description, String imageUrl, String urlName) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.urlName = urlName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrlName() {
        return urlName;
    }
} 
