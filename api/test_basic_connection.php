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
    
    // Test 1: Check if tables exist
    $tables = ['tbl_user', 'tbl_designation', 'tbl_partner_users', 'tbl_department'];
    $tableExists = [];
    
    foreach ($tables as $table) {
        $stmt = $pdo->prepare("SHOW TABLES LIKE ?");
        $stmt->execute([$table]);
        $tableExists[$table] = $stmt->rowCount() > 0;
    }
    
    // Test 2: Count records in each table
    $counts = [];
    foreach ($tables as $table) {
        if ($tableExists[$table]) {
            $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM $table");
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            $counts[$table] = $result['count'];
        } else {
            $counts[$table] = 0;
        }
    }
    
    // Test 3: Check for CBO/RBH designations
    $designationSql = "SELECT id, designation_name FROM tbl_designation 
                       WHERE LOWER(designation_name) LIKE '%chief%' 
                       OR LOWER(designation_name) LIKE '%cbo%'
                       OR LOWER(designation_name) LIKE '%regional%'
                       OR LOWER(designation_name) LIKE '%rbh%'
                       ORDER BY designation_name";
    $designationStmt = $pdo->prepare($designationSql);
    $designationStmt->execute();
    $designations = $designationStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Test 4: Check for users with these designations
    $userSql = "SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE (LOWER(d.designation_name) LIKE '%chief%' 
                       AND LOWER(d.designation_name) LIKE '%business%' 
                       AND LOWER(d.designation_name) LIKE '%officer%'
                       OR LOWER(d.designation_name) LIKE '%cbo%'
                       OR (LOWER(d.designation_name) LIKE '%regional%' 
                           AND LOWER(d.designation_name) LIKE '%business%' 
                           AND LOWER(d.designation_name) LIKE '%head%')
                       OR LOWER(d.designation_name) LIKE '%rbh%')
                LIMIT 5";
    $userStmt = $pdo->prepare($userSql);
    $userStmt->execute();
    $users = $userStmt->fetchAll(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'Basic connection test completed',
        'debug_info' => [
            'tables_exist' => $tableExists,
            'table_counts' => $counts,
            'cbo_rbh_designations' => $designations,
            'cbo_rbh_users_sample' => $users
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'sql_state' => $e->getCode()
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