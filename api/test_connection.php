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
    
    // Simple test query
    $stmt = $pdo->query("SELECT COUNT(*) as total FROM tbl_sdsa_users");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    $response = [
        'success' => true,
        'message' => 'Database connection successful',
        'total_users' => $result['total'],
        'server_info' => [
            'php_version' => phpversion(),
            'server_time' => date('Y-m-d H:i:s'),
            'database' => $dbname,
            'host' => $host
        ]
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    $response = [
        'success' => false,
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'error_code' => $e->getCode()
    ];
    http_response_code(500);
    echo json_encode($response);
    
} catch (Exception $e) {
    $response = [
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ];
    http_response_code(500);
    echo json_encode($response);
}
?>
