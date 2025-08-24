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
    $requiredFields = ['username', 'firstName', 'lastName', 'mobile', 'email_id', 'reportingTo'];
    foreach ($requiredFields as $field) {
        if (empty($input[$field])) {
            throw new Exception("Field '$field' is required");
        }
    }
    
    // Check if username already exists
    $checkSql = "SELECT id FROM tbl_user WHERE username = ?";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([$input['username']]);
    
    if ($checkStmt->rowCount() > 0) {
        throw new Exception('Username already exists');
    }
    
    // Insert new user
    $insertSql = "
        INSERT INTO tbl_user (
            username, firstName, lastName, mobile, email_id, 
            employee_no, status, reportingTo, designation_id, 
            department_id, created_at, updated_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
    ";
    
    $insertStmt = $conn->prepare($insertSql);
    $result = $insertStmt->execute([
        $input['username'],
        $input['firstName'],
        $input['lastName'],
        $input['mobile'],
        $input['email_id'],
        $input['employee_no'] ?? null,
        $input['status'] ?? 'Active',
        $input['reportingTo'],
        $input['designation_id'] ?? null,
        $input['department_id'] ?? null
    ]);
    
    if ($result) {
        $userId = $conn->lastInsertId();
        
        $response = [
            'status' => 'success',
            'message' => 'RBH user created successfully',
            'data' => [
                'user_id' => $userId,
                'username' => $input['username']
            ]
        ];
        
        echo json_encode($response, JSON_PRETTY_PRINT);
    } else {
        throw new Exception('Failed to create user');
    }
    
} catch (Exception $e) {
    error_log("RBH Add User Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
