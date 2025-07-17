<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Include database configuration
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Check if tbl_location table exists
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_location LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
        echo json_encode(array(
            'success' => false,
            'error' => 'tbl_location table does not exist',
            'suggestion' => 'The table needs to be created first'
        ));
        exit();
    }
    
    // Get table structure
    $columnsQuery = "SHOW COLUMNS FROM tbl_location";
    $columnsResult = $conn->query($columnsQuery);
    
    if ($columnsResult === false) {
        throw new Exception("Failed to get table structure: " . $conn->error);
    }
    
    $columns = array();
    while ($column = $columnsResult->fetch_assoc()) {
        $columns[] = array(
            'field' => $column['Field'],
            'type' => $column['Type'],
            'null' => $column['Null'],
            'key' => $column['Key'],
            'default' => $column['Default'],
            'extra' => $column['Extra']
        );
    }
    
    // Get sample data (first 5 rows)
    $sampleQuery = "SELECT * FROM tbl_location LIMIT 5";
    $sampleResult = $conn->query($sampleQuery);
    
    $sampleData = array();
    if ($sampleResult && $sampleResult->num_rows > 0) {
        while ($row = $sampleResult->fetch_assoc()) {
            $sampleData[] = $row;
        }
    }
    
    // Get total count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_location";
    $countResult = $conn->query($countQuery);
    $countRow = $countResult->fetch_assoc();
    $totalCount = $countRow['total'];
    
    echo json_encode(array(
        'success' => true,
        'table_exists' => true,
        'table_name' => 'tbl_location',
        'columns' => $columns,
        'sample_data' => $sampleData,
        'total_records' => $totalCount,
        'message' => 'Table structure retrieved successfully'
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Debug location table error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while checking table structure: ' . $e->getMessage()
    ));
}
?> 