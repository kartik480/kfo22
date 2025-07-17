<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

try {
    // Include database configuration
    require_once 'db_config.php';
    
    // Test connection
    $conn = getConnection();
    
    if ($conn) {
        echo json_encode(array(
            'success' => true,
            'message' => 'Database connection successful',
            'timestamp' => date('Y-m-d H:i:s')
        ));
    } else {
        echo json_encode(array(
            'success' => false,
            'error' => 'Failed to get database connection'
        ));
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Database connection test error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database connection failed: ' . $e->getMessage()
    ));
}
?> 