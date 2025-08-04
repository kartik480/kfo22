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
    
    // Query to get ALL users who might have team members (anyone who could be a manager)
    $sql = "SELECT DISTINCT
                u.id,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                d.designation_name
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE EXISTS (
                SELECT 1 FROM tbl_user team_member 
                WHERE team_member.reportingTo = u.id 
                AND (team_member.status = 'Active' OR team_member.status = 1 OR team_member.status IS NULL OR team_member.status = '')
            )
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName ASC, u.lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    // Debug: Log the raw query result
    error_log("ManagingDirectorAllUsers query returned " . $result->num_rows . " rows");
    
    $allUsers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Debug: Log each user being processed
            error_log("Processing all users user ID " . $row['id'] . ": " . $fullName);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $allUsers[] = array(
                    'id' => $row['id'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'fullName' => $fullName,
                    'employee_no' => $row['employee_no'] ?? '',
                    'mobile' => $row['mobile'] ?? '',
                    'email_id' => $row['email_id'] ?? '',
                    'designation_name' => $row['designation_name'] ?? 'Unknown'
                );
                error_log("Added user to all users list: " . $fullName . " (" . ($row['designation_name'] ?? 'Unknown') . ")");
            } else {
                error_log("Skipped user ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Log the number of users found for debugging
    error_log("Found " . count($allUsers) . " users who have team members");
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $allUsers,
        'message' => 'All users with team members fetched successfully',
        'count' => count($allUsers)
    ));
    
} catch (Exception $e) {
    error_log("Error in ManagingDirectorAllUsers.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch all users: ' . $e->getMessage()
    ));
}
?> 