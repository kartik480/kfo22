<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Debug information
$debug_info = [
    'request_method' => $_SERVER['REQUEST_METHOD'],
    'request_uri' => $_SERVER['REQUEST_URI'],
    'script_name' => $_SERVER['SCRIPT_NAME'],
    'file_exists' => file_exists(__FILE__),
    'current_time' => date('Y-m-d H:i:s'),
    'php_version' => PHP_VERSION
];

try {
    // Database configuration
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';

    // Test database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $debug_info['database_connected'] = true;
    
    // Check if tbl_sub_location table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_sub_location'");
    $tableExists = $tableCheck->rowCount() > 0;
    $debug_info['table_exists'] = $tableExists;
    
    if (!$tableExists) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_sub_location does not exist',
            'data' => [],
            'debug' => $debug_info
        ]);
        exit();
    }
    
    // Get table structure
    $columnsQuery = $pdo->query("DESCRIBE tbl_sub_location");
    $columns = $columnsQuery->fetchAll(PDO::FETCH_COLUMN);
    $debug_info['available_columns'] = $columns;
    
    // Check if sub_location column exists
    $hasSubLocationColumn = in_array('sub_location', $columns);
    $debug_info['has_sub_location_column'] = $hasSubLocationColumn;
    
    if (!$hasSubLocationColumn) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Column sub_location does not exist in tbl_sub_location',
            'data' => [],
            'debug' => $debug_info
        ]);
        exit();
    }
    
    // Fetch sub locations with status = 1 (active)
    $query = "SELECT id, sub_location, state_id, location_id, status, created_at, updated_at 
              FROM tbl_sub_location 
              WHERE status = 1 
              ORDER BY sub_location ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $subLocations = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $debug_info['query_executed'] = true;
    $debug_info['records_found'] = count($subLocations);
    
    if (empty($subLocations)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No sub locations found',
            'data' => [],
            'debug' => $debug_info
        ]);
        exit();
    }
    
    // Format the response
    $formattedData = [];
    foreach ($subLocations as $subLocation) {
        $formattedData[] = [
            'id' => $subLocation['id'],
            'sub_location' => $subLocation['sub_location'],
            'state_id' => $subLocation['state_id'],
            'location_id' => $subLocation['location_id'],
            'status' => $subLocation['status'],
            'created_at' => $subLocation['created_at'],
            'updated_at' => $subLocation['updated_at']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Sub locations fetched successfully',
        'data' => $formattedData,
        'count' => count($formattedData),
        'debug' => $debug_info
    ]);
    
} catch (PDOException $e) {
    $debug_info['database_error'] = $e->getMessage();
    $debug_info['error_code'] = $e->getCode();
    
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => [],
        'debug' => $debug_info
    ]);
} catch (Exception $e) {
    $debug_info['general_error'] = $e->getMessage();
    $debug_info['error_type'] = get_class($e);
    
    echo json_encode([
        'status' => 'error',
        'message' => 'General error: ' . $e->getMessage(),
        'data' => [],
        'debug' => $debug_info
    ]);
}
?> 