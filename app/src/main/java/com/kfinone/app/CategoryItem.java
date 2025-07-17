package com.kfinone.app;

public class CategoryItem {
    private String categoryName;
    private String status;
    private int id;

    public CategoryItem(String categoryName, String status, int id) {
        this.categoryName = categoryName;
        this.status = status;
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
} 
