<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get first 10 records from tbl_sdsa_users to see the structure
    $query = "SELECT * FROM tbl_sdsa_users LIMIT 10";
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $sdsaUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get count of total records
    $countQuery = "SELECT COUNT(*) as total_count FROM tbl_sdsa_users";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute();
    $count = $countStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get unique reportingTo values
    $reportingQuery = "SELECT DISTINCT reportingTo FROM tbl_sdsa_users WHERE reportingTo IS NOT NULL AND reportingTo != '' LIMIT 10";
    $reportingStmt = $pdo->prepare($reportingQuery);
    $reportingStmt->execute();
    $reportingToValues = $reportingStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'success' => true,
        'message' => 'Debug information for tbl_sdsa_users',
        'total_records' => (int)$count['total_count'],
        'sample_records' => $sdsaUsers,
        'unique_reporting_to_values' => $reportingToValues,
        'note' => 'This shows the structure of tbl_sdsa_users table'
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred',
        'error' => $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred',
        'error' => $e->getMessage()
    ]);
}
?>
