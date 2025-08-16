<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
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
    
    // First, check if the table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($stmt->rowCount() == 0) {
        throw new Exception("Table 'tbl_manage_icon' does not exist");
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_manage_icon");
    $columns = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Build dynamic SELECT query based on available columns
    $selectColumns = [];
    if (in_array('id', $columns)) $selectColumns[] = 'id';
    if (in_array('icon_name', $columns)) $selectColumns[] = 'icon_name';
    if (in_array('icon_url', $columns)) $selectColumns[] = 'icon_url';
    if (in_array('icon_description', $columns)) $selectColumns[] = 'icon_description';
    if (in_array('status', $columns)) $selectColumns[] = 'status';
    
    if (empty($selectColumns)) {
        throw new Exception("No valid columns found in tbl_manage_icon table");
    }
    
    $selectClause = implode(', ', $selectColumns);
    $query = "SELECT $selectClause FROM tbl_manage_icon ORDER BY icon_name ASC";
    
    // Execute the query
    $stmt = $pdo->query($query);
    $icons = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Prepare response
    $response = [
        'success' => true,
        'total_icons' => count($icons),
        'table_structure' => $columns,
        'query_executed' => $query,
        'icons' => $icons
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ];
    echo json_encode($response, JSON_PRETTY_PRINT);
} catch (Exception $e) {
    $response = [
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage()
    ];
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
