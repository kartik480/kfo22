# 🚀 Deployment Guide for Training API

## 📋 **Current Status**
✅ Android app builds successfully  
✅ Fallback data implemented for offline testing  
❌ PHP files need to be uploaded to server  

## 🔧 **Next Steps**

### **1. Upload PHP Files to Server**
Upload these files to your server at `https://emp.kfinone.com/mobile/api/`:

```
api/
├── get_vendor_banks.php
├── get_loan_types.php
└── get_videos.php
```

### **2. Database Setup**
Run the SQL script `database_setup_training.sql` on your `emp_kfinone` database to create the required tables.

### **3. Test API Endpoints**
Use the `test_api_endpoints.html` file to test if the APIs are working:
- Open the HTML file in a browser
- Click the test buttons to verify each endpoint

## 📱 **App Features**

### **Current Functionality**
- ✅ Type Of Loan Video List panel
- ✅ Vendor Bank dropdown (with fallback data)
- ✅ Loan Type dropdown (with fallback data)
- ✅ Filter and Reset buttons
- ✅ Video list display
- ✅ Error handling with fallback data

### **Fallback Data**
When API is not available, the app shows:
- **Vendor Banks**: HDFC Bank, ICICI Bank, SBI Bank, Axis Bank, Kotak Bank
- **Loan Types**: Personal Loan, Home Loan, Car Loan, Business Loan, Education Loan
- **Sample Videos**: 5 sample training videos

## 🔍 **Troubleshooting**

### **404 Errors**
- Ensure PHP files are uploaded to correct path
- Check server permissions
- Verify database connection

### **Database Errors**
- Run the SQL setup script
- Check database credentials in PHP files
- Ensure tables exist: `tbl_vendor_bank`, `tbl_loan_type`, `tbl_training_videos`

## 📞 **Support**
If you need help with server deployment or database setup, let me know!
