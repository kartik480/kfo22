<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

try {
    require_once 'db_config.php';
    global $conn;
    
    if (!$conn) {
        throw new Exception("Database connection is null");
    }
    
    if (!$conn->ping()) {
        throw new Exception("Database connection ping failed");
    }
    
    // Test a simple query
    $test_sql = "SELECT COUNT(*) as count FROM tbl_partner_users";
    $test_result = $conn->query($test_sql);
    
    if (!$test_result) {
        throw new Exception("Test query failed: " . $conn->error);
    }
    
    $row = $test_result->fetch_assoc();
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Database connection successful',
        'data' => [
            'total_partner_users' => $row['count']
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 