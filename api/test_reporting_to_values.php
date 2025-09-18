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
    
    // Get unique reportingTo values from tbl_sdsa_users
    $query = "SELECT DISTINCT reportingTo FROM tbl_sdsa_users WHERE reportingTo IS NOT NULL AND reportingTo != '' ORDER BY reportingTo";
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $reportingToValues = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get count of users for each reportingTo value
    $countQuery = "SELECT reportingTo, COUNT(*) as user_count FROM tbl_sdsa_users WHERE reportingTo IS NOT NULL AND reportingTo != '' GROUP BY reportingTo ORDER BY reportingTo";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute();
    $reportingCounts = $countStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'success' => true,
        'message' => 'ReportingTo values from tbl_sdsa_users',
        'unique_reporting_to_values' => $reportingToValues,
        'reporting_counts' => $reportingCounts,
        'note' => 'These are the reportingTo values that exist in tbl_sdsa_users'
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
