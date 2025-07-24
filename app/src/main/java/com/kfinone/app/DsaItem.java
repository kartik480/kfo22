package com.kfinone.app;

public class DsaItem {
    private String vendorBank;
    private String dsaCode;
    private String dsaName;
    private String loanType;
    private String state;
    private String location;

    public DsaItem(String vendorBank, String dsaCode, String dsaName, String loanType, String state, String location) {
        this.vendorBank = vendorBank;
        this.dsaCode = dsaCode;
        this.dsaName = dsaName;
        this.loanType = loanType;
        this.state = state;
        this.location = location;
    }

    public String getVendorBank() { return vendorBank; }
    public String getDsaCode() { return dsaCode; }
    public String getDsaName() { return dsaName; }
    public String getLoanType() { return loanType; }
    public String getState() { return state; }
    public String getLocation() { return location; }
} 