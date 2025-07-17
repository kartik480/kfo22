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
    $conn = getConnection();
    
    // Check if table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sub_location'");
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    
    if (!$tableExists) {
        echo json_encode(array(
            'success' => false,
            'error' => 'Table tbl_sub_location does not exist',
            'available_tables' => array()
        ));
        exit();
    }
    
    // Get table structure
    $stmt = $conn->prepare("DESCRIBE tbl_sub_location");
    $stmt->execute();
    $structure = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get sample data
    $stmt = $conn->prepare("SELECT * FROM tbl_sub_location LIMIT 5");
    $stmt->execute();
    $sampleData = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sub_location");
    $stmt->execute();
    $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Get all available tables that might contain sub location data
    $stmt = $conn->prepare("SHOW TABLES");
    $stmt->execute();
    $allTables = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Filter tables that might be related to sub locations
    $relatedTables = array_filter($allTables, function($table) {
        return stripos($table, 'sub') !== false || stripos($table, 'location') !== false;
    });
    
    echo json_encode(array(
        'success' => true,
        'table_exists' => $tableExists,
        'table_structure' => $structure,
        'sample_data' => $sampleData,
        'total_count' => $totalCount,
        'related_tables' => array_values($relatedTables),
        'all_tables' => $allTables
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Debug sub location table error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while debugging sub location table: ' . $e->getMessage()
    ));
}
?> 