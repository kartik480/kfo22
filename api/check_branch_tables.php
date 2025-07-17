<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    $results = array();
    
    // Check what tables exist with "branch" in the name
    $sql = "SHOW TABLES LIKE '%branch%'";
    $result = $conn->query($sql);
    
    $branchTables = array();
    while ($row = $result->fetch_array()) {
        $branchTables[] = $row[0];
    }
    $results['branch_tables_found'] = $branchTables;
    
    // Check each branch table for data
    foreach ($branchTables as $table) {
        $sql = "SELECT COUNT(*) as count FROM `$table`";
        $result = $conn->query($sql);
        if ($result) {
            $row = $result->fetch_assoc();
            $results['table_counts'][$table] = $row['count'];
            
            // Get sample data from each table
            if ($row['count'] > 0) {
                $sql = "SELECT * FROM `$table` LIMIT 5";
                $result2 = $conn->query($sql);
                $sampleData = array();
                while ($row2 = $result2->fetch_assoc()) {
                    $sampleData[] = $row2;
                }
                $results['sample_data'][$table] = $sampleData;
            }
        }
    }
    
    // Check if the specific tables we need exist
    $requiredTables = array('tbl_branch_state', 'tbl_branch_location');
    foreach ($requiredTables as $table) {
        $sql = "SHOW TABLES LIKE '$table'";
        $result = $conn->query($sql);
        $results['required_tables'][$table] = $result->num_rows > 0;
    }
    
    // Check for any table with similar names
    $sql = "SHOW TABLES LIKE '%state%'";
    $result = $conn->query($sql);
    $stateTables = array();
    while ($row = $result->fetch_array()) {
        $stateTables[] = $row[0];
    }
    $results['state_tables'] = $stateTables;
    
    $sql = "SHOW TABLES LIKE '%location%'";
    $result = $conn->query($sql);
    $locationTables = array();
    while ($row = $result->fetch_array()) {
        $locationTables[] = $row[0];
    }
    $results['location_tables'] = $locationTables;
    
    echo json_encode([
        'success' => true,
        'message' => 'Branch tables check completed',
        'data' => $results
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => null
    ]);
}
?> 