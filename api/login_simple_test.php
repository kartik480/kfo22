<?php
header('Content-Type: application/json');

$response = array();

// Direct database connection test
$host = "p3plzcpnl508816.prod.phx3.secureserver.net";
$dbname = "emp_kfinone";
$username = "emp_kfinone";
$password = "*F*im1!Y0D25";

try {
    $conn = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response['connection_successful'] = true;
    $response['message'] = 'Database connection successful';
    
    // Test if tbl_user table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
    $stmt->execute();
    $tableExists = $stmt->fetch();
    
    if ($tableExists) {
        $response['table_exists'] = true;
        
        // Get user count
        $stmt = $conn->prepare("SELECT COUNT(*) as count FROM tbl_user");
        $stmt->execute();
        $userCount = $stmt->fetch(PDO::FETCH_ASSOC);
        $response['user_count'] = $userCount['count'];
        
        // Get sample users
        $stmt = $conn->prepare("SELECT id, username FROM tbl_user LIMIT 3");
        $stmt->execute();
        $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['sample_users'] = $users;
        
    } else {
        $response['table_exists'] = false;
    }
    
    $conn = null;
    
} catch (Exception $e) {
    $response['connection_successful'] = false;
    $response['message'] = 'Database connection failed: ' . $e->getMessage();
}

echo json_encode($response, JSON_PRETTY_PRINT);
?> 