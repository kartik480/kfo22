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
    
    // Get the logged-in Business Head user's username from GET parameter
    $loggedInUsername = $_GET['username'] ?? '';
    
    if (empty($loggedInUsername)) {
        echo json_encode([
            'success' => false,
            'message' => 'Username parameter is required to identify the logged-in Business Head user'
        ]);
        exit;
    }
    
    // First, verify that the user is actually a Business Head
    $verifyStmt = $pdo->prepare("
        SELECT 
            u.id,
            u.username,
            u.firstName,
            u.lastName,
            u.email_id,
            u.mobile,
            u.employee_no,
            u.designation_id,
            u.department_id,
            u.reportingTo,
            u.status,
            d.designation_name,
            dept.department_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        WHERE u.username = :username 
        AND d.designation_name = 'Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
    ");
    
    $verifyStmt->bindParam(':username', $loggedInUsername, PDO::PARAM_STR);
    $verifyStmt->execute();
    $businessHeadUser = $verifyStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$businessHeadUser) {
        echo json_encode([
            'success' => false,
            'message' => 'User not found or is not a Business Head user'
        ]);
        exit;
    }
    
    // Now fetch all users who are reporting to this Business Head
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
        WHERE u.reportingTo = :businessHeadId
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY u.firstName, u.lastName
    ");
    
    $stmt->bindParam(':businessHeadId', $businessHeadUser['id'], PDO::PARAM_INT);
    $stmt->execute();
    $reportingUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics for the reporting users
    $statsStmt = $pdo->prepare("
        SELECT 
            COUNT(DISTINCT u.id) as total_users,
            COUNT(DISTINCT CASE WHEN u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '' THEN u.id END) as active_users,
            COUNT(DISTINCT CASE WHEN d.designation_name = 'SDSA' THEN u.id END) as sdsa_count,
            COUNT(DISTINCT CASE WHEN d.designation_name = 'Partner' THEN u.id END) as partner_count,
            COUNT(DISTINCT CASE WHEN d.designation_name = 'Agent' THEN u.id END) as agent_count
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = :businessHeadId
    ");
    
    $statsStmt->bindParam(':businessHeadId', $businessHeadUser['id'], PDO::PARAM_INT);
    $statsStmt->execute();
    $statistics = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Prepare response
    $response = [
        'success' => true,
        'message' => 'Active employees reporting to Business Head fetched successfully',
        'business_head_info' => [
            'id' => $businessHeadUser['id'],
            'username' => $businessHeadUser['username'],
            'full_name' => $businessHeadUser['fullName'],
            'designation' => $businessHeadUser['designation_name'],
            'department' => $businessHeadUser['department_name'],
            'email' => $businessHeadUser['email_id'],
            'mobile' => $businessHeadUser['mobile'],
            'employee_no' => $businessHeadUser['employee_no']
        ],
        'reporting_users' => $reportingUsers,
        'statistics' => [
            'total_users' => (int)$statistics['total_users'],
            'active_users' => (int)$statistics['active_users'],
            'sdsa_count' => (int)$statistics['sdsa_count'],
            'partner_count' => (int)$statistics['partner_count'],
            'agent_count' => (int)$statistics['agent_count']
        ],
        'count' => count($reportingUsers),
        'timestamp' => date('Y-m-d H:i:s')
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 