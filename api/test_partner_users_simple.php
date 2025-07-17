<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    // Step 1: Test if PHP is working
    echo json_encode([
        'status' => 'success',
        'message' => 'PHP is working correctly',
        'step' => 'PHP execution test',
        'timestamp' => date('Y-m-d H:i:s')
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'PHP Error: ' . $e->getMessage(),
        'step' => 'PHP execution test'
    ]);
}
?> 