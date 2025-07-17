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
    
    // Check if tbl_pincode table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_pincode'");
    if ($tableCheck->rowCount() == 0) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_pincode does not exist',
            'data' => []
        ]);
        exit();
    }
    
    // Get table structure to check columns
    $columnsQuery = $pdo->query("DESCRIBE tbl_pincode");
    $columns = $columnsQuery->fetchAll(PDO::FETCH_COLUMN);
    
    // Check if required columns exist
    $requiredColumns = ['id', 'pincode', 'state_id', 'location_id', 'sub_location_id', 'status'];
    $missingColumns = array_diff($requiredColumns, $columns);
    
    if (!empty($missingColumns)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing required columns in tbl_pincode: ' . implode(', ', $missingColumns),
            'data' => [],
            'debug' => [
                'available_columns' => $columns,
                'required_columns' => $requiredColumns
            ]
        ]);
        exit();
    }
    
    // Fetch PIN codes with related information from other tables
    $query = "
        SELECT 
            p.id,
            p.pincode,
            p.state_id,
            p.location_id,
            p.sub_location_id,
            p.status,
            bs.branch_state_name,
            bl.branch_location,
            sl.sub_location
        FROM tbl_pincode p
        LEFT JOIN tbl_branch_state bs ON p.state_id = bs.id
        LEFT JOIN tbl_branch_location bl ON p.location_id = bl.id
        LEFT JOIN tbl_sub_location sl ON p.sub_location_id = sl.id
        ORDER BY p.pincode ASC
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $pincodes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($pincodes)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No PIN codes found',
            'data' => [],
            'count' => 0
        ]);
        exit();
    }
    
    // Format the response
    $formattedData = [];
    foreach ($pincodes as $pincode) {
        $formattedData[] = [
            'id' => $pincode['id'],
            'pincode' => $pincode['pincode'],
            'state_id' => $pincode['state_id'],
            'location_id' => $pincode['location_id'],
            'sub_location_id' => $pincode['sub_location_id'],
            'status' => $pincode['status'],
            'state_name' => $pincode['branch_state_name'] ?? 'N/A',
            'location_name' => $pincode['branch_location'] ?? 'N/A',
            'sub_location_name' => $pincode['sub_location'] ?? 'N/A'
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'PIN codes fetched successfully',
        'data' => $formattedData,
        'count' => count($formattedData)
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
        'data' => []
    ]);
}
?> 