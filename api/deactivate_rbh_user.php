<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Only POST method is allowed');
    }
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        throw new Exception('Invalid JSON input');
    }
    
    // Validate required fields
    if (empty($input['user_id'])) {
        throw new Exception('User ID is required');
    }
    
    if (!isset($input['status'])) {
        throw new Exception('Status is required');
    }
    
    // Validate status values
    $allowedStatuses = ['Active', 'Inactive', 'Suspended', 'Terminated'];
    if (!in_array($input['status'], $allowedStatuses)) {
        throw new Exception('Invalid status. Allowed values: ' . implode(', ', $allowedStatuses));
    }
    
    // Check if user exists
    $checkSql = "SELECT id, username, status FROM tbl_user WHERE id = ?";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([$input['user_id']]);
    
    if ($checkStmt->rowCount() == 0) {
        throw new Exception('User not found');
    }
    
    $user = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    // Update user status
    $updateSql = "UPDATE tbl_user SET status = ?, updated_at = NOW() WHERE id = ?";
    $updateStmt = $conn->prepare($updateSql);
    $result = $updateStmt->execute([$input['status'], $input['user_id']]);
    
    if ($result) {
        $response = [
            'status' => 'success',
            'message' => 'User status updated successfully',
            'data' => [
                'user_id' => $input['user_id'],
                'username' => $user['username'],
                'old_status' => $user['status'],
                'new_status' => $input['status'],
                'updated_at' => date('Y-m-d H:i:s')
            ]
        ];
        
        echo json_encode($response, JSON_PRETTY_PRINT);
    } else {
        throw new Exception('Failed to update user status');
    }
    
} catch (Exception $e) {
    error_log("RBH Deactivate User Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
