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
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get all users with their names
    $query = "SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName, status 
              FROM tbl_user 
              WHERE firstName IS NOT NULL 
              AND firstName != ''
              AND (firstName LIKE '%DUBEY%' OR firstName LIKE '%SATYA%' OR firstName LIKE '%SAI%' OR firstName LIKE '%BABA%'
                   OR lastName LIKE '%DUBEY%' OR lastName LIKE '%SATYA%' OR lastName LIKE '%SAI%' OR lastName LIKE '%BABA%')
              ORDER BY firstName ASC, lastName ASC
              LIMIT 50";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Also get total count of all users
    $countQuery = "SELECT COUNT(*) as total_users FROM tbl_user WHERE firstName IS NOT NULL AND firstName != ''";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute();
    $totalUsers = $countStmt->fetch(PDO::FETCH_ASSOC)['total_users'];
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Users with DUBEY/SATYA/SAI/BABA in their names found',
        'data' => [
            'total_users_in_db' => $totalUsers,
            'matching_users' => $users,
            'search_criteria' => 'firstName or lastName contains DUBEY, SATYA, SAI, or BABA'
        ],
        'timestamp' => date('Y-m-d H:i:s')
    ], JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 