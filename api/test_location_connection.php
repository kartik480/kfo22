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

// Include database configuration
require_once 'db_config.php';

try {
    // Test database connection
    $conn = getConnection();
    
    if (!$conn) {
        throw new Exception("Database connection failed - getConnection() returned null");
    }
    
    // Test a simple query
    $testQuery = $conn->query("SELECT 1 as test");
    if (!$testQuery) {
        throw new Exception("Database query test failed");
    }
    
    // Check if tbl_location table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_location'");
    $tableExists = $tableCheck && $tableCheck->rowCount() > 0;
    
    // If table exists, get some info about it
    $tableInfo = null;
    if ($tableExists) {
        $countQuery = $conn->query("SELECT COUNT(*) as count FROM tbl_location");
        $countResult = $countQuery->fetch(PDO::FETCH_ASSOC);
        $tableInfo = array(
            'exists' => true,
            'record_count' => $countResult['count']
        );
    }
    
    echo json_encode(array(
        'success' => true,
        'message' => 'Database connection test successful',
        'data' => array(
            'connection_status' => 'Connected',
            'database_test' => 'Passed',
            'tbl_location_table' => $tableInfo ?: array('exists' => false)
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Location connection test error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Connection test failed: ' . $e->getMessage(),
        'details' => 'Please check database configuration and connection'
    ));
}
?> 