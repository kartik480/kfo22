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

    // Database configuration
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if tbl_sub_location table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_sub_location'");
    if ($tableCheck->rowCount() == 0) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_sub_location does not exist',
            'data' => []
        ]);
        exit();
    }
    
    // Get table structure to check columns
    $columnsQuery = $pdo->query("DESCRIBE tbl_sub_location");
    $columns = $columnsQuery->fetchAll(PDO::FETCH_COLUMN);
    
    // Check if required columns exist
    $requiredColumns = ['id', 'sub_location', 'state_id', 'location_id', 'status'];
    $missingColumns = array_diff($requiredColumns, $columns);
    
    if (!empty($missingColumns)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing required columns: ' . implode(', ', $missingColumns),
            'data' => [],
            'debug' => [
                'available_columns' => $columns,
                'required_columns' => $requiredColumns
            ]
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
    
    if (empty($subLocations)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No sub locations found',
            'data' => [],
            'debug' => [
                'query' => $query,
                'table_exists' => true,
                'columns' => $columns
            ]
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
        'debug' => [
            'table_exists' => true,
            'columns' => $columns,
            'query' => $query
        ]
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => [],
        'debug' => [
            'error_code' => $e->getCode(),
            'error_file' => $e->getFile(),
            'error_line' => $e->getLine()
        ]
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'General error: ' . $e->getMessage(),
        'data' => [],
        'debug' => [
            'error_type' => get_class($e),
            'error_file' => $e->getFile(),
            'error_line' => $e->getLine()
        ]
    ]);
}
?> 