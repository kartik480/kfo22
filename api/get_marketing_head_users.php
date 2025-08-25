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

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response = array();
    
    // 1. Fetch Marketing Head Users
    try {
        $stmt = $pdo->prepare("
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
                u.created_at,
                u.updated_at,
                d.designation_name,
                dept.department_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_department dept ON u.department_id = dept.id
            WHERE d.designation_name = 'Marketing Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName, u.lastName
        ");
        $stmt->execute();
        $marketingHeadUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['marketing_head_users'] = $marketingHeadUsers;
    } catch (Exception $e) {
        $response['marketing_head_users'] = array();
        $response['marketing_head_users_error'] = $e->getMessage();
    }
    
    // 2. Fetch Team Members who report to Marketing Heads
    try {
        $stmt = $pdo->prepare("
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
                u.created_at,
                u.updated_at,
                d.designation_name,
                dept.department_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName,
                CONCAT(m.firstName, ' ', m.lastName) as managerName,
                m.id as manager_id
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            INNER JOIN tbl_user m ON u.reportingTo = m.id
            INNER JOIN tbl_designation md ON m.designation_id = md.id
            LEFT JOIN tbl_department dept ON u.department_id = dept.id
            WHERE md.designation_name = 'Marketing Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY m.firstName, u.firstName, u.lastName
        ");
        $stmt->execute();
        $teamMembers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['team_members'] = $teamMembers;
    } catch (Exception $e) {
        $response['team_members'] = array();
        $response['team_members_error'] = $e->getMessage();
    }
    
    // 3. Calculate Statistics
    try {
        $marketingHeadCount = count($response['marketing_head_users']);
        $teamMembersCount = count($response['team_members']);
        
        // Count active users
        $activeMarketingHeadCount = 0;
        foreach ($response['marketing_head_users'] as $user) {
            if ($user['status'] == 'Active' || $user['status'] == 1 || $user['status'] == null || $user['status'] == '') {
                $activeMarketingHeadCount++;
            }
        }
        
        $activeTeamMembersCount = 0;
        foreach ($response['team_members'] as $user) {
            if ($user['status'] == 'Active' || $user['status'] == 1 || $user['status'] == null || $user['status'] == '') {
                $activeTeamMembersCount++;
            }
        }
        
        $response['statistics'] = array(
            'total_marketing_head_users' => $marketingHeadCount,
            'active_marketing_head_users' => $activeMarketingHeadCount,
            'total_team_members' => $teamMembersCount,
            'active_team_members' => $activeTeamMembersCount
        );
        
        $response['counts'] = array(
            'marketing_head_users_count' => $marketingHeadCount,
            'team_members_count' => $teamMembersCount
        );
        
    } catch (Exception $e) {
        $response['statistics'] = array(
            'total_marketing_head_users' => 0,
            'active_marketing_head_users' => 0,
            'total_team_members' => 0,
            'active_team_members' => 0
        );
        $response['counts'] = array(
            'marketing_head_users_count' => 0,
            'team_members_count' => 0
        );
        $response['statistics_error'] = $e->getMessage();
    }
    
    // 4. Set response status
    $response['status'] = 'success';
    $response['message'] = 'Marketing Head users fetched successfully';
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = array(
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'marketing_head_users' => array(),
        'team_members' => array(),
        'statistics' => array(
            'total_marketing_head_users' => 0,
            'active_marketing_head_users' => 0,
            'total_team_members' => 0,
            'active_team_members' => 0
        ),
        'counts' => array(
            'marketing_head_users_count' => 0,
            'team_members_count' => 0
        )
    );
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
