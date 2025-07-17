<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if tbl_agent_data table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_agent_data'");
    $tableExists = $stmt->rowCount() > 0;
    
    if (!$tableExists) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_agent_data does not exist',
            'table_exists' => false,
            'data_count' => 0
        ]);
        exit();
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_agent_data");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Count total records
    $stmt = $pdo->query("SELECT COUNT(*) as total FROM tbl_agent_data");
    $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalRecords = $countResult['total'];
    
    // Count active records
    $stmt = $pdo->query("SELECT COUNT(*) as active FROM tbl_agent_data WHERE status = 'Active' OR status IS NULL");
    $activeResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $activeRecords = $activeResult['active'];
    
    // Get sample data (first 3 records)
    $stmt = $pdo->query("SELECT * FROM tbl_agent_data LIMIT 3");
    $sampleData = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Table check completed',
        'table_exists' => true,
        'total_records' => $totalRecords,
        'active_records' => $activeRecords,
        'columns' => array_column($columns, 'Field'),
        'sample_data' => $sampleData
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'table_exists' => false,
        'data_count' => 0
    ]);
}
?> 