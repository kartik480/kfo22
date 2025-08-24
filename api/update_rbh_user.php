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
    
    // Check if user exists
    $checkSql = "SELECT id FROM tbl_user WHERE id = ?";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([$input['user_id']]);
    
    if ($checkStmt->rowCount() == 0) {
        throw new Exception('User not found');
    }
    
    // Build update query dynamically based on provided fields
    $updateFields = [];
    $updateValues = [];
    
    $allowedFields = [
        'firstName', 'lastName', 'mobile', 'email_id', 'employee_no',
        'status', 'designation_id', 'department_id', 'branch_state_id',
        'branch_location_id', 'mobile', 'official_phone', 'official_email',
        'residential_address', 'office_address', 'pan_number', 'aadhaar_number'
    ];
    
    foreach ($allowedFields as $field) {
        if (isset($input[$field])) {
            $updateFields[] = "$field = ?";
            $updateValues[] = $input[$field];
        }
    }
    
    if (empty($updateFields)) {
        throw new Exception('No valid fields to update');
    }
    
    // Add updated_at timestamp
    $updateFields[] = "updated_at = NOW()";
    
    // Add user_id to values array
    $updateValues[] = $input['user_id'];
    
    $updateSql = "UPDATE tbl_user SET " . implode(', ', $updateFields) . " WHERE id = ?";
    
    $updateStmt = $conn->prepare($updateSql);
    $result = $updateStmt->execute($updateValues);
    
    if ($result) {
        $response = [
            'status' => 'success',
            'message' => 'RBH user updated successfully',
            'data' => [
                'user_id' => $input['user_id'],
                'updated_fields' => array_keys(array_filter($input, function($key) use ($allowedFields) {
                    return in_array($key, $allowedFields);
                }, ARRAY_FILTER_USE_KEY))
            ]
        ];
        
        echo json_encode($response, JSON_PRETTY_PRINT);
    } else {
        throw new Exception('Failed to update user');
    }
    
} catch (Exception $e) {
    error_log("RBH Update User Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
