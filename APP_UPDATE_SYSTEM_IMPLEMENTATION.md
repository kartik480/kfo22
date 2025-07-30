# App Update System Implementation

## Overview
This document describes the implementation of an app update notification system for the Kurakulas Partners Android app. The system automatically checks for updates when users open the app and displays a dialog prompting them to update if a newer version is available.

## Features
- **Automatic Update Checking**: Checks for updates when the app starts
- **Smart Dialog Display**: Shows update dialog only when a newer version is available
- **Force Update Support**: Can force users to update for critical versions
- **Custom Update Messages**: Configurable update messages for each version
- **Play Store Integration**: Direct link to Google Play Store for updates
- **Fallback Support**: Works even if the server is unavailable

## Files Created/Modified

### New Files Created:
1. **`app/src/main/java/com/kfinone/app/AppUpdateChecker.java`**
   - Main utility class for checking app updates
   - Handles API calls and dialog display
   - Manages update logic and Play Store integration

2. **`app/src/main/res/layout/dialog_app_update.xml`**
   - Custom layout for the update dialog
   - Modern design with app icon and action buttons
   - Supports both optional and force update scenarios

3. **`api/check_app_version.php`**
   - PHP API endpoint for version checking
   - Returns latest version information from database
   - Includes fallback data if database is unavailable

4. **`create_app_versions_table.sql`**
   - SQL script to create the app_versions table
   - Includes sample data and proper indexing

5. **`test_app_update_api.html`**
   - Test page for the version check API
   - Allows testing different scenarios and versions

6. **`APP_UPDATE_SYSTEM_IMPLEMENTATION.md`**
   - This documentation file

### Modified Files:
1. **`app/src/main/java/com/kfinone/app/HomeActivity.java`**
   - Added update checker integration in onCreate()
   - Added checkForAppUpdates() method

2. **`app/src/main/java/com/kfinone/app/BusinessHeadPanelActivity.java`**
   - Added update checker integration in onCreate()
   - Added checkForAppUpdates() method

## Database Setup

### 1. Create the app_versions table:
```sql
-- Run the create_app_versions_table.sql script
-- This creates the table with proper structure and sample data
```

### 2. Table Structure:
```sql
CREATE TABLE `app_versions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version_code` int(11) NOT NULL COMMENT 'Android version code (increment for each release)',
  `version_name` varchar(20) NOT NULL COMMENT 'Version name like 1.0.1',
  `update_message` text COMMENT 'Message to show to users about the update',
  `force_update` tinyint(1) DEFAULT 0 COMMENT 'Whether this update is mandatory',
  `download_url` varchar(500) DEFAULT NULL COMMENT 'Custom download URL (optional)',
  `min_required_version` int(11) DEFAULT 1 COMMENT 'Minimum version required to run the app',
  `release_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'Whether this version is currently active',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `version_code` (`version_code`)
);
```

## How It Works

### 1. App Startup Flow:
```
User opens app → HomeActivity/BusinessHeadPanelActivity onCreate() → 
checkForAppUpdates() → AppUpdateChecker.checkForUpdates() → 
API call to check_app_version.php → Compare versions → 
Show dialog if update needed
```

### 2. Version Comparison:
- App gets current version from PackageInfo
- API returns latest version from database
- If server version > current version → Show update dialog
- If server version ≤ current version → No action needed

### 3. Update Dialog:
- Shows app icon, version info, and update message
- "Update Now" button opens Play Store
- "Later" button dismisses dialog (hidden for force updates)
- Force updates prevent app usage until updated

## Usage Instructions

### For Developers:

#### 1. When Releasing a New Version:
1. Increment the `versionCode` in `app/build.gradle.kts`
2. Update the `versionName` in `app/build.gradle.kts`
3. Add a new record to the `app_versions` table:

```sql
INSERT INTO app_versions (version_code, version_name, update_message, force_update) 
VALUES (3, '1.0.2', 'New features and bug fixes. Enhanced performance and user experience.', 0);
```

#### 2. For Force Updates (Critical):
```sql
INSERT INTO app_versions (version_code, version_name, update_message, force_update) 
VALUES (4, '1.0.3', 'Critical security update. Please update immediately.', 1);
```

### For Users:
- The app automatically checks for updates when opened
- If an update is available, a dialog appears
- Users can choose to update now or later (unless it's a force update)
- Clicking "Update Now" opens the Google Play Store

## API Response Format

### Success Response:
```json
{
  "status": true,
  "message": "Version information retrieved successfully",
  "data": {
    "version_code": 2,
    "version_name": "1.0.1",
    "update_message": "Bug fixes and performance improvements.",
    "force_update": false,
    "download_url": "https://play.google.com/store/apps/details?id=com.kfinone.app",
    "release_date": "2024-01-15 10:30:00",
    "min_required_version": 1
  }
}
```

### Error Response:
```json
{
  "status": false,
  "message": "Error message here",
  "data": null
}
```

## Testing

### 1. Test the API:
- Open `test_app_update_api.html` in a browser
- Click "Test Version Check API" to test the endpoint
- Use "Test with Current Version X" to simulate different app versions

### 2. Test in App:
- Build and install the app
- The update check happens automatically on app startup
- To test with different versions, modify the version code in build.gradle.kts

### 3. Test Force Updates:
- Set `force_update` to 1 in the database
- The dialog will not have a "Later" button
- Users must update to continue using the app

## Configuration

### 1. API Endpoint:
- Default: `https://emp.kfinone.com/mobile/api/check_app_version.php`
- Can be modified in `AppUpdateChecker.java`

### 2. Play Store URL:
- Default: `https://play.google.com/store/apps/details?id=com.kfinone.app`
- Can be modified in `AppUpdateChecker.java`

### 3. Update Check Frequency:
- Currently checks on every app startup
- Can be modified to check less frequently if needed

## Security Considerations

1. **API Security**: The version check API is public but only returns version information
2. **Force Updates**: Use sparingly and only for critical security updates
3. **Version Validation**: The app validates version codes before showing dialogs
4. **Fallback Handling**: System works even if the server is unavailable

## Troubleshooting

### Common Issues:

1. **Update Dialog Not Showing**:
   - Check if the API is accessible
   - Verify version codes in database vs app
   - Check network connectivity

2. **API Not Responding**:
   - The system has fallback data
   - Check server logs and database connection
   - Verify the PHP file is accessible

3. **Play Store Not Opening**:
   - Verify the Play Store URL is correct
   - Check if Play Store is installed on device
   - Ensure proper intent handling

### Debug Information:
- Check Android logs for "AppUpdateChecker" tags
- Use the test HTML page to verify API functionality
- Monitor database for version information

## Future Enhancements

1. **In-App Updates**: Integrate with Google Play In-App Updates API
2. **Delta Updates**: Support for incremental updates
3. **Update Scheduling**: Allow users to schedule updates
4. **Update History**: Track which users have updated
5. **A/B Testing**: Test different update messages
6. **Analytics**: Track update acceptance rates

## Support

For issues or questions about the app update system:
1. Check this documentation
2. Test with the provided HTML test page
3. Review Android logs for error messages
4. Verify database configuration and API accessibility

---

**Last Updated**: January 2024
**Version**: 1.0
**App**: Kurakulas Partners 