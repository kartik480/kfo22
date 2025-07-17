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
    // Fetch from tbl_user table using firstName and lastName columns
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
    
    $reportingUsersList = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $reportingUsersList[] = array(
                    'id' => $row['id'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'full_name' => $fullName
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $reportingUsersList,
        'message' => 'Reporting users list fetched successfully from tbl_user',
        'count' => count($reportingUsersList)
    ));
    
} catch (Exception $e) {
    error_log("Error in get_reporting_users_list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch reporting users: ' . $e->getMessage()
    ));
}
?> 