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
    
    // Get the reportingTo value from GET parameter
    $reportingTo = isset($_GET['reportingTo']) ? trim($_GET['reportingTo']) : '';
    
    if (empty($reportingTo)) {
        throw new Exception('reportingTo parameter is required');
    }
    
    // First, check if the table exists
    $tableCheckSql = "SHOW TABLES LIKE 'tbl_rbh_users'";
    $tableStmt = $conn->prepare($tableCheckSql);
    $tableStmt->execute();
    $tableExists = $tableStmt->rowCount() > 0;
    
    if (!$tableExists) {
        // If tbl_rbh_users doesn't exist, use tbl_user table with RBH designation
        $teamSql = "
            SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.mobile,
                u.email_id,
                u.employee_no,
                u.status,
                u.reportingTo,
                u.created_at,
                u.updated_at,
                d.designation_name,
                dept.department_name,
                bs.state_name as branch_state_name,
                bl.location_name as branch_location_name,
                CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, '')) as full_name
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_department dept ON u.department_id = dept.id
            LEFT JOIN tbl_branch_states bs ON u.branch_state_id = bs.id
            LEFT JOIN tbl_branch_locations bl ON u.branch_location_id = bl.id
            WHERE u.reportingTo = ? 
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND d.designation_name != 'Regional Business Head'
            ORDER BY u.firstName, u.lastName
        ";
        
        $teamStmt = $conn->prepare($teamSql);
        $teamStmt->execute([$reportingTo]);
        $teamMembers = $teamStmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Get manager details
        $managerSql = "
            SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.mobile,
                u.email_id,
                u.employee_no,
                u.status,
                d.designation_name
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.username = ? 
            AND d.designation_name = 'Regional Business Head'
        ";
        
        $managerStmt = $conn->prepare($managerSql);
        $managerStmt->execute([$reportingTo]);
        $manager = $managerStmt->fetch(PDO::FETCH_ASSOC);
        
    } else {
        // Use tbl_rbh_users table if it exists
        $teamSql = "
            SELECT 
                id,
                username,
                first_name,
                last_name,
                Phone_number as mobile,
                email_id,
                employee_no,
                status,
                reportingTo,
                created_at,
                updated_at,
                designation_name,
                department_name,
                branch_state_name,
                branch_location_name,
                CONCAT(COALESCE(first_name, ''), ' ', COALESCE(last_name, '')) as full_name
            FROM tbl_rbh_users 
            WHERE reportingTo = ? 
            AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
            ORDER BY first_name, last_name
        ";
        
        $teamStmt = $conn->prepare($teamSql);
        $teamStmt->execute([$reportingTo]);
        $teamMembers = $teamStmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Get manager details
        $managerSql = "
            SELECT 
                id,
                username,
                first_name as firstName,
                last_name as lastName,
                Phone_number as mobile,
                email_id,
                employee_no,
                status,
                designation_name
            FROM tbl_rbh_users 
            WHERE username = ? 
            AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
        ";
        
        $managerStmt = $conn->prepare($managerSql);
        $managerStmt->execute([$reportingTo]);
        $manager = $managerStmt->fetch(PDO::FETCH_ASSOC);
    }
    
    // Get summary statistics
    $statsSql = "
        SELECT 
            COUNT(*) as total_team_members,
            COUNT(CASE WHEN status = 'Active' OR status = 1 OR status IS NULL OR status = '' THEN 1 END) as active_members
        FROM tbl_user 
        WHERE reportingTo = ?
        AND designation_id IN (SELECT id FROM tbl_designation WHERE designation_name != 'Regional Business Head')
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute([$reportingTo]);
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'RBH Active User List fetched successfully',
        'data' => [
            'manager' => $manager,
            'team_members' => $teamMembers,
            'statistics' => $stats
        ],
        'counts' => [
            'total_team_members' => count($teamMembers),
            'active_members' => $stats['active_members']
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("RBH Active User List Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [],
        'counts' => [
            'total_team_members' => 0,
            'active_members' => 0
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
