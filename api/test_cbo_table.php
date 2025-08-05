<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    // Check if table exists
    $tableCheckSql = "SHOW TABLES LIKE 'tbl_cbo_users'";
    $tableStmt = $conn->prepare($tableCheckSql);
    $tableStmt->execute();
    $tableExists = $tableStmt->rowCount() > 0;
    
    if (!$tableExists) {
        throw new Exception('Table tbl_cbo_users does not exist');
    }
    
    // Get table structure
    $structureSql = "DESCRIBE tbl_cbo_users";
    $structureStmt = $conn->prepare($structureSql);
    $structureStmt->execute();
    $structure = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get sample data (first 5 records)
    $sampleSql = "SELECT * FROM tbl_cbo_users LIMIT 5";
    $sampleStmt = $conn->prepare($sampleSql);
    $sampleStmt->execute();
    $sampleData = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total FROM tbl_cbo_users";
    $countStmt = $conn->prepare($countSql);
    $countStmt->execute();
    $count = $countStmt->fetch(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'Table tbl_cbo_users exists and is accessible',
        'data' => [
            'table_exists' => true,
            'total_records' => $count['total'],
            'structure' => $structure,
            'sample_data' => $sampleData
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("Test CBO Table Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => [
            'table_exists' => false,
            'total_records' => 0,
            'structure' => [],
            'sample_data' => []
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 