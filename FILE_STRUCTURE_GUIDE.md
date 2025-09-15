# 📁 File Structure Guide

## 🎯 **Correct API Structure**

Your PHP files should be uploaded to:
```
https://emp.kfinone.com/mobile/api/
├── get_vendor_banks.php
├── get_loan_types.php
└── get_videos.php
```

## 📱 **Android App URLs**

The app now calls these endpoints:
- `https://emp.kfinone.com/mobile/api/get_vendor_banks.php`
- `https://emp.kfinone.com/mobile/api/get_loan_types.php`
- `https://emp.kfinone.com/mobile/api/get_videos.php`

## 🔧 **Current Status**

✅ **Android App**: Updated with correct URLs  
✅ **PHP Files**: Ready to upload  
✅ **Test HTML**: Updated with correct URLs  
✅ **Fallback Data**: Working for offline testing  

## 🚀 **Next Steps**

1. **Upload PHP files** to `https://emp.kfinone.com/mobile/api/`
2. **Run SQL script** to create database tables
3. **Test the app** - it will work immediately with fallback data!

## 📋 **Files to Upload**

From your local `api/training/` folder, upload these 3 files:
- `get_vendor_banks.php`
- `get_loan_types.php` 
- `get_videos.php`

To your server at: `https://emp.kfinone.com/mobile/api/`
