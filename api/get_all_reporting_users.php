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
    
    // Query to get all users who report to any designated user (CBO, Regional Business Head, Director, etc.)
    $sql = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                u.reportingTo,
                d.designation_name,
                CONCAT(manager.firstName, ' ', manager.lastName) AS manager_name,
                manager_d.designation_name AS manager_designation
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            INNER JOIN tbl_user manager ON u.reportingTo = manager.id
            INNER JOIN tbl_designation manager_d ON manager.designation_id = manager_d.id
            WHERE manager_d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director', 'Managing Director', 'Business Head', 'Senior Business Head')
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY manager_d.designation_name ASC, manager.firstName ASC, u.firstName ASC, u.lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $teamMembers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                // Get manage icons for this user
                $manageIcons = array();
                $iconSql = "SELECT mi.icon_name 
                           FROM tbl_manage_icon mi 
                           INNER JOIN tbl_user_icon_permissions uip ON mi.id = uip.icon_id 
                           WHERE uip.user_id = ? AND (mi.status = 1 OR mi.status = 'Active' OR mi.status IS NULL OR mi.status = '')
                           ORDER BY mi.icon_name ASC";
                
                $iconStmt = $conn->prepare($iconSql);
                if ($iconStmt) {
                    $iconStmt->bind_param("s", $row['id']);
                    $iconStmt->execute();
                    $iconResult = $iconStmt->get_result();
                    
                    while ($iconRow = $iconResult->fetch_assoc()) {
                        $manageIcons[] = $iconRow['icon_name'];
                    }
                    $iconStmt->close();
                }
                
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
                    'designation_name' => $row['designation_name'],
                    'manager_name' => $row['manager_name'],
                    'manager_designation' => $row['manager_designation'],
                    'manage_icons' => $manageIcons
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $teamMembers,
        'message' => 'All users reporting to designated users fetched successfully',
        'count' => count($teamMembers)
    ));
    
} catch (Exception $e) {
    error_log("Error in get_all_reporting_users.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch all reporting users: ' . $e->getMessage()
    ));
}
?> 