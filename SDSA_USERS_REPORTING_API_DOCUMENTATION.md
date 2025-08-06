# SDSA Users Reporting to User API Documentation

## Overview
The `get_sdsa_users_reporting_to_user.php` API endpoint detects the designated user who logged in and fetches all SDSA users who report to them based on the `reportingTo` column in the `tbl_sdsa_users` table.

## API Endpoint
```
GET https://emp.kfinone.com/mobile/api/get_sdsa_users_reporting_to_user.php?user_id={user_id}
```

## Parameters
- `user_id` (required): The ID of the logged-in user from `tbl_user` table

## How It Works

### 1. User Detection
The API first identifies the current logged-in user by:
- Taking the `user_id` parameter
- Querying `tbl_user` table to get user details
- Retrieving the user's designation from `tbl_designation` table

### 2. SDSA Users Fetching
The API then fetches SDSA users who report to this user by:
- Querying `tbl_sdsa_users` table where `reportingTo` column matches the user's ID
- Also checking for users where `createdBy` matches the user's ID (for Business Heads)
- Filtering only active users with valid names
- Sorting results by first name and last name

### 3. Data Processing
- Combines users from both `reportingTo` and `createdBy` queries
- Removes duplicates based on user ID
- Formats full names and validates data
- Returns all relevant user information

## Response Format

### Success Response
```json
{
    "status": "success",
    "data": [
        {
            "id": "1",
            "username": "john.doe",
            "alias_name": "John",
            "first_name": "John",
            "last_name": "Doe",
            "full_name": "John Doe",
            "phone_number": "1234567890",
            "email_id": "john@example.com",
            "company_name": "KfinOne",
            "department": "Sales",
            "designation": "SDSA",
            "status": "Active",
            "employee_no": "EMP001",
            "reportingTo": "5",
            "createdBy": "5",
            // ... other fields from tbl_sdsa_users
        }
    ],
    "message": "SDSA users reporting to user fetched successfully",
    "count": 1,
    "current_user": {
        "id": "5",
        "name": "Manager Name",
        "designation": "Chief Business Officer",
        "designation_type": "CBO"
    },
    "debug": {
        "user_id_requested": "5",
        "reporting_users_count": 1,
        "business_head_users_count": 0,
        "total_unique_users": 1
    }
}
```

### Error Response
```json
{
    "status": "error",
    "message": "Failed to fetch SDSA users: User with ID 999 not found in tbl_user table."
}
```

## Database Tables Used

### tbl_user
- Contains the main user information
- Fields: `id`, `firstName`, `lastName`, `designation_id`, `status`

### tbl_designation
- Contains designation information
- Fields: `id`, `designation_name`, `designation_type`

### tbl_sdsa_users
- Contains SDSA user information
- Key fields: `id`, `first_name`, `last_name`, `reportingTo`, `createdBy`, `status`
- All columns from the table are returned in the response

## Usage Examples

### 1. Test with User ID 1
```
GET https://emp.kfinone.com/mobile/api/get_sdsa_users_reporting_to_user.php?user_id=1
```

### 2. Test with User ID 5 (CBO)
```
GET https://emp.kfinone.com/mobile/api/get_sdsa_users_reporting_to_user.php?user_id=5
```

### 3. Test with User ID 10 (RBH)
```
GET https://emp.kfinone.com/mobile/api/get_sdsa_users_reporting_to_user.php?user_id=10
```

## Supported Designations
The API works with any user designation, but is particularly designed for:
- **CBO (Chief Business Officer)**
- **RBH (Regional Business Head)**
- **BH (Business Head)**
- **Managing Director**
- Any other designation that has SDSA users reporting to them

## Features

### 1. Automatic User Detection
- Validates that the provided user_id exists in tbl_user
- Retrieves user's designation information
- Ensures user is active

### 2. Comprehensive Data Fetching
- Fetches users reporting directly to the user (reportingTo)
- Fetches users created by the user (createdBy) - useful for Business Heads
- Combines and deduplicates results

### 3. Data Validation
- Filters only active users
- Ensures users have valid names
- Sorts results alphabetically

### 4. Debug Information
- Provides detailed debug information
- Shows counts for different query types
- Helps troubleshoot issues

## Testing
Use the provided `test_sdsa_users_reporting_to_user.html` file to test the API:
1. Open the HTML file in a browser
2. Enter a user ID
3. Click "Test API" to see the results
4. Use the preset buttons to test with common user IDs

## Error Handling
The API handles various error scenarios:
- Invalid user_id parameter
- User not found in database
- Database connection issues
- SQL query errors

All errors are logged and returned with appropriate HTTP status codes.

## Integration with Android App
This API is designed to be integrated with the KfinOne Android app to:
- Display SDSA users in listboxes/dropdowns
- Show reporting hierarchy
- Manage user relationships
- Support different user roles and permissions 