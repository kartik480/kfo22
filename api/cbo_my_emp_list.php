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
    
    // Fetch all users who are reporting to the logged in user from tbl_user table
    $empListSql = "
        SELECT 
            id,
            username,
            alias_name,
            firstName,
            lastName,
            mobile,
            email_id,
            employee_no,
            manage_icons,
            status,
            reportingTo
        FROM tbl_user 
        WHERE reportingTo = ? 
        AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
        ORDER BY firstName, lastName
    ";
    
    $empStmt = $conn->prepare($empListSql);
    $empStmt->execute([$loggedInUserId]);
    $employees = $empStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Debug: Log what we found
    error_log("CBO API Debug - Looking for employees with reportingTo = " . $loggedInUserId);
    error_log("CBO API Debug - Found " . count($employees) . " employees");
    
    // Get manager's details (the logged in user)
    $managerSql = "
        SELECT 
            id,
            username,
            firstName,
            lastName,
            mobile,
            email_id,
            employee_no,
            status
        FROM tbl_user 
        WHERE id = ?
    ";
    
    $managerStmt = $conn->prepare($managerSql);
    $managerStmt->execute([$loggedInUserId]);
    $manager = $managerStmt->fetch(PDO::FETCH_ASSOC);
    
    // Debug: Check if manager exists
    error_log("CBO API Debug - Manager found: " . ($manager ? "YES (ID: " . $manager['id'] . ")" : "NO"));
    
    // If no employees found, let's check what users exist with this reportingTo value
    if (count($employees) == 0) {
        $checkSql = "SELECT COUNT(*) as total FROM tbl_user WHERE reportingTo = ?";
        $checkStmt = $conn->prepare($checkSql);
        $checkStmt->execute([$loggedInUserId]);
        $totalCheck = $checkStmt->fetch(PDO::FETCH_ASSOC);
        error_log("CBO API Debug - Total users with reportingTo = " . $loggedInUserId . ": " . $totalCheck['total']);
        
        // Check what reportingTo values exist in the table
        $reportingSql = "SELECT DISTINCT reportingTo, COUNT(*) as count FROM tbl_user WHERE reportingTo IS NOT NULL GROUP BY reportingTo LIMIT 10";
        $reportingStmt = $conn->prepare($reportingSql);
        $reportingStmt->execute();
        $reportingValues = $reportingStmt->fetchAll(PDO::FETCH_ASSOC);
        error_log("CBO API Debug - Sample reportingTo values in database: " . json_encode($reportingValues));
    }
    
    // Process employees data and format manage_icons
    $processedEmployees = [];
    foreach ($employees as $employee) {
        // Process manage_icons to split them for display
        $manageIcons = [];
        if (!empty($employee['manage_icons'])) {
            $icons = explode(',', $employee['manage_icons']);
            foreach ($icons as $icon) {
                $icon = trim($icon);
                if (!empty($icon)) {
                    $manageIcons[] = $icon;
                }
            }
        }
        
        // Default icons if none set
        if (empty($manageIcons)) {
            $manageIcons = ['Dashboard', 'Settings', 'Analytics'];
        }
        
        // Create full name from firstName and lastName
        $fullName = trim(($employee['firstName'] ?? '') . ' ' . ($employee['lastName'] ?? ''));
        
        $processedEmployees[] = [
            'id' => $employee['id'],
            'username' => $employee['username'] ?? '',
            'full_name' => $fullName,
            'firstName' => $employee['firstName'] ?? '',
            'lastName' => $employee['lastName'] ?? '',
            'employee_id' => $employee['employee_no'] ?: 'EMP' . str_pad($employee['id'], 3, '0', STR_PAD_LEFT),
            'mobile' => $employee['mobile'] ?? '',
            'email' => $employee['email_id'] ?? '',
            'status' => $employee['status'] ?? '',
            'reportingTo' => $employee['reportingTo'] ?? '',
            'manage_icons' => $manageIcons,
            'manage_icons_string' => implode(', ', $manageIcons)
        ];
    }
    
    // Get summary statistics  
    $statsSql = "
        SELECT 
            COUNT(*) as total_employees,
            COUNT(CASE WHEN (status = 'Active' OR status = 1 OR status IS NULL OR status = '') THEN 1 END) as active_employees
        FROM tbl_user 
        WHERE reportingTo = ?
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute([$loggedInUserId]);
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'CBO employee list fetched successfully',
        'data' => [
            'manager' => $manager,
            'employees' => $processedEmployees,
            'statistics' => [
                'total_employees' => (int)$stats['total_employees'],
                'active_employees' => (int)$stats['active_employees'],
                'employee_count' => count($processedEmployees)
            ]
        ],
        'debug' => [
            'original_user_id_param' => isset($_GET['user_id']) ? $_GET['user_id'] : 'not set',
            'resolved_user_id' => $loggedInUserId,
            'query_executed' => 'SELECT from tbl_user WHERE reportingTo = ' . $loggedInUserId,
            'timestamp' => date('Y-m-d H:i:s'),
            'manager_found' => $manager ? 'yes' : 'no'
        ]
    ];
    
    http_response_code(200);
    echo json_encode($response);
    
} catch (Exception $e) {
    // Error response
    $errorResponse = [
        'status' => 'error',
        'message' => $e->getMessage(),
        'debug' => [
            'original_user_id_param' => isset($_GET['user_id']) ? $_GET['user_id'] : 'not set',
            'resolved_user_id' => isset($loggedInUserId) ? $loggedInUserId : 'not resolved',
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