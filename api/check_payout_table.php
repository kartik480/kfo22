<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if tbl_payout exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_payout'");
    $tableExists = $stmt->rowCount() > 0;
    
    if ($tableExists) {
        // Get table structure
        $stmt = $pdo->query("DESCRIBE tbl_payout");
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Count records
        $stmt = $pdo->query("SELECT COUNT(*) as count FROM tbl_payout");
        $count = $stmt->fetch(PDO::FETCH_ASSOC)['count'];
        
        // Get sample data (first 5 records)
        $stmt = $pdo->query("SELECT * FROM tbl_payout LIMIT 5");
        $sampleData = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Table tbl_payout exists',
            'table_exists' => true,
            'record_count' => $count,
            'columns' => $columns,
            'sample_data' => $sampleData
        ]);
    } else {
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_payout does not exist',
            'table_exists' => false
        ]);
    }
    
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 