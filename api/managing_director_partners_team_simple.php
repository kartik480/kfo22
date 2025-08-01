<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    // Create a new connection
    $conn = new mysqli($db_host, $db_username, $db_password, $db_name);
    
    // Check connection
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    // Set charset to utf8
    $conn->set_charset("utf8");
    
    // Simple query to get all partner users first
    $sql = "SELECT 
                id,
                username,
                first_name,
                last_name,
                email_id,
                Phone_number,
                company_name,
                status,
                createdBy,
                created_at
            FROM tbl_partner_users 
            ORDER BY id DESC 
            LIMIT 10";
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Simple query failed: ' . $conn->error);
    }
    
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = [
            'id' => $row['id'],
            'username' => $row['username'],
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'email_id' => $row['email_id'],
            'Phone_number' => $row['Phone_number'],
            'company_name' => $row['company_name'],
            'status' => $row['status'],
            'createdBy' => $row['createdBy'],
            'created_at' => $row['created_at']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $users,
        'count' => count($users),
        'message' => 'Simple partner users query successful'
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 