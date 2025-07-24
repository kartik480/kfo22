package com.kfinone.app;

public class DirectorInsuranceItem {
    private String name;
    private String phone;
    private String email;
    private String password;

    public DirectorInsuranceItem(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getMaskedPassword() {
        if (password == null) return "";
        return password.replaceAll(".", "*");
    }
} 