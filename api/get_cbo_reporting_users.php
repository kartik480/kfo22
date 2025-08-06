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
    
    // Get the logged-in user ID from query parameter
    $loggedInUserId = isset($_GET['user_id']) ? trim($_GET['user_id']) : '';
    
    if (empty($loggedInUserId)) {
        throw new Exception('user_id parameter is required');
    }
    
    // Ensure user_id is numeric
    if (!is_numeric($loggedInUserId)) {
        throw new Exception('Invalid user_id format. Expected numeric ID, received: ' . $loggedInUserId);
    }
    
    // Step 1: Verify the logged-in user is a Chief Business Officer
    $verifyUserSql = "
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            u.username,
            u.designation_id,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.id = ?
        AND d.designation_name = 'Chief Business Officer'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
    ";
    
    $verifyStmt = $conn->prepare($verifyUserSql);
    $verifyStmt->execute([$loggedInUserId]);
    $loggedInUser = $verifyStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found or is not a Chief Business Officer',
            'data' => [],
            'count' => 0,
            'debug' => [
                'requested_user_id' => $loggedInUserId,
                'note' => 'User must be a Chief Business Officer to access this endpoint'
            ]
        ]);
        exit;
    }
    
    // Step 2: Fetch all users who are reporting to this Chief Business Officer
    $reportingUsersSql = "
        SELECT 
            u.id,
            u.username,
            u.firstName,
            u.lastName,
            u.employee_no,
            u.mobile,
            u.email_id,
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
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY u.firstName ASC, u.lastName ASC
    ";
    
    $reportingStmt = $conn->prepare($reportingUsersSql);
    $reportingStmt->execute([$loggedInUserId]);
    $reportingUsers = $reportingStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Step 3: Get statistics
    $statsSql = "
        SELECT 
            COUNT(*) as total_reporting_users,
            COUNT(DISTINCT u.designation_id) as unique_designations,
            COUNT(DISTINCT u.department_id) as unique_departments
        FROM tbl_user u
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute([$loggedInUserId]);
    $statistics = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Step 4: Get designation breakdown
    $designationBreakdownSql = "
        SELECT 
            d.designation_name,
            COUNT(*) as count
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        GROUP BY d.designation_name
        ORDER BY count DESC, d.designation_name ASC
    ";
    
    $designationStmt = $conn->prepare($designationBreakdownSql);
    $designationStmt->execute([$loggedInUserId]);
    $designationBreakdown = $designationStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Users reporting to Chief Business Officer fetched successfully',
        'logged_in_user' => [
            'id' => $loggedInUser['id'],
            'username' => $loggedInUser['username'],
            'fullName' => $loggedInUser['fullName'],
            'designation_name' => $loggedInUser['designation_name']
        ],
        'data' => $reportingUsers,
        'statistics' => [
            'total_reporting_users' => (int)$statistics['total_reporting_users'],
            'unique_designations' => (int)$statistics['unique_designations'],
            'unique_departments' => (int)$statistics['unique_departments']
        ],
        'designation_breakdown' => $designationBreakdown,
        'count' => count($reportingUsers),
        'debug' => [
            'logged_in_user_id' => $loggedInUserId,
            'query_executed' => 'SELECT from tbl_user WHERE reportingTo = ' . $loggedInUserId,
            'timestamp' => date('Y-m-d H:i:s')
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0,
        'debug' => [
            'error_details' => $e->getMessage(),
            'timestamp' => date('Y-m-d H:i:s')
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 