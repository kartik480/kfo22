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
    
    // Query to get users with specific designations
    // Join tbl_user with tbl_designation to filter by designation names
    $sql = "SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                d.designation_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName ASC, u.lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    // Debug: Log the raw query result
    error_log("Emp panel query returned " . $result->num_rows . " rows");
    
    $empPanelList = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Debug: Log each user being processed
            error_log("Processing emp panel user ID " . $row['id'] . ": " . $fullName);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $empPanelList[] = array(
                    'id' => $row['id'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'fullName' => $fullName,
                    'employee_no' => $row['employee_no'] ?? '',
                    'mobile' => $row['mobile'] ?? '',
                    'email_id' => $row['email_id'] ?? '',
                    'designation_name' => $row['designation_name']
                );
                error_log("Added user to emp panel list: " . $fullName . " (" . $row['designation_name'] . ")");
            } else {
                error_log("Skipped user ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Log the number of users found for debugging
    error_log("Found " . count($empPanelList) . " users for emp panel (filtered by specific designations)");
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $empPanelList,
        'message' => 'Employee panel users fetched successfully',
        'count' => count($empPanelList)
    ));
    
} catch (Exception $e) {
    error_log("Error in fetch_emp_panel_users.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch employee panel users: ' . $e->getMessage()
    ));
}
?> 