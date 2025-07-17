<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Debug: Log what we receive
$rawInput = file_get_contents('php://input');
$postData = $_POST;
$jsonData = json_decode($rawInput, true);

echo json_encode([
    'success' => false,
    'debug' => [
        'method' => $_SERVER['REQUEST_METHOD'],
        'raw_input' => $rawInput,
        'post_data' => $postData,
        'json_data' => $jsonData,
        'content_type' => $_SERVER['CONTENT_TYPE'] ?? 'not set'
    ]
]);
?> 