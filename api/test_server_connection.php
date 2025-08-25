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
    
    // Test basic connection
    $stmt = $pdo->query("SELECT 1 as test");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // Test if tbl_user table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_user'");
    $userTableExists = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // Test if tbl_designation table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_designation'");
    $designationTableExists = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // Get sample data from tbl_user (limit 1 for safety)
    $stmt = $pdo->query("SELECT id, username, firstName, lastName, designation_id, reportingTo, status FROM tbl_user LIMIT 1");
    $sampleUser = $stmt->fetch(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Server connection test successful',
        'data' => [
            'connection_test' => $result,
            'tbl_user_exists' => $userTableExists ? true : false,
            'tbl_designation_exists' => $designationTableExists ? true : false,
            'sample_user_data' => $sampleUser,
            'server_info' => [
                'host' => $host,
                'database' => $dbname,
                'php_version' => PHP_VERSION
            ]
        ]
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed',
        'debug' => $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'General error occurred',
        'debug' => $e->getMessage()
    ]);
}
?>
