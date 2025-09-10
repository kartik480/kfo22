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
    $tableCheckSql = "SHOW TABLES LIKE 'tbl_cbo_users'";
    $tableStmt = $conn->prepare($tableCheckSql);
    $tableStmt->execute();
    $tableExists = $tableStmt->rowCount() > 0;
    
    if (!$tableExists) {
        throw new Exception('Table tbl_cbo_users does not exist');
    }
    
    // Try to get the manager's details (the person who others report to)
    // Note: Manager might not exist in tbl_cbo_users, which is okay
    $managerSql = "
        SELECT 
            id,
            username,
            first_name,
            last_name,
            Phone_number,
            email_id,
            alternative_mobile_number,
            company_name,
            branch_state_name_id,
            branch_location_id,
            bank_id,
            account_type_id,
            office_address,
            residential_address,
            aadhaar_number,
            pan_number,
            account_number,
            ifsc_code,
            rank,
            status,
            reportingTo,
            user_id,
            createdBy,
            created_at,
            updated_at
        FROM tbl_cbo_users 
        WHERE user_id = ? 
        AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
    ";
    
    $managerStmt = $conn->prepare($managerSql);
    $managerStmt->execute([$reportingTo]);
    $manager = $managerStmt->fetch(PDO::FETCH_ASSOC);
    
    // If manager not found in tbl_cbo_users, that's okay - we can still show their team
    
    // Now fetch all users who report to this manager
    // First try tbl_cbo_users table
    $teamSql = "
        SELECT 
            id,
            username,
            first_name,
            last_name,
            Phone_number,
            email_id,
            alternative_mobile_number,
            company_name,
            branch_state_name_id,
            branch_location_id,
            bank_id,
            account_type_id,
            office_address,
            residential_address,
            aadhaar_number,
            pan_number,
            account_number,
            ifsc_code,
            rank,
            status,
            reportingTo,
            user_id,
            createdBy,
            created_at,
            updated_at,
            CONCAT(COALESCE(first_name, ''), ' ', COALESCE(last_name, '')) as full_name
        FROM tbl_cbo_users 
        WHERE reportingTo = ? 
        AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
        ORDER BY first_name, last_name
    ";
    
    $teamStmt = $conn->prepare($teamSql);
    $teamStmt->execute([$reportingTo]);
    $teamMembers = $teamStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // If no team members found in tbl_cbo_users, try tbl_user table as fallback
    if (empty($teamMembers)) {
        $fallbackSql = "
            SELECT 
                id,
                username,
                firstName as first_name,
                lastName as last_name,
                mobile as Phone_number,
                email_id,
                mobile as alternative_mobile_number,
                '' as company_name,
                '' as branch_state_name_id,
                '' as branch_location_id,
                '' as bank_id,
                '' as account_type_id,
                '' as office_address,
                '' as residential_address,
                '' as aadhaar_number,
                '' as pan_number,
                '' as account_number,
                '' as ifsc_code,
                '' as rank,
                status,
                reportingTo,
                id as user_id,
                '' as createdBy,
                '' as created_at,
                '' as updated_at,
                CONCAT(COALESCE(firstName, ''), ' ', COALESCE(lastName, '')) as full_name
            FROM tbl_user 
            WHERE reportingTo = ? 
            AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
            ORDER BY firstName, lastName
        ";
        
        $fallbackStmt = $conn->prepare($fallbackSql);
        $fallbackStmt->execute([$reportingTo]);
        $teamMembers = $fallbackStmt->fetchAll(PDO::FETCH_ASSOC);
    }
    
    // Get summary statistics - check both tables
    $statsSql = "
        SELECT 
            (SELECT COUNT(*) FROM tbl_cbo_users WHERE reportingTo = ?) +
            (SELECT COUNT(*) FROM tbl_user WHERE reportingTo = ?) as total_team_members,
            (SELECT COUNT(CASE WHEN status = 'Active' OR status = 1 OR status IS NULL OR status = '' THEN 1 END) FROM tbl_cbo_users WHERE reportingTo = ?) +
            (SELECT COUNT(CASE WHEN status = 'Active' OR status = 1 OR status IS NULL OR status = '' THEN 1 END) FROM tbl_user WHERE reportingTo = ?) as active_members
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute([$reportingTo, $reportingTo, $reportingTo, $reportingTo]);
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'CBO Active Employee List fetched successfully',
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
    error_log("CBO Active Employee List Error: " . $e->getMessage());
    
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