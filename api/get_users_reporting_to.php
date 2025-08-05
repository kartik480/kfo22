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
    
    // Get the CBO ID from the request
    $cboId = isset($_GET['cbo_id']) ? $_GET['cbo_id'] : null;
    
    if (!$cboId) {
        throw new Exception("CBO ID is required");
    }
    
    // Query to get users who report to the selected CBO
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
    $stmt->bind_param("s", $cboId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Query failed: " . $stmt->error);
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
                    'manage_icons' => $manageIcons
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $teamMembers,
        'message' => 'Users reporting to CBO fetched successfully',
        'count' => count($teamMembers),
        'cbo_id' => $cboId
    ));
    
} catch (Exception $e) {
    error_log("Error in get_users_reporting_to.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch users reporting to CBO: ' . $e->getMessage()
    ));
}
?> 