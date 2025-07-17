<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    // Check if tbl_users table exists
    $sql = "SHOW TABLES LIKE 'tbl_users'";
    $result = $conn->query($sql);
    
    if ($result->num_rows == 0) {
        // Table doesn't exist, return sample data
        $users = array(
            array('id' => '1', 'name' => 'Admin User'),
            array('id' => '2', 'name' => 'Manager User'),
            array('id' => '3', 'name' => 'Supervisor User')
        );
        
        echo json_encode([
            'status' => 'success',
            'data' => $users,
            'message' => 'Sample users data (table not found)'
        ]);
        exit;
    }
    
    // Fetch users for dropdown
    $sql = "SELECT id, username, first_name, last_name FROM tbl_users WHERE status = 'Active' ORDER BY first_name, last_name ASC";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $users = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            if (empty($fullName)) {
                $fullName = $row['username'];
            }
            
            $users[] = array(
                'id' => $row['id'],
                'name' => $fullName
            );
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $users,
        'message' => 'Users fetched successfully'
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 