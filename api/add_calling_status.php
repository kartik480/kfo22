<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Include database configuration
require_once 'db_config_fixed.php';

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array(
        'success' => false,
        'message' => 'Method not allowed. Use POST.'
    ));
    exit();
}

try {
    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['calling_status'])) {
        http_response_code(400);
        echo json_encode(array(
            'success' => false,
            'message' => 'Missing calling_status parameter'
        ));
        exit();
    }
    
    $callingStatus = trim($input['calling_status']);
    
    if (empty($callingStatus)) {
        http_response_code(400);
        echo json_encode(array(
            'success' => false,
            'message' => 'Calling status cannot be empty'
        ));
        exit();
    }
    
    $conn = getConnection();
    
    // Check if calling status already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_partner_calling_status WHERE calling_status = ?");
    $checkStmt->execute([$callingStatus]);
    
    if ($checkStmt->rowCount() > 0) {
        http_response_code(409);
        echo json_encode(array(
            'success' => false,
            'message' => 'Calling status already exists'
        ));
        exit();
    }
    
    // Insert new calling status
    $stmt = $conn->prepare("INSERT INTO tbl_partner_calling_status (calling_status) VALUES (?)");
    $result = $stmt->execute([$callingStatus]);
    
    if ($result) {
        $newId = $conn->lastInsertId();
        echo json_encode(array(
            'success' => true,
            'message' => 'Calling status added successfully',
            'data' => array(
                'id' => $newId,
                'calling_status' => $callingStatus
            )
        ));
    } else {
        http_response_code(500);
        echo json_encode(array(
            'success' => false,
            'message' => 'Failed to add calling status'
        ));
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    // Log the error and return a generic error message
    error_log("Add calling status error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'An error occurred while adding calling status: ' . $e->getMessage()
    ));
}
?> 