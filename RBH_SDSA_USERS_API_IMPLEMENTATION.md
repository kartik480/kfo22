# RBH My SDSA Users API Implementation

## 🎯 Overview

This document explains the implementation of the **RBH My SDSA Users API** (`get_rbh_my_sdsa_users.php`) which fetches SDSA users who are reporting to the currently logged-in Regional Business Head user.

## 🔍 Key Understanding: Table Differences

### **Critical Difference in `reportingTo` Column:**

| Table | `reportingTo` Column Contains | Search Method | API File |
|-------|------------------------------|---------------|----------|
| `tbl_rbh_users` | **USERNAMES** (e.g., "admin", "john_doe") | `reportingTo = "admin"` | `get_rbh_active_users.php` |
| `tbl_sdsa_users` | **USER IDs** (e.g., 40, 123, 456) | `reportingTo = 40` | `get_rbh_my_sdsa_users.php` |

## 📋 API Details

### **API Endpoint:**
```
GET /mobile/api/get_rbh_my_sdsa_users.php?reportingTo={user_id_or_username}&status=active
```

### **Parameters:**
- `reportingTo`: User ID or username of the logged-in user
- `status`: Status filter (default: "active")

### **Response Structure:**
```json
{
  "success": true,
  "message": "SDSA users reporting to logged-in user fetched successfully from tbl_sdsa_users",
  "users": [...],
  "count": 5,
  "debug_info": {
    "logged_in_user": {...},
    "database_stats": {...},
    "sample_reporting_to_values": [...],
    "reporting_relationships_found": [...],
    "numeric_reporting_analysis": {...},
    "non_numeric_reporting_analysis": {...}
  }
}
```

## 🗄️ Database Schema

### **Source Table: `tbl_sdsa_users`**
The API fetches all columns from this table:

```sql
SELECT 
  sdsa.id,
  sdsa.username,
  sdsa.alias_name,
  sdsa.first_name,
  sdsa.last_name,
  sdsa.password,
  sdsa.Phone_number,
  sdsa.email_id,
  sdsa.alternative_mobile_number,
  sdsa.company_name,
  sdsa.branch_state_name_id,
  sdsa.branch_location_id,
  sdsa.bank_id,
  sdsa.account_type_id,
  sdsa.office_address,
  sdsa.residential_address,
  sdsa.aadhaar_number,
  sdsa.pan_number,
  sdsa.account_number,
  sdsa.ifsc_code,
  sdsa.rank,
  sdsa.status,
  sdsa.reportingTo,
  sdsa.employee_no,
  sdsa.department,
  sdsa.designation,
  sdsa.branchstate,
  sdsa.branchloaction,
  sdsa.bank_name,
  sdsa.account_type,
  sdsa.pan_img,
  sdsa.aadhaar_img,
  sdsa.photo_img,
  sdsa.bankproof_img,
  sdsa.user_id,
  sdsa.createdBy,
  sdsa.created_at,
  sdsa.updated_at
FROM tbl_sdsa_users sdsa
```

## 🔧 Search Logic

### **1. User Detection:**
```php
// Find logged-in user by ID or username
$checkUserQuery = "SELECT id, username, firstName, lastName, designation_id 
                   FROM tbl_user 
                   WHERE id = ? OR username = ?";
```

### **2. SDSA Users Search:**
```php
// Search for SDSA users reporting to the logged-in user
WHERE (
  sdsa.reportingTo = ?        -- reportingTo matches logged-in user's ID
  OR
  sdsa.createdBy = ?          -- createdBy matches logged-in user's ID
)
AND (
  sdsa.status = 'Active' 
  OR sdsa.status = 'active' 
  OR sdsa.status = 1 
  OR sdsa.status IS NULL 
  OR sdsa.status = ''
)
AND sdsa.first_name IS NOT NULL 
AND sdsa.first_name != ''
AND sdsa.id != ?              -- Exclude self
```

## 📊 Debug Information

The API provides comprehensive debugging information:

### **Database Statistics:**
- Total SDSA users count
- Users with `reportingTo` values
- Users with `createdBy` values
- Active users count

### **Sample Data Analysis:**
- Sample `reportingTo` values from the table
- Numeric vs non-numeric `reportingTo` analysis
- Reporting relationships found

### **Key Insights:**
- Search logic used
- Table differences explanation
- Data structure analysis

## 🧪 Testing

### **Test Page:**
Use `test_rbh_my_sdsa_users.html` to test the API with:
- Individual user IDs/usernames
- Sample user IDs (40, 123, 456, 789, 100)
- Debug information display

### **Expected Results:**
- ✅ **If `reportingTo` contains user IDs:** API works correctly
- ❌ **If `reportingTo` contains usernames:** API will not find users
- 🔍 **Mixed data:** Debug info will help identify issues

## 🔄 Comparison with RBH Users API

| Feature | RBH Users API | SDSA Users API |
|---------|---------------|----------------|
| **Table** | `tbl_rbh_users` | `tbl_sdsa_users` |
| **reportingTo Type** | Usernames | User IDs |
| **Search Method** | `reportingTo = "username"` | `reportingTo = user_id` |
| **File** | `get_rbh_active_users.php` | `get_rbh_my_sdsa_users.php` |
| **Purpose** | Find RBH users reporting to logged-in user | Find SDSA users reporting to logged-in user |

## 🚀 Implementation Steps

### **1. API Creation:**
- ✅ Created `get_rbh_my_sdsa_users.php`
- ✅ Implemented user ID-based search logic
- ✅ Added comprehensive debugging

### **2. Testing:**
- ✅ Created `test_rbh_my_sdsa_users.html`
- ✅ Added comparison table for clarity
- ✅ Included sample testing functionality

### **3. Next Steps:**
- [ ] Test the API with real user data
- [ ] Verify reporting relationships
- [ ] Integrate with Android app if needed
- [ ] Monitor performance and optimize if required

## 📝 Important Notes

1. **Data Consistency:** Ensure `tbl_sdsa_users.reportingTo` contains valid user IDs from `tbl_user.id`
2. **Status Filtering:** The API filters for active users only
3. **Self-Exclusion:** Logged-in users are excluded from results
4. **Error Handling:** Comprehensive error messages and debug information
5. **Performance:** Simple queries without complex JOINs for better performance

## 🔗 Related Files

- **API:** `api/get_rbh_my_sdsa_users.php`
- **Test Page:** `test_rbh_my_sdsa_users.html`
- **Documentation:** `RBH_SDSA_USERS_API_IMPLEMENTATION.md`
- **Related API:** `api/get_rbh_active_users.php` (for RBH users)

---

**Last Updated:** Current Date  
**Status:** ✅ Implemented and Ready for Testing
