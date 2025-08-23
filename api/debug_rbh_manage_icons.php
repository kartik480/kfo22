<?php
// Debug version of get_rbh_manage_icons.php to identify 500 error
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Log all errors to help debug
ini_set('log_errors', 1);
ini_set('error_log', '/tmp/php_errors.log');

try {
    // Step 1: Basic PHP test
    echo json_encode([
        'step' => '1',
        'status' => 'success',
        'message' => 'PHP is working',
        'php_version' => phpversion(),
        'server_time' => date('Y-m-d H:i:s')
    ]);
    exit();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'step' => '1',
        'status' => 'error',
        'message' => 'PHP Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
    exit();
} catch (Error $e) {
    http_response_code(500);
    echo json_encode([
        'step' => '1',
        'status' => 'error',
        'message' => 'PHP Fatal Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
    exit();
}
?>
