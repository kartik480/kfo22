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

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    // Get the logged in user ID from GET parameter
    $loggedInUserId = isset($_GET['user_id']) ? trim($_GET['user_id']) : '';
    
    if (empty($loggedInUserId)) {
        throw new Exception('user_id parameter is required');
    }
    
    // Ensure user_id is numeric - we only accept numeric user IDs
    if (!is_numeric($loggedInUserId)) {
        throw new Exception('Invalid user_id format. Expected numeric ID (e.g., 21), received: ' . $loggedInUserId);
    }
    
    // Step 1: Verify the logged-in user and get their designation
    $verifyUserSql = "
        SELECT
            u.id, u.firstName, u.lastName, u.username, u.designation_id, u.department_id,
            d.designation_name, dept.department_name, CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        WHERE u.id = ? AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
    ";
    
    $verifyStmt = $conn->prepare($verifyUserSql);
    $verifyStmt->execute([$loggedInUserId]);
    $loggedInUser = $verifyStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        throw new Exception('User not found or inactive');
    }
    
    // Step 2: Verify the user is a Regional Business Head
    if ($loggedInUser['designation_name'] !== 'Regional Business Head') {
        throw new Exception('Access denied. Only Regional Business Head users can access this feature.');
    }
    
    // Step 3: Fetch all users who are reporting to this Regional Business Head
    $reportingUsersSql = "
        SELECT
            u.id, u.username, u.firstName, u.lastName, u.employee_no, u.mobile, u.email_id,
            u.department_id, u.reportingTo, u.status, u.created_at, u.updated_at,
            d.designation_name, dept.department_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName,
            CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL AND u.firstName != ''
        ORDER BY u.firstName ASC, u.lastName ASC
    ";
    
    $reportingStmt = $conn->prepare($reportingUsersSql);
    $reportingStmt->execute([$loggedInUserId]);
    $reportingUsers = $reportingStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Step 4: Get statistics
    $statsSql = "
        SELECT 
            COUNT(*) as total_reporting_users,
            COUNT(DISTINCT designation_id) as unique_designations,
            COUNT(DISTINCT department_id) as unique_departments
        FROM tbl_user 
        WHERE reportingTo = ?
        AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute([$loggedInUserId]);
    $statistics = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Step 5: Get designation breakdown
    $designationBreakdownSql = "
        SELECT 
            d.designation_name,
            COUNT(*) as count
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        GROUP BY d.designation_name
        ORDER BY count DESC
    ";
    
    $designationStmt = $conn->prepare($designationBreakdownSql);
    $designationStmt->execute([$loggedInUserId]);
    $designationBreakdown = $designationStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Step 6: Get department breakdown
    $departmentBreakdownSql = "
        SELECT 
            COALESCE(dept.department_name, 'Not Assigned') as department_name,
            COUNT(*) as count
        FROM tbl_user u
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        GROUP BY dept.department_name
        ORDER BY count DESC
    ";
    
    $departmentStmt = $conn->prepare($departmentBreakdownSql);
    $departmentStmt->execute([$loggedInUserId]);
    $departmentBreakdown = $departmentStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Step 7: Format response
    $response = [
        'status' => 'success',
        'message' => 'RBH Reporting Users fetched successfully',
        'data' => $reportingUsers,
        'statistics' => $statistics,
        'logged_in_user' => $loggedInUser,
        'breakdown' => [
            'designations' => $designationBreakdown,
            'departments' => $departmentBreakdown
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("RBH Reporting Users Error: " . $e->getMessage());
    
    $errorResponse = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'debug' => [
            'original_user_id_param' => isset($_GET['user_id']) ? $_GET['user_id'] : 'not_provided',
            'resolved_user_id' => isset($loggedInUserId) ? $loggedInUserId : 'not_resolved',
            'timestamp' => date('Y-m-d H:i:s'),
            'file' => __FILE__,
            'line' => $e->getLine(),
            'error_message' => $e->getMessage()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($errorResponse);
}
?> 