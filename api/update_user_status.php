<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration for kfinone database
$db_host = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_name = "emp_kfinone";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";

try {
    // Create connection
    $conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);
    
    if (!$conn) {
        throw new Exception('Connection failed: ' . mysqli_connect_error());
    }
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    if (!$data) {
        throw new Exception('Invalid JSON input');
    }
    
    $userId = $data['userId'] ?? '';
    $status = $data['status'] ?? '';
    
    if (empty($userId)) {
        throw new Exception('User ID is required');
    }
    
    if (empty($status)) {
        throw new Exception('Status is required');
    }
    
    // Update user status in tbl_user table
    $sql = "UPDATE tbl_user SET status = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }
    
    $stmt->bind_param("si", $status, $userId);
    
    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                'status' => 'success',
                'message' => 'User status updated successfully',
                'affected_rows' => $stmt->affected_rows
            ]);
        } else {
            echo json_encode([
                'status' => 'warning',
                'message' => 'No changes made. User may not exist or status is the same.',
                'affected_rows' => $stmt->affected_rows
            ]);
        }
    } else {
        throw new Exception("Execute failed: " . $stmt->error);
    }
    
    $stmt->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage()
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 