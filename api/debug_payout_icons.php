<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database connection
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$username = "emp_kfinone";
$password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Debug: Check what's in tbl_user payout_icons column
    $debug_sql = "SELECT 
                    id,
                    firstName,
                    lastName,
                    payout_icons,
                    status
                  FROM tbl_user 
                  WHERE payout_icons IS NOT NULL 
                  LIMIT 10";
    
    $debug_stmt = $conn->prepare($debug_sql);
    $debug_stmt->execute();
    $debug_results = $debug_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check total count of users with payout_icons
    $count_sql = "SELECT COUNT(*) as total FROM tbl_user WHERE payout_icons IS NOT NULL AND payout_icons != ''";
    $count_stmt = $conn->prepare($count_sql);
    $count_stmt->execute();
    $total_count = $count_stmt->fetch(PDO::FETCH_ASSOC);
    
    // Check if payout_icons column exists and its structure
    $column_sql = "DESCRIBE tbl_user";
    $column_stmt = $conn->prepare($column_sql);
    $column_stmt->execute();
    $columns = $column_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Debug information for payout_icons',
        'debug_data' => [
            'total_users_with_payout_icons' => $total_count['total'],
            'sample_users' => $debug_results,
            'table_structure' => $columns
        ]
    ]);
    
} catch(PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage()
    ]);
}

$conn = null;
?>
