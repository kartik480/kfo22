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
    
    // Get all tables that might contain calling status data
    $tablesQuery = $conn->query("SHOW TABLES");
    $allTables = $tablesQuery->fetchAll(PDO::FETCH_COLUMN);
    
    // Filter tables that might be related to calling status
    $callingStatusTables = [];
    foreach ($allTables as $table) {
        if (stripos($table, 'calling') !== false || 
            stripos($table, 'status') !== false ||
            stripos($table, 'partner') !== false) {
            $callingStatusTables[] = $table;
        }
    }
    
    // Check each potential table for calling_status column
    $tableDetails = [];
    foreach ($callingStatusTables as $table) {
        try {
            $structureQuery = $conn->query("DESCRIBE `$table`");
            $structure = $structureQuery->fetchAll(PDO::FETCH_ASSOC);
            
            $hasCallingStatusColumn = false;
            foreach ($structure as $column) {
                if (stripos($column['Field'], 'calling') !== false && 
                    stripos($column['Field'], 'status') !== false) {
                    $hasCallingStatusColumn = true;
                    break;
                }
            }
            
            if ($hasCallingStatusColumn) {
                // Count records
                $countQuery = $conn->query("SELECT COUNT(*) as count FROM `$table`");
                $count = $countQuery->fetch(PDO::FETCH_ASSOC);
                
                // Get sample data
                $sampleQuery = $conn->query("SELECT * FROM `$table` LIMIT 3");
                $sampleData = $sampleQuery->fetchAll(PDO::FETCH_ASSOC);
                
                $tableDetails[] = [
                    'table_name' => $table,
                    'has_calling_status_column' => true,
                    'structure' => $structure,
                    'record_count' => $count['count'],
                    'sample_data' => $sampleData
                ];
            }
        } catch (Exception $e) {
            // Skip tables that can't be accessed
        }
    }
    
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling status tables check completed',
        'data' => array(
            'all_tables' => $allTables,
            'potential_calling_status_tables' => $callingStatusTables,
            'tables_with_calling_status_column' => $tableDetails
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Check calling status tables error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'Error checking tables: ' . $e->getMessage()
    ));
}
?> 