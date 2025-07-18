<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Query to fetch Chief Business Officer users specifically
    $sql = "
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            u.username,
            u.email_id,
            u.mobile,
            u.employee_no,
            u.designation_id,
            u.department_id,
            u.reportingTo,
            u.status,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        WHERE d.designation_name = 'Chief Business Officer'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY u.firstName, u.lastName
    ";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $cboUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get team members who report to Chief Business Officers
    $teamSql = "
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            u.username,
            u.email_id,
            u.mobile,
            u.employee_no,
            u.designation_id,
            u.department_id,
            u.reportingTo,
            u.status,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName,
            CONCAT(m.firstName, ' ', m.lastName) as managerName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        INNER JOIN tbl_user m ON u.reportingTo = m.id
        INNER JOIN tbl_designation md ON m.designation_id = md.id
        WHERE md.designation_name = 'Chief Business Officer'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY m.firstName, u.firstName, u.lastName
    ";
    
    $teamStmt = $conn->prepare($teamSql);
    $teamStmt->execute();
    $teamMembers = $teamStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get summary statistics
    $statsSql = "
        SELECT 
            COUNT(DISTINCT u.id) as total_cbo_users,
            COUNT(DISTINCT t.id) as total_team_members
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_user t ON t.reportingTo = u.id
        WHERE d.designation_name = 'Chief Business Officer'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND (t.status = 'Active' OR t.status = 1 OR t.status IS NULL OR t.status = '')
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute();
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Chief Business Officer data fetched successfully',
        'data' => [
            'cbo_users' => $cboUsers,
            'team_members' => $teamMembers,
            'statistics' => $stats
        ],
        'counts' => [
            'cbo_users_count' => count($cboUsers),
            'team_members_count' => count($teamMembers)
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [],
        'counts' => [
            'cbo_users_count' => 0,
            'team_members_count' => 0
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 