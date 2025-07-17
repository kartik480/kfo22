<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
        // Query to get all active users for reporting dropdown
    // Handle both string and integer status values
    // status = 1 means Active, status = 0 means Inactive
    // Include users even without lastName (for user ID 1)
    $sql = "SELECT id, firstName, lastName 
            FROM tbl_user 
            WHERE (status = 'Active' OR status = 1 OR status IS NULL OR status = '') 
            AND firstName IS NOT NULL 
            AND firstName != '' 
            AND firstName != 'EMPTY'
            ORDER BY firstName ASC, lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    // Debug: Log the raw query result
    error_log("Raw query returned " . $result->num_rows . " rows");
    
    $reportingToList = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Debug: Log each user being processed
            error_log("Processing user ID " . $row['id'] . ": " . $fullName);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $reportingToList[] = array(
                    'id' => $row['id'],
                    'name' => $fullName,
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName']
                );
                error_log("Added user to reporting list: " . $fullName);
            } else {
                error_log("Skipped user ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Log the number of users found for debugging
    error_log("Found " . count($reportingToList) . " users for reporting dropdown (corrected query)");
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $reportingToList,
        'message' => 'Reporting to list fetched successfully',
        'count' => count($reportingToList)
    ));
    
} catch (Exception $e) {
    error_log("Error in fetch_sdsa_reporting_users.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch reporting users: ' . $e->getMessage()
    ));
}
?> 