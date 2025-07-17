package com.kfinone.app;

public class Location {
    private String name;
    private String state;
    private String status;

    public Location(String name, String state, String status) {
        this.name = name;
        this.state = state;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getStatus() {
        return status;
    }
} 
