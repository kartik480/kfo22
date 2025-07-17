<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$response = array();

try {
    $conn = getConnection();
    
    // Check if user with ID 8 exists
    $stmt = $conn->prepare("SELECT * FROM tbl_user WHERE id = 8");
    $stmt->execute();
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($user) {
        $response['success'] = true;
        $response['message'] = 'User with ID 8 found';
        $response['user'] = array(
            'id' => $user['id'],
            'username' => $user['username'],
            'firstName' => $user['firstName'] ?? '',
            'lastName' => $user['lastName'] ?? '',
            'email' => $user['email'] ?? '',
            'status' => $user['status'] ?? ''
        );
    } else {
        $response['success'] = false;
        $response['message'] = 'User with ID 8 not found';
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    $response['success'] = false;
    $response['message'] = 'Database error: ' . $e->getMessage();
}

echo json_encode($response, JSON_PRETTY_PRINT);
?> 