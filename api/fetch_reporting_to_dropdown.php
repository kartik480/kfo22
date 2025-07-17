<?php
/**
 * Fetch ReportingTo Dropdown Options
 * 
 * This API endpoint fetches all active users from tbl_user table
 * to populate the ReportingTo dropdown in SDSA Details panel.
 * 
 * Returns: JSON array of users with id, firstName, lastName, and combined full name
 */

// Set headers for JSON response and CORS
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Get database connection
    global $conn;
    
    // Validate database connection
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // Check if connection is alive
    if (!$conn->ping()) {
        throw new Exception("Database connection is not alive");
    }
    
    // Query to get all active users for reporting dropdown
    // This query handles different status formats and ensures we get valid users
    $sql = "SELECT 
                id,
                firstName,
                lastName,
                status
            FROM tbl_user 
            WHERE (
                status = 'Active' 
                OR status = 1 
                OR status IS NULL 
                OR status = ''
                OR status = 'active'
            ) 
            AND firstName IS NOT NULL 
            AND firstName != '' 
            AND firstName != 'EMPTY'
            AND firstName != 'NULL'
            AND TRIM(firstName) != ''
            ORDER BY firstName ASC, lastName ASC";
    
    // Execute query
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    // Log query results for debugging
    error_log("ReportingTo dropdown query returned " . $result->num_rows . " rows");
    
    $reportingToList = array();
    $processedCount = 0;
    $skippedCount = 0;
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Clean and validate the data
            $firstName = trim($row['firstName']);
            $lastName = trim($row['lastName'] ?? '');
            $userId = $row['id'];
            $status = $row['status'];
            
            // Combine firstName and lastName into full name
            $fullName = trim($firstName . ' ' . $lastName);
            
            // Log processing for debugging
            error_log("Processing user ID {$userId}: firstName='{$firstName}', lastName='{$lastName}', fullName='{$fullName}'");
            
            // Only add if we have a valid full name
            if (!empty($fullName) && $fullName !== 'NULL' && strlen($fullName) > 0) {
                $reportingToList[] = array(
                    'id' => $userId,
                    'name' => $fullName,
                    'firstName' => $firstName,
                    'lastName' => $lastName,
                    'status' => $status
                );
                $processedCount++;
                error_log("✅ Added user to reporting list: {$fullName} (ID: {$userId})");
            } else {
                $skippedCount++;
                error_log("❌ Skipped user ID {$userId} due to invalid name: '{$fullName}'");
            }
        }
    }
    
    // Log summary for debugging
    error_log("ReportingTo dropdown processing complete: {$processedCount} users added, {$skippedCount} users skipped");
    
    // Prepare response
    $response = array(
        'status' => 'success',
        'message' => 'Reporting to dropdown options fetched successfully',
        'data' => $reportingToList,
        'count' => count($reportingToList),
        'processed' => $processedCount,
        'skipped' => $skippedCount,
        'timestamp' => date('Y-m-d H:i:s'),
        'query_info' => array(
            'total_rows' => $result->num_rows,
            'sql' => $sql
        )
    );
    
    // Return JSON response
    echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    // Log the error
    error_log("Error in fetch_reporting_to_dropdown.php: " . $e->getMessage());
    error_log("Stack trace: " . $e->getTraceAsString());
    
    // Return error response
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch reporting dropdown options: ' . $e->getMessage(),
        'timestamp' => date('Y-m-d H:i:s'),
        'error_details' => array(
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'type' => get_class($e)
        )
    ), JSON_PRETTY_PRINT);
}

// Close database connection if it exists
if (isset($conn) && $conn) {
    $conn->close();
}
?> 