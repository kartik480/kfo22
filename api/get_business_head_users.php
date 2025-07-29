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
    
    // Query to fetch Business Head users specifically
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
        WHERE d.designation_name = 'Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY u.firstName, u.lastName
    ";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $businessHeadUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get team members who report to Business Heads
    $teamMembersSql = "
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
        WHERE md.designation_name = 'Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY m.firstName, u.firstName, u.lastName
    ";
    
    $stmt = $conn->prepare($teamMembersSql);
    $stmt->execute();
    $teamMembers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics
    $statsSql = "
        SELECT 
            COUNT(DISTINCT u.id) as total_business_head_users,
            COUNT(DISTINCT CASE WHEN u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '' THEN u.id END) as active_business_head_users,
            COUNT(DISTINCT tm.id) as total_team_members,
            COUNT(DISTINCT CASE WHEN tm.status = 'Active' OR tm.status = 1 OR tm.status IS NULL OR tm.status = '' THEN tm.id END) as active_team_members
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_user tm ON tm.reportingTo = u.id
        WHERE d.designation_name = 'Business Head'
    ";
    
    $stmt = $conn->prepare($statsSql);
    $stmt->execute();
    $stats = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Business Head users fetched successfully',
        'data' => [
            'business_head_users' => $businessHeadUsers,
            'team_members' => $teamMembers,
            'statistics' => [
                'total_business_head_users' => (int)$stats['total_business_head_users'],
                'active_business_head_users' => (int)$stats['active_business_head_users'],
                'total_team_members' => (int)$stats['total_team_members'],
                'active_team_members' => (int)$stats['active_team_members']
            ]
        ],
        'counts' => [
            'business_head_users_count' => count($businessHeadUsers),
            'team_members_count' => count($teamMembers)
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [
            'business_head_users' => [],
            'team_members' => [],
            'statistics' => [
                'total_business_head_users' => 0,
                'active_business_head_users' => 0,
                'total_team_members' => 0,
                'active_team_members' => 0
            ]
        ],
        'counts' => [
            'business_head_users_count' => 0,
            'team_members_count' => 0
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 