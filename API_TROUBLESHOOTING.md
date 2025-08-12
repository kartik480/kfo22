# API Troubleshooting Guide - 500 Error Resolution

## 🚨 **Current Issue: HTTP 500 Internal Server Error**

The Business Head My Partner API is returning a 500 error. This guide will help you resolve this issue step by step.

## 🔍 **Step 1: Test Database Connection**

First, test your database connection by visiting:
```
http://your-domain.com/api/test_db_connection.php
```

This will show you:
- ✓ Database connection status
- ✓ Table existence
- ✓ Sample data queries
- ✗ Any specific error messages

## 🔧 **Step 2: Update Database Configuration**

### Option A: Update db_config.php
Edit `api/db_config.php` and update these values:

```php
$DB_HOST = 'localhost';           // Your database server
$DB_NAME = 'your_database_name';  // Your actual database name
$DB_USER = 'your_username';       // Your database username
$DB_PASS = 'your_password';       // Your database password
```

### Option B: Direct Connection in API
If `db_config.php` doesn't work, the API will try direct connection. Update these values in `business_head_my_partner_users.php`:

```php
$host = 'localhost';
$dbname = 'your_database_name';  // Update this
$username = 'your_username';      // Update this
$password = 'your_password';      // Update this
```

## 🗄️ **Step 3: Common Database Names for KfinOne**

Try these common database names:
- `kfinone_db`
- `emp_kfinone`
- `kfinone`
- `kfinone_app`
- `kfinone_mobile`

## 👤 **Step 4: Common Database Usernames**

Try these common usernames:
- `root` (for local development)
- `kfinone_user`
- `emp_kfinone`
- `kfinone_admin`

## 📋 **Step 5: Verify Table Names**

The API expects these tables to exist:
1. `tbl_user` - User information
2. `tbl_designation` - User designations
3. `tbl_partner_users` - Partner user data

## 🧪 **Step 6: Test API Endpoints**

### Test 1: Basic Connection
```
GET http://your-domain.com/api/test_db_connection.php
```

### Test 2: Business Head API (after fixing connection)
```
GET http://your-domain.com/api/business_head_my_partner_users.php?username=94000
```

## 🐛 **Step 7: Common Error Solutions**

### Error: "Connection failed"
- Check database host, name, username, password
- Verify database server is running
- Check firewall settings

### Error: "Table doesn't exist"
- Verify table names are correct
- Check if you're connected to the right database
- Look for typos in table names

### Error: "Access denied for user"
- Check username and password
- Verify user has access to the database
- Check user permissions

## 📱 **Step 8: Android App Testing**

After fixing the API:

1. **Build the app**: `./gradlew assembleDebug`
2. **Test the API call** in the app
3. **Check logs** for any remaining errors

## 🔍 **Step 9: Debug Information**

The updated API now includes:
- Detailed error messages
- File and line numbers
- Connection type information
- Debug parameters

## 📞 **Step 10: Get Help**

If you're still getting errors:

1. **Check the test script output** first
2. **Look at server error logs** (usually in `/var/log/apache2/` or `/var/log/nginx/`)
3. **Verify database credentials** with your database administrator
4. **Test with a simple database client** (phpMyAdmin, MySQL Workbench)

## 🎯 **Quick Fix Checklist**

- [ ] Database server is running
- [ ] Database credentials are correct
- [ ] Tables exist with correct names
- [ ] User has proper permissions
- [ ] API files are accessible via web
- [ ] No syntax errors in PHP files

## 🚀 **Expected Result**

After fixing the database connection, you should see:

```json
{
  "success": true,
  "message": "Partners fetched successfully",
  "data": [...],
  "stats": {
    "total_partners": 5,
    "active_partners": 4,
    "inactive_partners": 1
  }
}
```

## 📝 **Next Steps**

1. **Test database connection** using the test script
2. **Update database credentials** in the config file
3. **Verify table existence** and structure
4. **Test the API endpoint** with Postman or browser
5. **Test the Android app** functionality

Let me know what the test script shows and I can help you further!
