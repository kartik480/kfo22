<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    // Get the reportingTo value from GET parameter
    $reportingTo = isset($_GET['reportingTo']) ? trim($_GET['reportingTo']) : '';
    
    // Get all users in the table
    $allUsersSql = "SELECT id, username, first_name, last_name, reportingTo, status FROM tbl_cbo_users ORDER BY id";
    $allUsersStmt = $conn->prepare($allUsersSql);
    $allUsersStmt->execute();
    $allUsers = $allUsersStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get users who report to the specified user
    $reportingUsersSql = "SELECT id, username, first_name, last_name, reportingTo, status FROM tbl_cbo_users WHERE reportingTo = ?";
    $reportingUsersStmt = $conn->prepare($reportingUsersSql);
    $reportingUsersStmt->execute([$reportingTo]);
    $reportingUsers = $reportingUsersStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get the manager details
    $managerSql = "SELECT id, username, first_name, last_name, reportingTo, status FROM tbl_cbo_users WHERE user_id = ?";
    $managerStmt = $conn->prepare($managerSql);
    $managerStmt->execute([$reportingTo]);
    $manager = $managerStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get unique reportingTo values
    $uniqueReportingSql = "SELECT DISTINCT reportingTo FROM tbl_cbo_users WHERE reportingTo IS NOT NULL AND reportingTo != '' ORDER BY reportingTo";
    $uniqueReportingStmt = $conn->prepare($uniqueReportingSql);
    $uniqueReportingStmt->execute();
    $uniqueReportingTo = $uniqueReportingStmt->fetchAll(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'Debug information for CBO reporting structure',
        'data' => [
            'searching_for_reportingTo' => $reportingTo,
            'total_users_in_table' => count($allUsers),
            'all_users' => $allUsers,
            'users_reporting_to_specified' => $reportingUsers,
            'manager_found' => $manager ? true : false,
            'manager_details' => $manager,
            'unique_reportingTo_values' => $uniqueReportingTo
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("Debug CBO Reporting Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 