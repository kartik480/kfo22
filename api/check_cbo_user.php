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
    
    // Get the username from GET parameter
    $username = isset($_GET['username']) ? trim($_GET['username']) : '';
    
    if (empty($username)) {
        throw new Exception('Username parameter is required');
    }
    
    // Check if the user exists in tbl_cbo_users
    $userSql = "SELECT * FROM tbl_cbo_users WHERE username = ?";
    $userStmt = $conn->prepare($userSql);
    $userStmt->execute([$username]);
    $user = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get all users who have this user as their reportingTo
    $reportingToSql = "SELECT id, username, first_name, last_name, reportingTo, status FROM tbl_cbo_users WHERE reportingTo = ?";
    $reportingToStmt = $conn->prepare($reportingToSql);
    $reportingToStmt->execute([$username]);
    $reportingToUsers = $reportingToStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get all unique reportingTo values in the table
    $allReportingToSql = "SELECT DISTINCT reportingTo FROM tbl_cbo_users WHERE reportingTo IS NOT NULL AND reportingTo != '' ORDER BY reportingTo";
    $allReportingToStmt = $conn->prepare($allReportingToSql);
    $allReportingToStmt->execute();
    $allReportingTo = $allReportingToStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get all users in the table (limited to first 10 for readability)
    $allUsersSql = "SELECT id, username, first_name, last_name, reportingTo, status FROM tbl_cbo_users ORDER BY id LIMIT 10";
    $allUsersStmt = $conn->prepare($allUsersSql);
    $allUsersStmt->execute();
    $allUsers = $allUsersStmt->fetchAll(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'CBO User Check Results',
        'data' => [
            'searching_for_username' => $username,
            'user_exists_in_table' => $user ? true : false,
            'user_details' => $user,
            'users_reporting_to_this_user' => $reportingToUsers,
            'count_users_reporting_to_this_user' => count($reportingToUsers),
            'all_reportingTo_values_in_table' => $allReportingTo,
            'sample_users_in_table' => $allUsers
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("Check CBO User Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 