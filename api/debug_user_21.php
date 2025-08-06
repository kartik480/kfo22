<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    // Check if user ID 21 exists
    $userSql = "SELECT id, username, firstName, lastName, status FROM tbl_user WHERE id = 21";
    $userStmt = $conn->prepare($userSql);
    $userStmt->execute();
    $user = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    // Check how many users report to user 21
    $reportingSql = "SELECT COUNT(*) as count FROM tbl_user WHERE reportingTo = 21";
    $reportingStmt = $conn->prepare($reportingSql);
    $reportingStmt->execute();
    $reportingCount = $reportingStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get sample of users reporting to 21
    $sampleSql = "SELECT id, username, firstName, lastName, status, reportingTo FROM tbl_user WHERE reportingTo = 21 LIMIT 5";
    $sampleStmt = $conn->prepare($sampleSql);
    $sampleStmt->execute();
    $sampleUsers = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get sample of all reportingTo values
    $allReportingSql = "SELECT DISTINCT reportingTo, COUNT(*) as count FROM tbl_user WHERE reportingTo IS NOT NULL GROUP BY reportingTo ORDER BY count DESC LIMIT 10";
    $allReportingStmt = $conn->prepare($allReportingSql);
    $allReportingStmt->execute();
    $allReporting = $allReportingStmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'user_21_exists' => $user ? true : false,
        'user_21_details' => $user,
        'users_reporting_to_21' => [
            'count' => $reportingCount['count'],
            'sample' => $sampleUsers
        ],
        'all_reporting_values' => $allReporting
    ], JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>