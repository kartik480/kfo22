<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Simple test endpoint to verify server is working
echo json_encode(array(
    'status' => 'success',
    'message' => 'Test endpoint is working!',
    'timestamp' => date('Y-m-d H:i:s'),
    'server_info' => array(
        'php_version' => PHP_VERSION,
        'server_software' => $_SERVER['SERVER_SOFTWARE'] ?? 'Unknown',
        'document_root' => $_SERVER['DOCUMENT_ROOT'] ?? 'Unknown'
    )
));
?> 