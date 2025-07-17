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
    
    // Query to get all users who report to Chief Business Officer, Regional Business Head, and Director
    // First, get the IDs of managers with these designations
    $managerSql = "SELECT 
                        u.id,
                        u.firstName,
                        u.lastName,
                        d.designation_name
                    FROM tbl_user u
                    INNER JOIN tbl_designation d ON u.designation_id = d.id
                    WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
                    AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                    AND u.firstName IS NOT NULL 
                    AND u.firstName != ''
                    ORDER BY d.designation_name ASC, u.firstName ASC";
    
    $managerResult = $conn->query($managerSql);
    
    if ($managerResult === false) {
        throw new Exception("Manager query failed: " . $conn->error);
    }
    
    // Get manager IDs
    $managerIds = array();
    $managerNames = array();
    
    if ($managerResult->num_rows > 0) {
        while($row = $managerResult->fetch_assoc()) {
            $managerIds[] = $row['id'];
            $managerNames[$row['id']] = trim($row['firstName'] . ' ' . $row['lastName']);
        }
    }
    
    if (empty($managerIds)) {
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No managers found with specified designations',
            'count' => 0
        ));
        exit;
    }
    
    // Create placeholders for the IN clause
    $placeholders = str_repeat('?,', count($managerIds) - 1) . '?';
    
    // Query to get all team members who report to these managers
    $sql = "SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                u.reportingTo,
                d.designation_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.reportingTo IN ($placeholders)
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.reportingTo ASC, u.firstName ASC, u.lastName ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param(str_repeat('s', count($managerIds)), ...$managerIds);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Team members query failed: " . $stmt->error);
    }
    
    // Debug: Log the query and parameters
    error_log("All team members query for manager IDs: " . implode(', ', $managerIds));
    error_log("All team members query returned " . $result->num_rows . " rows");
    
    $teamMembers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Get manager name
            $managerName = isset($managerNames[$row['reportingTo']]) ? $managerNames[$row['reportingTo']] : 'Unknown Manager';
            
            // Debug: Log each team member being processed
            error_log("Processing team member ID " . $row['id'] . ": " . $fullName . " (reports to: " . $managerName . ")");
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $teamMembers[] = array(
                    'id' => $row['id'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'fullName' => $fullName,
                    'employee_no' => $row['employee_no'] ?? '',
                    'mobile' => $row['mobile'] ?? '',
                    'email_id' => $row['email_id'] ?? '',
                    'reportingTo' => $row['reportingTo'],
                    'manager_name' => $managerName,
                    'designation_name' => $row['designation_name']
                );
                error_log("Added team member: " . $fullName . " (reports to: " . $managerName . ")");
            } else {
                error_log("Skipped team member ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Log the number of team members found
    error_log("Found " . count($teamMembers) . " team members for all managers");
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $teamMembers,
        'message' => 'All team members fetched successfully',
        'count' => count($teamMembers),
        'managers_found' => count($managerIds)
    ));
    
} catch (Exception $e) {
    error_log("Error in fetch_all_team_members.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch all team members: ' . $e->getMessage()
    ));
}
?> 