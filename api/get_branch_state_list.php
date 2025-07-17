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
    
    // Check if tbl_branch_state table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_branch_state'");
    if ($tableCheck->rowCount() == 0) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_branch_state does not exist',
            'data' => []
        ]);
        exit();
    }
    
    // Get table structure to check columns
    $columnsQuery = $pdo->query("DESCRIBE tbl_branch_state");
    $columns = $columnsQuery->fetchAll(PDO::FETCH_COLUMN);
    
    // Check if required columns exist
    $requiredColumns = ['id', 'branch_state_name', 'status'];
    $missingColumns = array_diff($requiredColumns, $columns);
    
    if (!empty($missingColumns)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing required columns in tbl_branch_state: ' . implode(', ', $missingColumns),
            'data' => [],
            'debug' => [
                'available_columns' => $columns,
                'required_columns' => $requiredColumns
            ]
        ]);
        exit();
    }
    
    // Fetch all branch states with all required columns
    $query = "
        SELECT 
            id,
            branch_state_name,
            status
        FROM tbl_branch_state 
        ORDER BY branch_state_name ASC
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $branchStates = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($branchStates)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No branch states found',
            'data' => [],
            'count' => 0
        ]);
        exit();
    }
    
    // Format the response
    $formattedData = [];
    foreach ($branchStates as $branchState) {
        $formattedData[] = [
            'id' => $branchState['id'],
            'branch_state_name' => $branchState['branch_state_name'],
            'status' => $branchState['status'] ?? 'Active'
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Branch states fetched successfully',
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