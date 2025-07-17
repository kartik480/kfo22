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
require_once 'db_config_fixed.php';

try {
    $conn = getConnection();
    
    // Check if table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_partner_calling_status'");
    $tableExists = $tableCheck->rowCount() > 0;
    
    if (!$tableExists) {
        echo json_encode(array(
            'success' => false,
            'message' => 'Table tbl_partner_calling_status does not exist',
            'debug_info' => array(
                'table_exists' => false,
                'available_tables' => array()
            )
        ));
        exit();
    }
    
    // Get table structure
    $structureQuery = $conn->query("DESCRIBE tbl_partner_calling_status");
    $structure = $structureQuery->fetchAll(PDO::FETCH_ASSOC);
    
    // Check if table has data
    $countQuery = $conn->query("SELECT COUNT(*) as count FROM tbl_partner_calling_status");
    $count = $countQuery->fetch(PDO::FETCH_ASSOC);
    
    // Try to get some sample data
    $sampleQuery = $conn->query("SELECT * FROM tbl_partner_calling_status LIMIT 5");
    $sampleData = $sampleQuery->fetchAll(PDO::FETCH_ASSOC);
    
    // Check for calling_status column specifically
    $hasCallingStatusColumn = false;
    foreach ($structure as $column) {
        if ($column['Field'] === 'calling_status') {
            $hasCallingStatusColumn = true;
            break;
        }
    }
    
    echo json_encode(array(
        'success' => true,
        'message' => 'Debug information retrieved successfully',
        'debug_info' => array(
            'table_exists' => true,
            'table_structure' => $structure,
            'has_calling_status_column' => $hasCallingStatusColumn,
            'total_records' => $count['count'],
            'sample_data' => $sampleData,
            'connection_status' => 'Connected successfully'
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Debug calling status error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'Debug error: ' . $e->getMessage(),
        'debug_info' => array(
            'error_type' => get_class($e),
            'error_message' => $e->getMessage(),
            'error_file' => $e->getFile(),
            'error_line' => $e->getLine()
        )
    ));
}
?> 