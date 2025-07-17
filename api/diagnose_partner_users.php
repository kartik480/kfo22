<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

$diagnosis = [];

try {
    // Step 1: Test PHP execution
    $diagnosis['step1'] = 'PHP execution - OK';
    
    // Step 2: Test if db_config.php exists
    if (file_exists('db_config.php')) {
        $diagnosis['step2'] = 'db_config.php exists - OK';
    } else {
        throw new Exception('db_config.php file not found');
    }
    
    // Step 3: Test database configuration inclusion
    require_once 'db_config.php';
    $diagnosis['step3'] = 'db_config.php included - OK';
    
    // Step 4: Test if $conn variable exists
    if (isset($conn)) {
        $diagnosis['step4'] = '$conn variable exists - OK';
    } else {
        throw new Exception('$conn variable not set in db_config.php');
    }
    
    // Step 5: Test database connection
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    } else {
        $diagnosis['step5'] = 'Database connection - OK';
    }
    
    // Step 6: Test table existence
    $sql = "SHOW TABLES LIKE 'tbl_partner_users'";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception('Table check query failed: ' . $conn->error);
    }
    
    if ($result->num_rows > 0) {
        $diagnosis['step6'] = 'tbl_partner_users table exists - OK';
    } else {
        $diagnosis['step6'] = 'tbl_partner_users table does not exist';
        throw new Exception('tbl_partner_users table not found');
    }
    
    // Step 7: Test simple SELECT query
    $sql = "SELECT COUNT(*) as count FROM tbl_partner_users";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception('COUNT query failed: ' . $conn->error);
    }
    
    $row = $result->fetch_assoc();
    $count = $row['count'];
    $diagnosis['step7'] = "SELECT COUNT query - OK (Found $count records)";
    
    // Step 8: Test full SELECT query
    $sql = "SELECT id, username, first_name, last_name FROM tbl_partner_users LIMIT 1";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception('SELECT query failed: ' . $conn->error);
    }
    
    if ($result->num_rows > 0) {
        $diagnosis['step8'] = 'Full SELECT query - OK';
    } else {
        $diagnosis['step8'] = 'No data found in table';
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'All diagnostic tests passed',
        'diagnosis' => $diagnosis,
        'timestamp' => date('Y-m-d H:i:s')
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Diagnostic failed: ' . $e->getMessage(),
        'diagnosis' => $diagnosis,
        'failed_step' => count($diagnosis) + 1
    ]);
}
?> 