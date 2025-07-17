<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, let's check if the table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_agent_data'");
    if ($stmt->rowCount() == 0) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_agent_data does not exist',
            'data' => []
        ]);
        exit();
    }
    
    // Get table structure to see what columns exist
    $stmt = $pdo->query("DESCRIBE tbl_agent_data");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $columnNames = array_column($columns, 'Field');
    
    // Count total records
    $stmt = $pdo->query("SELECT COUNT(*) as total FROM tbl_agent_data");
    $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalRecords = $countResult['total'];
    
    // Fetch ALL records from tbl_agent_data - NO FILTERS, NO JOINS
    $selectSQL = "SELECT * FROM tbl_agent_data ORDER BY id DESC";
    $result = $pdo->query($selectSQL);
    
    if (!$result) {
        throw new Exception("Error fetching agent data");
    }
    
    $agents = [];
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $agents[] = $row; // Return all columns as they are
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'All agent data fetched successfully',
        'total_records' => $totalRecords,
        'columns' => $columnNames,
        'data' => $agents
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 