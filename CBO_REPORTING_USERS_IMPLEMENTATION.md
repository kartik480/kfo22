# Chief Business Officer Reporting Users Implementation

## Overview

This implementation provides a comprehensive solution for the Chief Business Officer (CBO) panel to fetch and display all users who are reporting to a particular designated user who logged in to the CBO panel. The system uses the `reportingTo` column in the `tbl_user` table to establish the reporting hierarchy.

## Key Features

1. **User Authentication & Verification**: Verifies that the logged-in user is a Chief Business Officer
2. **Reporting Hierarchy**: Fetches all users who report to the designated CBO user
3. **Comprehensive Statistics**: Provides detailed statistics about the reporting team
4. **Designation Breakdown**: Shows distribution of team members by designation
5. **Department Analysis**: Displays department-wise breakdown of team members
6. **Android Integration**: Complete Android app integration with modern UI
7. **API Flexibility**: Supports filtering by designation and works with any designated user type

## Database Structure

### Tables Used
- `tbl_user` - Main user table with reporting hierarchy
- `tbl_designation` - Designation information
- `tbl_department` - Department information

### Key Relationships
- `tbl_user.reportingTo` → `tbl_user.id` (Self-referencing foreign key)
- `tbl_user.designation_id` → `tbl_designation.id`
- `tbl_user.department_id` → `tbl_department.id`

## API Endpoints

### 1. CBO-Specific Reporting Users API
**Endpoint:** `GET /api/get_cbo_reporting_users.php`

**Purpose:** Fetches all users reporting to a Chief Business Officer

**Parameters:**
- `user_id` (required): The ID of the logged-in CBO user

**Response:**
```json
{
  "status": "success",
  "message": "Users reporting to Chief Business Officer fetched successfully",
  "logged_in_user": {
    "id": "21",
    "username": "cbo_user",
    "fullName": "John Doe",
    "designation_name": "Chief Business Officer"
  },
  "data": [...],
  "statistics": {
    "total_reporting_users": 15,
    "unique_designations": 5,
    "unique_departments": 3
  },
  "designation_breakdown": [...],
  "count": 15
}
```

### 2. Flexible Designated User Reporting API
**Endpoint:** `GET /api/get_designated_user_reporting_team.php`

**Purpose:** Fetches all users reporting to any designated user (CBO, RBH, Director, etc.)

**Parameters:**
- `user_id` (required): The ID of the logged-in user
- `designation` (optional): Filter by specific designation

**Response:**
```json
{
  "status": "success",
  "message": "Users reporting to designated user fetched successfully",
  "logged_in_user": {
    "id": "21",
    "username": "cbo_user",
    "fullName": "John Doe",
    "designation_name": "Chief Business Officer",
    "department_name": "Executive",
    "is_designated_user": true
  },
  "data": [...],
  "statistics": {...},
  "designation_breakdown": [...],
  "department_breakdown": [...],
  "count": 15
}
```

## Android Implementation

### 1. Main Activity: `CBOReportingUsersActivity.java`
- Displays all users reporting to the logged-in CBO
- Modern card-based UI design
- Real-time statistics display
- Bottom navigation integration
- Error handling and loading states

### 2. Adapter: `ReportingUsersAdapter.java`
- RecyclerView adapter for displaying user cards
- Status color coding (Active/Inactive)
- Contact information display
- Professional card layout

### 3. Layout Files
- `activity_cbo_reporting_users.xml`: Main activity layout
- `item_reporting_user.xml`: Individual user card layout

### 4. Drawable Resources
- `status_background.xml`: Status badge background
- `ic_contact.xml`: Contact information icon

## How It Works

### 1. User Detection
```sql
-- Verify the logged-in user is a Chief Business Officer
SELECT u.id, u.firstName, u.lastName, u.username, u.designation_id,
       d.designation_name, CONCAT(u.firstName, ' ', u.lastName) as fullName
FROM tbl_user u
INNER JOIN tbl_designation d ON u.designation_id = d.id
WHERE u.id = ?
AND d.designation_name = 'Chief Business Officer'
AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
```

