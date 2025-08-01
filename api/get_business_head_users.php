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
    
    // 1. Fetch Business Head Users
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
            WHERE d.designation_name = 'Business Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName, u.lastName
        ");
        $stmt->execute();
        $businessHeadUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['business_head_users'] = $businessHeadUsers;
    } catch (Exception $e) {
        $response['business_head_users'] = array();
        $response['business_head_users_error'] = $e->getMessage();
    }
    
    // 2. Fetch Team Members who report to Business Heads
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
            WHERE md.designation_name = 'Business Head'
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
    
    // 3. Fetch Statistics
    try {
        $stmt = $pdo->prepare("
            SELECT 
                COUNT(DISTINCT u.id) as total_business_head_users,
                COUNT(DISTINCT CASE WHEN u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '' THEN u.id END) as active_business_head_users,
                COUNT(DISTINCT tm.id) as total_team_members,
                COUNT(DISTINCT CASE WHEN tm.status = 'Active' OR tm.status = 1 OR tm.status IS NULL OR tm.status = '' THEN tm.id END) as active_team_members,
                COUNT(DISTINCT CASE WHEN tm.designation_id = (SELECT id FROM tbl_designation WHERE designation_name = 'SDSA') THEN tm.id END) as total_sdsa_users,
                COUNT(DISTINCT CASE WHEN tm.designation_id = (SELECT id FROM tbl_designation WHERE designation_name = 'Partner') THEN tm.id END) as total_partner_users,
                COUNT(DISTINCT CASE WHEN tm.designation_id = (SELECT id FROM tbl_designation WHERE designation_name = 'Agent') THEN tm.id END) as total_agent_users
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_user tm ON tm.reportingTo = u.id
            WHERE d.designation_name = 'Business Head'
        ");
        $stmt->execute();
        $stats = $stmt->fetch(PDO::FETCH_ASSOC);
        $response['statistics'] = array(
            'total_business_head_users' => (int)$stats['total_business_head_users'],
            'active_business_head_users' => (int)$stats['active_business_head_users'],
            'total_team_members' => (int)$stats['total_team_members'],
            'active_team_members' => (int)$stats['active_team_members'],
            'total_sdsa_users' => (int)$stats['total_sdsa_users'],
            'total_partner_users' => (int)$stats['total_partner_users'],
            'total_agent_users' => (int)$stats['total_agent_users']
        );
    } catch (Exception $e) {
        $response['statistics'] = array(
            'total_business_head_users' => 0,
            'active_business_head_users' => 0,
            'total_team_members' => 0,
            'active_team_members' => 0,
            'total_sdsa_users' => 0,
            'total_partner_users' => 0,
            'total_agent_users' => 0
        );
        $response['statistics_error'] = $e->getMessage();
    }
    
    // 4. Fetch Business Head Performance Data (if available)
    try {
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                COUNT(DISTINCT tm.id) as team_size,
                COUNT(DISTINCT CASE WHEN tm.status = 'Active' OR tm.status = 1 OR tm.status IS NULL OR tm.status = '' THEN tm.id END) as active_team_size
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_user tm ON tm.reportingTo = u.id
            WHERE d.designation_name = 'Business Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            GROUP BY u.id, u.firstName, u.lastName
            ORDER BY active_team_size DESC, u.firstName
        ");
        $stmt->execute();
        $performanceData = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['performance_data'] = $performanceData;
    } catch (Exception $e) {
        $response['performance_data'] = array();
        $response['performance_data_error'] = $e->getMessage();
    }
    
    // 5. Add summary information
    $response['summary'] = array(
        'total_business_heads' => count($businessHeadUsers),
        'total_team_members' => count($teamMembers),
        'api_version' => '2.0',
        'last_updated' => date('Y-m-d H:i:s'),
        'server_timezone' => date_default_timezone_get()
    );
    
    // 6. Add success response
    $response['status'] = 'success';
    $response['message'] = 'Business Head users data fetched successfully';
    $response['timestamp'] = time();
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $errorResponse = array(
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'data' => array(
            'business_head_users' => array(),
            'team_members' => array(),
            'statistics' => array(
                'total_business_head_users' => 0,
                'active_business_head_users' => 0,
                'total_team_members' => 0,
                'active_team_members' => 0,
                'total_sdsa_users' => 0,
                'total_partner_users' => 0,
                'total_agent_users' => 0
            ),
            'performance_data' => array()
        ),
        'summary' => array(
            'total_business_heads' => 0,
            'total_team_members' => 0,
            'api_version' => '2.0',
            'last_updated' => date('Y-m-d H:i:s'),
            'server_timezone' => date_default_timezone_get()
        ),
        'timestamp' => time()
    );
    
    http_response_code(500);
    echo json_encode($errorResponse, JSON_PRETTY_PRINT);
}
?> 