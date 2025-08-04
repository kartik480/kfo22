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
    
    // Get the manager ID from the request
    $managerId = isset($_GET['manager_id']) ? $_GET['manager_id'] : null;
    
    if (!$managerId) {
        throw new Exception("Manager ID is required");
    }
    
    // Query to get users who report to the selected manager
    // Join tbl_user with tbl_designation to get designation names
    $sql = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                u.reportingTo,
                d.designation_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.reportingTo = ?
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName ASC, u.lastName ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $managerId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Query failed: " . $stmt->error);
    }
    
    // Debug: Log the query and parameters
    error_log("ManagingDirector query for manager_id: " . $managerId);
    error_log("ManagingDirector query returned " . $result->num_rows . " rows");
    
    $teamMembers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Debug: Log each team member being processed
            error_log("Processing team member ID " . $row['id'] . ": " . $fullName);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $teamMembers[] = array(
                    'id' => $row['id'],
                    'username' => $row['username'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'fullName' => $fullName,
                    'employee_no' => $row['employee_no'] ?? '',
                    'mobile' => $row['mobile'] ?? '',
                    'email_id' => $row['email_id'] ?? '',
                    'reportingTo' => $row['reportingTo'],
                    'designation_name' => $row['designation_name']
                );
                error_log("Added team member: " . $fullName . " (reports to: " . $row['reportingTo'] . ")");
            } else {
                error_log("Skipped team member ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Log the number of team members found
    error_log("Found " . count($teamMembers) . " team members for manager_id: " . $managerId);
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $teamMembers,
        'message' => 'Managing Director team members fetched successfully',
        'count' => count($teamMembers),
        'manager_id' => $managerId
    ));
    
} catch (Exception $e) {
    error_log("Error in ManagingDirector.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch Managing Director team members: ' . $e->getMessage()
    ));
}
?> 