### 2. Fetch Reporting Users
```sql
-- Get all users who report to this CBO
SELECT u.id, u.username, u.firstName, u.lastName, u.employee_no,
       u.mobile, u.email_id, u.department_id, u.reportingTo, u.status,
       d.designation_name, dept.department_name,
       CONCAT(u.firstName, ' ', u.lastName) as fullName,
       CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
FROM tbl_user u
INNER JOIN tbl_designation d ON u.designation_id = d.id
LEFT JOIN tbl_department dept ON u.department_id = dept.id
WHERE u.reportingTo = ?
AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
AND u.firstName IS NOT NULL AND u.firstName != ''
ORDER BY u.firstName ASC, u.lastName ASC
```

### 3. Statistics Calculation
```sql
-- Get comprehensive statistics
SELECT 
    COUNT(*) as total_reporting_users,
    COUNT(DISTINCT u.designation_id) as unique_designations,
    COUNT(DISTINCT u.department_id) as unique_departments
FROM tbl_user u
WHERE u.reportingTo = ?
AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
AND u.firstName IS NOT NULL AND u.firstName != ''
```

## Usage Examples

### 1. Android App Integration
```java
// Navigate to CBO Reporting Users Activity
Intent intent = new Intent(this, CBOReportingUsersActivity.class);
intent.putExtra("USERNAME", userName);
intent.putExtra("USER_ID", userId);
startActivity(intent);
```

### 2. API Testing
```bash
# Test CBO-specific API
curl "https://emp.kfinone.com/mobile/api/get_cbo_reporting_users.php?user_id=21"

# Test flexible API with designation filter
curl "https://emp.kfinone.com/mobile/api/get_designated_user_reporting_team.php?user_id=21&designation=Chief%20Business%20Officer"
```

### 3. Web Testing
Open `test_cbo_reporting_users.html` in a web browser to test the API with a user-friendly interface.

## Security Features

1. **User Verification**: Ensures only designated users can access the API
2. **Input Validation**: Validates user_id parameter
3. **SQL Injection Prevention**: Uses prepared statements
4. **Error Handling**: Comprehensive error handling and logging
5. **CORS Support**: Proper CORS headers for cross-origin requests

## Error Handling

### Common Error Scenarios
1. **Invalid User ID**: Returns error if user_id is not numeric
2. **User Not Found**: Returns error if user doesn't exist
3. **Wrong Designation**: Returns error if user is not a CBO (when designation filter is used)
4. **Database Errors**: Returns detailed error messages for debugging

### Error Response Format
```json
{
  "status": "error",
  "message": "User not found or is not a Chief Business Officer",
  "data": [],
  "count": 0,
  "debug": {
    "requested_user_id": "999",
    "note": "User must be a Chief Business Officer to access this endpoint"
  }
}
```

## Performance Considerations

1. **Indexed Queries**: Uses indexed columns for optimal performance
2. **Efficient Joins**: Optimized SQL queries with proper joins
3. **Pagination Ready**: Structure supports future pagination implementation
4. **Caching Friendly**: Response format suitable for caching

## Future Enhancements

1. **Pagination**: Add pagination for large teams
2. **Search & Filter**: Add search and filter capabilities
3. **Export Functionality**: Add CSV/PDF export options
4. **Real-time Updates**: Implement WebSocket for real-time updates
5. **Advanced Analytics**: Add performance metrics and trends

## Testing

### Manual Testing
1. Use the provided HTML test file
2. Test with different user IDs
3. Verify designation filtering
4. Check error scenarios

### Automated Testing
1. Unit tests for API endpoints
2. Integration tests for database queries
3. UI tests for Android activities
4. Performance tests for large datasets

## Deployment

### API Deployment
1. Upload PHP files to the server
2. Ensure database connectivity
3. Test with real user data
4. Monitor error logs

### Android Deployment
1. Build the APK with new activities
2. Test on different devices
3. Update app store listing
4. Monitor crash reports

## Support and Maintenance

### Monitoring
- Monitor API response times
- Track error rates
- Monitor database performance
- Check user feedback

### Maintenance
- Regular database optimization
- API performance tuning
- Security updates
- Feature enhancements

## Conclusion

This implementation provides a robust, scalable solution for the Chief Business Officer panel to manage and view their reporting team. The system is designed with security, performance, and user experience in mind, making it suitable for production use in enterprise environments. 