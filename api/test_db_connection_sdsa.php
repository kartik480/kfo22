<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

// Include database configuration
require_once 'db_config.php';

try {
    // Use the existing connection from db_config.php
    global $conn;
    
    // Check if connection exists and is valid
    if (!$conn) {
        throw new Exception("Database connection not available");
    }
    
    // Test a simple query
    $test_sql = "SELECT COUNT(*) as total_users FROM tbl_user";
    $result = $conn->query($test_sql);
    
    if (!$result) {
        throw new Exception("Test query failed: " . $conn->error);
    }
    
    $row = $result->fetch_assoc();
    $total_users = $row['total_users'];
    
    // Test the specific query for SDSA team
    $sdsa_sql = "SELECT COUNT(*) as sdsa_count FROM tbl_user WHERE reportingTo = '8' AND status = 'Active'";
    $sdsa_result = $conn->query($sdsa_sql);
    
    if (!$sdsa_result) {
        throw new Exception("SDSA query failed: " . $conn->error);
    }
    
    $sdsa_row = $sdsa_result->fetch_assoc();
    $sdsa_count = $sdsa_row['sdsa_count'];
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Database connection test successful',
        'data' => array(
            'total_users' => $total_users,
            'sdsa_team_members' => $sdsa_count,
            'connection_status' => 'Connected'
        )
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database test failed: ' . $e->getMessage()
    ));
}
?> 