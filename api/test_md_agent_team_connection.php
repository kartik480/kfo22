<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Test basic connection
    $response = [
        'success' => true,
        'message' => 'Database connection successful',
        'host' => $host,
        'database' => $dbname
    ];
    
    // Check if tables exist
    $tables = ['tbl_user', 'tbl_designation', 'tbl_agent_data'];
    $tableStatus = [];
    
    foreach ($tables as $table) {
        try {
            $stmt = $pdo->query("SHOW TABLES LIKE '$table'");
            $exists = $stmt->rowCount() > 0;
            $tableStatus[$table] = $exists ? 'exists' : 'not found';
        } catch (Exception $e) {
            $tableStatus[$table] = 'error: ' . $e->getMessage();
        }
    }
    
    $response['tables'] = $tableStatus;
    
    // Test user table structure
    try {
        $stmt = $pdo->query("DESCRIBE tbl_user");
        $userColumns = $stmt->fetchAll(PDO::FETCH_COLUMN);
        $response['tbl_user_columns'] = $userColumns;
    } catch (Exception $e) {
        $response['tbl_user_error'] = $e->getMessage();
    }
    
    // Test designation table structure
    try {
        $stmt = $pdo->query("DESCRIBE tbl_designation");
        $designationColumns = $stmt->fetchAll(PDO::FETCH_COLUMN);
        $response['tbl_designation_columns'] = $designationColumns;
    } catch (Exception $e) {
        $response['tbl_designation_error'] = $e->getMessage();
    }
    
    // Test agent_data table structure
    try {
        $stmt = $pdo->query("DESCRIBE tbl_agent_data");
        $agentColumns = $stmt->fetchAll(PDO::FETCH_COLUMN);
        $response['tbl_agent_data_columns'] = $agentColumns;
    } catch (Exception $e) {
        $response['tbl_agent_data_error'] = $e->getMessage();
    }
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database connection failed: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage()
    ]);
}
?>
