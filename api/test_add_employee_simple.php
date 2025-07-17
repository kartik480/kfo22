<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    
    global $conn;
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }

    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        throw new Exception('Invalid JSON input: ' . json_last_error_msg());
    }

    // Log the input
    error_log("Input received: " . print_r($input, true));

    // Simple insert with only basic required fields
    $sql = "INSERT INTO tbl_user (firstName, lastName, employee_no, password, status, username) VALUES (?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        throw new Exception('Prepare failed: ' . $conn->error);
    }
    
    $firstName = $input['firstName'] ?? '';
    $lastName = $input['lastName'] ?? '';
    $employeeId = $input['employeeId'] ?? '';
    $password = $input['password'] ?? '';
    $status = 'Active';
    $username = $employeeId;
    
    $stmt->bind_param('ssssss', $firstName, $lastName, $employeeId, $password, $status, $username);
    
    if ($stmt->execute()) {
        $result = [
            'status' => 'success', 
            'message' => 'Employee added successfully', 
            'user_id' => $conn->insert_id,
            'data_sent' => $input
        ];
        echo json_encode($result);
    } else {
        throw new Exception('Failed to insert employee: ' . $stmt->error);
    }

    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Error in test_add_employee_simple.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'status' => 'error', 
        'message' => $e->getMessage(),
        'input_received' => isset($input) ? $input : 'No input'
    ]);
}
?> 