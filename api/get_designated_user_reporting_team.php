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
    
    // Get parameters from query string
    $loggedInUserId = isset($_GET['user_id']) ? trim($_GET['user_id']) : '';
    $designationFilter = isset($_GET['designation']) ? trim($_GET['designation']) : '';
    
    if (empty($loggedInUserId)) {
        throw new Exception('user_id parameter is required');
    }
    
    // Ensure user_id is numeric
    if (!is_numeric($loggedInUserId)) {
        throw new Exception('Invalid user_id format. Expected numeric ID, received: ' . $loggedInUserId);
    }
    
    // Step 1: Verify the logged-in user and get their designation
    $verifyUserSql = "
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            u.username,
            u.designation_id,
            u.department_id,
            d.designation_name,
            dept.department_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        INNER JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        WHERE u.id = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
    ";
    
    $verifyStmt = $conn->prepare($verifyUserSql);
    $verifyStmt->execute([$loggedInUserId]);
    $loggedInUser = $verifyStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found or inactive',
            'data' => [],
            'count' => 0,
            'debug' => [
                'requested_user_id' => $loggedInUserId,
                'note' => 'User must exist and be active'
            ]
        ]);
        exit;
    }
    
    // Step 2: Check if user has a designated role (optional filter)
    $designatedRoles = [
        'Chief Business Officer', 'CBO',
        'Regional Business Head', 'RBH',
        'Director', 'Managing Director',
        'Business Head', 'Senior Business Head'
    ];
    
    $isDesignatedUser = in_array($loggedInUser['designation_name'], $designatedRoles);
    
    // If designation filter is provided, verify the user has that designation
    if (!empty($designationFilter) && $loggedInUser['designation_name'] !== $designationFilter) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User does not have the required designation: ' . $designationFilter,
            'data' => [],
            'count' => 0,
            'debug' => [
                'user_designation' => $loggedInUser['designation_name'],
                'required_designation' => $designationFilter
            ]
        ]);
        exit;
    }
    
    // Step 3: Fetch all users who are reporting to this designated user
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
    
    // Step 4: Get comprehensive statistics
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
    
    // Step 5: Get designation breakdown
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
    
    // Step 6: Get department breakdown
    $departmentBreakdownSql = "
        SELECT 
            COALESCE(dept.department_name, 'Not Assigned') as department_name,
            COUNT(*) as count
        FROM tbl_user u
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        WHERE u.reportingTo = ?
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        GROUP BY dept.department_name
        ORDER BY count DESC, department_name ASC
    ";
    
    $departmentStmt = $conn->prepare($departmentBreakdownSql);
    $departmentStmt->execute([$loggedInUserId]);
    $departmentBreakdown = $departmentStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Users reporting to designated user fetched successfully',
        'logged_in_user' => [
            'id' => $loggedInUser['id'],
            'username' => $loggedInUser['username'],
            'fullName' => $loggedInUser['fullName'],
            'designation_name' => $loggedInUser['designation_name'],
            'department_name' => $loggedInUser['department_name'],
            'is_designated_user' => $isDesignatedUser
        ],
        'data' => $reportingUsers,
        'statistics' => [
            'total_reporting_users' => (int)$statistics['total_reporting_users'],
            'unique_designations' => (int)$statistics['unique_designations'],
            'unique_departments' => (int)$statistics['unique_departments']
        ],
        'designation_breakdown' => $designationBreakdown,
        'department_breakdown' => $departmentBreakdown,
        'count' => count($reportingUsers),
        'debug' => [
            'logged_in_user_id' => $loggedInUserId,
            'designation_filter' => $designationFilter,
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