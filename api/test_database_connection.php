<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration for kfinone database
$db_host = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_name = "emp_kfinone";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";

try {
    echo json_encode([
        'status' => 'debug',
        'message' => 'Starting database connection test...',
        'config' => [
            'host' => $db_host,
            'database' => $db_name,
            'username' => $db_username,
            'password' => '***hidden***'
        ]
    ]);
    exit;
    
    // Create connection
    $conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);
    
    if (!$conn) {
        throw new Exception('Connection failed: ' . mysqli_connect_error());
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Database connection successful',
        'server_info' => [
            'server_info' => $conn->server_info,
            'host_info' => $conn->host_info,
            'protocol_version' => $conn->protocol_version
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage()
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 