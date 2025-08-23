# Business Head Active Employee List Implementation

## Overview
This document summarizes the implementation of the "Active Employee List" panel for the Business Head home page. The panel displays all users from `tbl_bh_users` where `reportingTo` matches the current logged-in user's username.

## Features
- **Panel Name**: "Active Employee List"
- **Access**: Click "Performance Tracking" box on Business Head home page
- **Data Source**: `tbl_bh_users` table where `reportingTo` matches logged-in user's username
- **User Resolution**: Uses `tbl_user.username` to resolve `reportingTo` values
- **Display**: Shows all specified columns with a "View" button for full details

## Files Created

### 1. PHP API Endpoint
- **File**: `api/get_business_head_active_emp_list.php`
- **Purpose**: Fetches active employees for Business Head users
- **Features**:
  - Accepts `user_id` or `username` parameters
  - Resolves logged-in user's username from `tbl_user`
  - Fetches users from `tbl_bh_users` where `reportingTo` matches username
  - Returns all specified columns from `tbl_bh_users`
  - Includes comprehensive debug information

### 2. Android Model Class
- **File**: `app/src/main/java/com/kfinone/app/BusinessHeadUser.java`
- **Purpose**: Data model for Business Head user information
- **Fields**: All 40+ columns from `tbl_bh_users` including:
  - Basic info: `id`, `username`, `alias_name`, `first_name`, `last_name`
  - Contact: `Phone_number`, `email_id`, `alternative_mobile_number`
  - Company: `company_name`, `branch_state_name_id`, `branch_location_id`
  - Banking: `bank_id`, `account_type_id`, `account_number`, `ifsc_code`
  - Addresses: `office_address`, `residential_address`
  - Documents: `aadhaar_number`, `pan_number`, `pan_img`, `aadhaar_img`, `photo_img`, `bankproof_img`, `resume_file`
  - Icons: `data_icons`, `work_icons`
  - Metadata: `status`, `rank`, `reportingTo`, `createdBy`, `created_at`, `updated_at`

### 3. Android Adapter
- **File**: `app/src/main/java/com/kfinone/app/BusinessHeadActiveEmpListAdapter.java`
- **Purpose**: RecyclerView adapter for displaying Business Head users
- **Features**:
  - Binds user data to views with proper null handling
  - Displays key information: name, username, email, phone, status
  - Shows conditional fields: company, state, location, rank
  - Implements "View" button with detailed user information dialog
  - Includes "Copy Details" functionality for clipboard

### 4. Android Activity
- **File**: `app/src/main/java/com/kfinone/app/BusinessHeadActiveEmpListActivity.java`
- **Purpose**: Main activity for the Active Employee List panel
- **Features**:
  - Fullscreen display with toolbar navigation
  - Linear layout for user list
  - Progress indicators and error handling
  - API integration with Volley
  - Comprehensive logging for debugging

### 5. Layout Files
- **Main Layout**: `app/src/main/res/layout/activity_business_head_active_emp_list.xml`
  - Toolbar navigation
  - Title display
  - Progress bar and error text
  - RecyclerView for users
- **Item Layout**: `app/src/main/res/layout/item_business_head_user.xml`
  - CardView design with user information
  - Top section with name and View button
  - Key fields: username, email, phone, status
  - Conditional fields: company, state, location, rank

### 6. Test Page
- **File**: `test_business_head_active_emp_list.html`
- **Purpose**: HTML test page for API verification
- **Features**:
  - Test with User ID or Username
  - Real-time API response display
  - Error handling and status codes
  - Pre-filled test values

## Database Schema

### tbl_user (for resolution)
- **Column**: `username`
- **Purpose**: Resolve logged-in user's username from user_id

### tbl_bh_users (main data source)
- **Columns**: All 40+ columns as specified in the requirements
- **Key Column**: `reportingTo` (contains usernames, not IDs)
- **Filter**: `status = 'active'`
- **Order**: `first_name ASC, last_name ASC`

## API Endpoint

### URL
```
POST https://emp.kfinone.com/mobile/api/get_business_head_active_emp_list.php
```

### Request Parameters
```json
{
  "user_id": "1"
}
```
OR
```json
{
  "username": "krajeshk"
}
```

