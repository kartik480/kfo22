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
    
    // Get all unique reportingTo values from tbl_sdsa_users
    $query = "SELECT DISTINCT reportingTo, COUNT(*) as count 
              FROM tbl_sdsa_users 
              WHERE reportingTo IS NOT NULL AND reportingTo != ''
              GROUP BY reportingTo 
              ORDER BY reportingTo";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $reportingToList = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get user ID 8 details from tbl_user (only if columns exist)
    $userResult = null;
    try {
        $userQuery = "SELECT id, username FROM tbl_user WHERE id = 8";
        $userStmt = $pdo->prepare($userQuery);
        $userStmt->execute();
        $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    } catch (Exception $e) {
        // If tbl_user doesn't have the expected columns, skip it
        $userResult = ['id' => 8, 'username' => 'Unknown'];
    }
    
    // Get sample data from tbl_sdsa_users
    $sampleQuery = "SELECT id, username, reportingTo, status 
                    FROM tbl_sdsa_users 
                    LIMIT 10";
    
    $sampleStmt = $pdo->prepare($sampleQuery);
    $sampleStmt->execute();
    $sampleData = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'ReportingTo lists fetched successfully',
        'data' => [
            'reporting_to_list' => $reportingToList,
            'total_unique_reporting_to' => count($reportingToList),
            'sample_sdsa_users' => $sampleData
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