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
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sdsa_users'");
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    
    if (!$tableExists) {
        echo json_encode(array(
            'success' => false,
            'error' => 'Table tbl_sdsa_users does not exist',
            'available_tables' => array()
        ));
        exit();
    }
    
    // Get table structure
    $stmt = $conn->prepare("DESCRIBE tbl_sdsa_users");
    $stmt->execute();
    $structure = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sdsa_users");
    $stmt->execute();
    $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Get inactive count (status = 0)
    $stmt = $conn->prepare("SELECT COUNT(*) as inactive FROM tbl_sdsa_users WHERE status = 0");
    $stmt->execute();
    $inactiveResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $inactiveCount = $inactiveResult['inactive'];
    
    // Get active count (status = 1)
    $stmt = $conn->prepare("SELECT COUNT(*) as active FROM tbl_sdsa_users WHERE status = 1");
    $stmt->execute();
    $activeResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $activeCount = $activeResult['active'];
    
    // Get sample data (both active and inactive)
    $stmt = $conn->prepare("SELECT * FROM tbl_sdsa_users ORDER BY status ASC, name ASC LIMIT 10");
    $stmt->execute();
    $sampleData = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get all available tables that might contain SDSA data
    $stmt = $conn->prepare("SHOW TABLES");
    $stmt->execute();
    $allTables = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Filter tables that might be related to SDSA users
    $relatedTables = array_filter($allTables, function($table) {
        return stripos($table, 'sdsa') !== false || stripos($table, 'user') !== false;
    });
    
    echo json_encode(array(
        'success' => true,
        'table_exists' => $tableExists,
        'table_structure' => $structure,
        'sample_data' => $sampleData,
        'total_count' => $totalCount,
        'inactive_count' => $inactiveCount,
        'active_count' => $activeCount,
        'related_tables' => array_values($relatedTables),
        'all_tables' => $allTables
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Debug SDSA users table error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while debugging SDSA users table: ' . $e->getMessage()
    ));
}
?> 