<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if reportingTo column exists in tbl_user
    $columnCheckQuery = "SHOW COLUMNS FROM tbl_user LIKE 'reportingTo'";
    $columnCheckStmt = $pdo->prepare($columnCheckQuery);
    $columnCheckStmt->execute();
    $columnExists = $columnCheckStmt->fetch();
    
    if (!$columnExists) {
        // If reportingTo column doesn't exist, show table structure
        $structureQuery = "DESCRIBE tbl_user";
        $structureStmt = $pdo->prepare($structureQuery);
        $structureStmt->execute();
        $columns = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'status' => 'error',
            'message' => 'reportingTo column not found in tbl_user',
            'available_columns' => array_column($columns, 'Field')
        ]);
        exit();
    }
    
    // Get all unique reportingTo values with count
    $reportingToListQuery = "SELECT DISTINCT reportingTo, COUNT(*) as count 
                             FROM tbl_user 
                             WHERE reportingTo IS NOT NULL AND reportingTo != '' 
                             GROUP BY reportingTo 
                             ORDER BY reportingTo ASC";
    
    $reportingToListStmt = $pdo->prepare($reportingToListQuery);
    $reportingToListStmt->execute();
    $reportingToList = $reportingToListStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get all users with their reportingTo information
    $usersQuery = "SELECT id, firstName, lastName, username, reportingTo, status 
                   FROM tbl_user 
                   WHERE reportingTo IS NOT NULL AND reportingTo != '' 
                   ORDER BY reportingTo ASC, firstName ASC";
    
    $usersStmt = $pdo->prepare($usersQuery);
    $usersStmt->execute();
    $allUsers = $usersStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Group users by reportingTo
    $usersByReportingTo = [];
    foreach ($allUsers as $user) {
        $reportingTo = $user['reportingTo'];
        if (!isset($usersByReportingTo[$reportingTo])) {
            $usersByReportingTo[$reportingTo] = [];
        }
        $usersByReportingTo[$reportingTo][] = [
            'id' => $user['id'],
            'firstName' => $user['firstName'],
            'lastName' => $user['lastName'],
            'username' => $user['username'],
            'status' => $user['status']
        ];
    }
    
    // Get total statistics
    $totalUsersQuery = "SELECT COUNT(*) as total_users FROM tbl_user WHERE reportingTo IS NOT NULL AND reportingTo != ''";
    $totalUsersStmt = $pdo->prepare($totalUsersQuery);
    $totalUsersStmt->execute();
    $totalUsers = $totalUsersStmt->fetch(PDO::FETCH_ASSOC)['total_users'];
    
    // Get users without reportingTo
    $noReportingQuery = "SELECT COUNT(*) as no_reporting FROM tbl_user WHERE reportingTo IS NULL OR reportingTo = ''";
    $noReportingStmt = $pdo->prepare($noReportingQuery);
    $noReportingStmt->execute();
    $noReporting = $noReportingStmt->fetch(PDO::FETCH_ASSOC)['no_reporting'];
    
    echo json_encode([
        'status' => 'success',
        'message' => 'ReportingTo lists fetched successfully from tbl_user',
        'data' => [
            'reporting_to_list' => $reportingToList,
            'users_by_reporting_to' => $usersByReportingTo,
            'statistics' => [
                'total_unique_reporting_to' => count($reportingToList),
                'total_users_with_reporting_to' => $totalUsers,
                'users_without_reporting_to' => $noReporting,
                'total_users_in_table' => $totalUsers + $noReporting
            ]
        ]
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 