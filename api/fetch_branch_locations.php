<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if tbl_branch_location table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_branch_location'");
    if ($stmt->rowCount() == 0) {
        echo json_encode([
            'success' => false,
            'message' => 'Table tbl_branch_location does not exist',
            'data' => []
        ]);
        exit;
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_branch_location");
    $columns = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    if (!in_array('branch_location', $columns)) {
        echo json_encode([
            'success' => false,
            'message' => 'Column branch_location does not exist in tbl_branch_location',
            'available_columns' => $columns,
            'data' => []
        ]);
        exit;
    }
    
    // Fetch all branch locations
    $stmt = $pdo->query("SELECT DISTINCT branch_location FROM tbl_branch_location WHERE branch_location IS NOT NULL AND branch_location != '' ORDER BY branch_location");
    $locations = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'message' => 'Branch locations fetched successfully',
        'count' => count($locations),
        'data' => $locations
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 