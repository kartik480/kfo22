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
    
    // Fixed manager ID as requested - fetch all users reporting to ID 8
    $managerId = 8;
    
    // Query to get users who report to manager ID 8
    // Join tbl_user with tbl_designation to get designation names
    $sql = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                u.manage_icons,
                u.reportingTo,
                u.department_id,
                u.designation_id,
                u.status,
                u.created_at,
                u.updated_at,
                d.designation_name,
                dept.department_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_department dept ON u.department_id = dept.id
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
    error_log("ManagingDirector Active Emp List query for manager_id: " . $managerId);
    error_log("ManagingDirector Active Emp List query returned " . $result->num_rows . " rows");
    
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
                    'displayName' => $row['displayName'],
                    'employee_no' => $row['employee_no'] ?? '',
                    'mobile' => $row['mobile'] ?? '',
                    'email_id' => $row['email_id'] ?? '',
                    'manage_icons' => $row['manage_icons'] ?? '',
                    'reportingTo' => $row['reportingTo'],
                    'designation_name' => $row['designation_name'],
                    'department_name' => $row['department_name'] ?? '',
                    'status' => $row['status'] ?? '',
                    'created_at' => $row['created_at'] ?? '',
                    'updated_at' => $row['updated_at'] ?? ''
                );
                error_log("Added team member: " . $fullName . " (reports to: " . $row['reportingTo'] . ")");
            } else {
                error_log("Skipped team member ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Get summary statistics
    $statsSql = "SELECT 
                    COUNT(*) as total_team_members,
                    COUNT(CASE WHEN status = 'Active' OR status = 1 OR status IS NULL OR status = '' THEN 1 END) as active_members
                  FROM tbl_user 
                  WHERE reportingTo = ?";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->bind_param("s", $managerId);
    $statsStmt->execute();
    $statsResult = $statsStmt->get_result();
    $stats = $statsResult->fetch_assoc();
    
    // Log the number of team members found
    error_log("Found " . count($teamMembers) . " team members for manager_id: " . $managerId);
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $teamMembers,
        'message' => 'Managing Director Active Emp List fetched successfully',
        'count' => count($teamMembers),
        'manager_id' => $managerId,
        'statistics' => array(
            'total_team_members' => $stats['total_team_members'] ?? 0,
            'active_members' => $stats['active_members'] ?? 0
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_managing_director_active_emp_list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch Managing Director Active Emp List: ' . $e->getMessage()
    ));
}
?>
