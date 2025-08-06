# RBH My SDSA Panel Debugging Guide

## 🔍 Quick Debug Steps

### 1. **Test the API First**
Open `debug_rbh_my_sdsa.html` in your browser and run the tests to check:
- ✅ API connectivity
- ✅ User verification
- ✅ Database connection
- ✅ SDSA data retrieval

### 2. **Check Android Logs**
When you enter the RBH My SDSA panel, look for these log tags in Android Studio:

```
D/RBHMySdsa: === RBH My SDSA Debug Start ===
D/RBHMySdsa: Fetching SDSA users from: https://emp.kfinone.com/mobile/api/get_rbh_my_sdsa_users.php?reportingTo=21&status=active
D/RBHMySdsa: Using userId: 21
D/RBHMySdsa: User data - userName: [username], userId: [userid]
D/RBHMySdsa: Making GET request to: [URL]
D/RBHMySdsa: Connection established, getting response...
D/RBHMySdsa: Response code: 200
D/RBHMySdsa: HTTP OK - Reading response...
D/RBHMySdsa: Response length: [X] characters
D/RBHMySdsa: Raw API Response: [JSON response]
D/RBHMySdsa: JSON Response parsed successfully
D/RBHMySdsa: Success: true
D/RBHMySdsa: Users array length: [X]
D/RBHMySdsa: === RBH My SDSA Debug End (Success) ===
```

## 🚨 Common Issues & Solutions

### **Issue 1: 404 Error**
```
E/RBHMySdsa: HTTP Error 404: <!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
```
**Solution:** The API file is not deployed on the server. Check if `get_rbh_my_sdsa_users.php` exists on the server.

### **Issue 2: 500 Error**
```
E/RBHMySdsa: HTTP Error 500: Internal Server Error
```
**Solution:** Server-side PHP error. Check the PHP error logs or test the API directly.

### **Issue 3: Access Denied**
```
E/RBHMySdsa: API Error: Access denied. Only Regional Business Head users can access this feature.
```
**Solution:** The user ID doesn't have RBH designation. Use a valid RBH user ID.

### **Issue 4: No Response**
```
E/RBHMySdsa: No response from server or empty response
E/RBHMySdsa: Response was: null
```
**Solution:** Network connectivity issue or server timeout.

### **Issue 5: JSON Parse Error**
```
E/RBHMySdsa: JSON Parse Error: Unexpected token < in JSON at position 0
```
**Solution:** Server returning HTML instead of JSON (usually 404 or 500 error page).

### **Issue 6: No Users Found**
```
W/RBHMySdsa: No SDSA users found in the response
```
**Solution:** No SDSA users are reporting to this user ID. Check the `reportingTo` column in `tbl_sdsa_users`.

## 🔧 Debug Tools

### **1. Web Debug Tool**
- **File:** `debug_rbh_my_sdsa.html`
- **Usage:** Open in browser, run tests to check API functionality
- **Tests:** Basic API, User verification, Database, SDSA data

### **2. Postman Testing**
```
GET https://emp.kfinone.com/mobile/api/get_rbh_my_sdsa_users.php?reportingTo=21&status=active
```

### **3. Android Logcat Filter**
```
adb logcat | grep "RBHMySdsa"
```

## 📊 Expected Response Format

### **Success Response:**
```json
{
  "success": true,
  "message": "SDSA users fetched successfully (TEST MODE - RBH verification disabled)",
  "logged_in_user": {
    "id": "21",
    "username": "testuser",
    "full_name": "Test User",
    "designation": "Regional Business Head"
  },
  "users": [
    {
      "id": "1",
      "first_name": "John",
      "last_name": "Doe",
      "username": "johndoe",
      "Phone_number": "1234567890",
      "email_id": "john@example.com",
      "designation": "SDSA",
      "department": "Sales",
      "status": "Active"
    }
  ],
  "count": 1,
  "statistics": {
    "total_users": 1,
    "unique_designations": 1,
    "unique_departments": 1,
    "unique_banks": 1
  }
}
```

### **Error Response:**
```json
{
  "success": false,
  "message": "Error: [error message]"
}
```

## 🛠️ Manual Testing Steps

### **Step 1: Check API File**
1. Verify `get_rbh_my_sdsa_users.php` exists on server
2. Test direct URL access in browser
3. Check file permissions

### **Step 2: Test Database**
1. Use `get_all_designations.php` to test DB connection
2. Verify `tbl_sdsa_users` table exists
3. Check `reportingTo` column data

### **Step 3: Test User Data**
1. Use `check_user_designation.php` to verify user
2. Confirm user has RBH designation
3. Check if user exists in `tbl_user`

### **Step 4: Test SDSA Data**
1. Query `tbl_sdsa_users` directly
2. Check `reportingTo` values
3. Verify status filters

## 📱 Android App Debugging

### **Enable Debug Logs:**
The app now has extensive logging. Check these log levels:
- `D/RBHMySdsa` - Debug information
- `E/RBHMySdsa` - Error messages
- `W/RBHMySdsa` - Warning messages

### **Test User ID:**
- Default test ID: `21`
- Can be changed in the debug tool
- Should be a valid RBH user ID

### **Network Debugging:**
- Check internet connectivity
- Verify API URL is accessible
- Test with different user IDs

## 🎯 Quick Fixes

### **If API returns 404:**
1. Deploy the PHP file to server
2. Check file path and permissions
3. Verify server configuration

### **If API returns 500:**
1. Check PHP error logs
2. Verify database credentials
3. Test database connection

### **If no users found:**
1. Check `tbl_sdsa_users` table
2. Verify `reportingTo` column values
3. Test with different user IDs

### **If JSON parse fails:**
1. Check server response format
2. Verify API returns JSON, not HTML
3. Test API directly in browser

## 📞 Support Information

- **API Base URL:** `https://emp.kfinone.com/mobile/api/`
- **Test User ID:** `21`
- **Debug Tool:** `debug_rbh_my_sdsa.html`
- **Log Tag:** `RBHMySdsa`

---

**Next Steps:**
1. Run the debug tool first
2. Check Android logs when entering the panel
3. Compare with expected response format
4. Use the troubleshooting steps above 