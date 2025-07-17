<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    // Test database connection first
    require_once 'db_config.php';
    
    if (!isset($conn)) {
        throw new Exception('Database connection variable $conn is not set');
    }
    
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Database connection successful',
        'debug_info' => [
            'server_info' => $conn->server_info,
            'host_info' => $conn->host_info,
            'connect_error' => $conn->connect_error
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => get_class($e),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ]);
}
?> 