### Response Format
```json
{
  "status": "success",
  "message": "Active employees fetched successfully for Business Head user",
  "users": [
    {
      "id": "1",
      "username": "employee1",
      "alias_name": "Alias1",
      "first_name": "John",
      "last_name": "Doe",
      "password": "hashed_password",
      "Phone_number": "1234567890",
      "email_id": "john@example.com",
      "alternative_mobile_number": "0987654321",
      "company_name": "Company A",
      "branch_state_name_id": "State1",
      "branch_location_id": "Location1",
      "bank_id": "1",
      "account_type_id": "1",
      "office_address": "Office Address",
      "residential_address": "Home Address",
      "aadhaar_number": "123456789012",
      "pan_number": "ABCDE1234F",
      "account_number": "1234567890",
      "ifsc_code": "ABCD0001234",
      "rank": "Senior",
      "status": "active",
      "reportingTo": "krajeshk",
      "pan_img": "pan_image.jpg",
      "aadhaar_img": "aadhaar_image.jpg",
      "photo_img": "photo_image.jpg",
      "bankproof_img": "bank_proof.jpg",
      "resume_file": "resume.pdf",
      "data_icons": "1,2,3",
      "work_icons": "4,5,6",
      "user_id": "100",
      "createdBy": "admin",
      "created_at": "2024-01-01 00:00:00",
      "updated_at": "2024-01-01 00:00:00"
    }
  ],
  "count": 1,
  "debug_info": {
    "logged_in_user": {"id": "1"},
    "logged_in_username": "krajeshk",
    "query_executed": "SELECT ... FROM tbl_bh_users WHERE reportingTo = ? AND status = 'active'",
    "database_stats": {
      "total_bh_users": 100,
      "active_bh_users": 80,
      "users_reporting_to_logged_in": 5,
      "active_employees_found": 5
    }
  }
}
```

## Implementation Details

### Integration with Business Head Panel
- **File Updated**: `app/src/main/java/com/kfinone/app/BusinessHeadPanelActivity.java`
- **Change**: Updated `cardPerformanceTracking` click listener to launch `BusinessHeadActiveEmpListActivity`
- **Previous Behavior**: Launched `BusinessHeadEmpMasterActivity`
- **New Behavior**: Launches the Active Employee List panel

### Activity Declaration
- **File Updated**: `app/src/main/AndroidManifest.xml`
- **Note**: Activity was already declared in the manifest

### User Resolution Logic
1. **Input**: Receive `user_id` or `username` from Android app
2. **Resolution**: Query `tbl_user` to get logged-in user's username
3. **Search**: Find all users in `tbl_bh_users` where `reportingTo` matches the username
4. **Filter**: Only include users with `status = 'active'`
5. **Return**: All specified columns for matching users

### Error Handling
- Comprehensive error messages
- Debug information for troubleshooting
- Graceful fallbacks for missing data
- Input validation and sanitization

### UI/UX Features
- Linear layout for optimal user list display
- Card-based design for each user
- Loading indicators and error states
- Responsive design with proper margins
- "View" button for detailed information
- "Copy Details" functionality

## Testing

### Manual Testing
1. Open `test_business_head_active_emp_list.html`
2. Enter User ID or Username
3. Click test buttons
4. Verify API responses

### Android Testing
1. Build and install the app
2. Navigate to Business Head panel
3. Click "Performance Tracking" box
4. Verify employee list displays correctly
5. Test "View" button functionality
6. Test "Copy Details" functionality

## Dependencies
- **Android**: RecyclerView, CardView, Volley, AlertDialog, ClipboardManager
- **PHP**: PDO, JSON functions
- **Database**: MySQL with prepared statements

## Security Features
- Input validation and sanitization
- Prepared statements for SQL queries
- CORS headers for cross-origin requests
- Error message sanitization

## Future Enhancements
- Image loading for user photos and documents
- Caching for better performance
- Offline support
- Advanced filtering and search
- Export functionality
- Bulk operations

## Notes
- All files follow the "Business Head..." naming convention
- The implementation is consistent with existing Business Head panels
- Comprehensive error handling and debugging information
- Responsive design for various screen sizes
- Existing activity declaration in AndroidManifest.xml was reused
- The "Performance Tracking" box now shows Active Employee List instead of Employee Master
