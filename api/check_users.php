<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$response = array();

try {
    $conn = getConnection();
    
    // Check if tbl_user table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
    $stmt->execute();
    $tableExists = $stmt->fetch();
    
    if ($tableExists) {
        // Get all users from the table
        $stmt = $conn->prepare("SELECT id, username, password FROM tbl_user");
        $stmt->execute();
        $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        $response['success'] = true;
        $response['message'] = 'Users found in database';
        $response['user_count'] = count($users);
        $response['users'] = $users;
        
    } else {
        $response['success'] = false;
        $response['message'] = 'tbl_user table does not exist';
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    $response['success'] = false;
    $response['message'] = 'Database error: ' . $e->getMessage();
}

echo json_encode($response, JSON_PRETTY_PRINT);
?> 