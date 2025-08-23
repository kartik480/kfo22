<?php
// Minimal test API to check basic PHP functionality
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Handle OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    echo json_encode(['status' => 'success', 'message' => 'OPTIONS handled']);
    exit();
}

try {
    // Basic PHP test
    $response = [
        'status' => 'success',
        'message' => 'Basic PHP functionality test',
        'data' => [
            'php_version' => phpversion(),
            'server_time' => date('Y-m-d H:i:s'),
            'request_method' => $_SERVER['REQUEST_METHOD'] ?? 'unknown',
            'extensions' => [
                'pdo' => extension_loaded('pdo'),
                'pdo_mysql' => extension_loaded('pdo_mysql'),
                'json' => extension_loaded('json'),
                'mbstring' => extension_loaded('mbstring')
            ],
            'server_info' => [
                'software' => $_SERVER['SERVER_SOFTWARE'] ?? 'unknown',
                'php_sapi' => php_sapi_name(),
                'memory_limit' => ini_get('memory_limit'),
                'max_execution_time' => ini_get('max_execution_time')
            ]
        ]
    ];
    
    echo json_encode($response);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Exception: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
} catch (Error $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
}
?>
