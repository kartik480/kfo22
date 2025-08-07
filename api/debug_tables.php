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
    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get all tables
    $tablesSql = "SHOW TABLES";
    $tablesStmt = $pdo->prepare($tablesSql);
    $tablesStmt->execute();
    $tables = $tablesStmt->fetchAll(PDO::FETCH_COLUMN);
    
    $response = [
        'status' => 'success',
        'message' => 'Database structure debug',
        'tables' => $tables,
        'table_details' => []
    ];
    
    // Check specific tables we're interested in
    $importantTables = ['tbl_user', 'tbl_partner_users', 'tbl_designation'];
    
    foreach ($importantTables as $tableName) {
        if (in_array($tableName, $tables)) {
            // Get table structure
            $structureSql = "DESCRIBE $tableName";
            $structureStmt = $pdo->prepare($structureSql);
            $structureStmt->execute();
            $structure = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
            
            // Get sample data (first 3 rows)
            $sampleSql = "SELECT * FROM $tableName LIMIT 3";
            $sampleStmt = $pdo->prepare($sampleSql);
            $sampleStmt->execute();
            $sampleData = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
            
            $response['table_details'][$tableName] = [
                'structure' => $structure,
                'sample_data' => $sampleData
            ];
        } else {
            $response['table_details'][$tableName] = 'Table not found';
        }
    }
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'General error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 