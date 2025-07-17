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
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        throw new Exception('Invalid JSON input');
    }
    
    // Validate required fields
    if (empty($input['calling_sub_status'])) {
        throw new Exception('Calling sub status is required');
    }
    
    if (empty($input['calling_status_id'])) {
        throw new Exception('Calling status ID is required');
    }
    
    $callingSubStatus = trim($input['calling_sub_status']);
    $callingStatusId = intval($input['calling_status_id']);
    
    // Validate calling status ID exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_partner_calling_status WHERE id = ? AND status = 1");
    $checkStmt->execute(array($callingStatusId));
    
    if ($checkStmt->rowCount() == 0) {
        throw new Exception('Invalid calling status ID');
    }
    
    // Check if calling sub status already exists for this calling status
    $duplicateStmt = $conn->prepare("
        SELECT id FROM tbl_partner_calling_sub_status 
        WHERE calling_sub_status = ? AND calling_status_id = ? AND status = 1
    ");
    $duplicateStmt->execute(array($callingSubStatus, $callingStatusId));
    
    if ($duplicateStmt->rowCount() > 0) {
        throw new Exception('Calling sub status already exists for this calling status');
    }
    
    // Insert new calling sub status
    $insertStmt = $conn->prepare("
        INSERT INTO tbl_partner_calling_sub_status 
        (calling_sub_status, calling_status_id, status) 
        VALUES (?, ?, 1)
    ");
    $insertStmt->execute(array($callingSubStatus, $callingStatusId));
    
    $newId = $conn->lastInsertId();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling sub status added successfully.',
        'data' => array(
            'id' => $newId,
            'calling_sub_status' => $callingSubStatus,
            'calling_status_id' => $callingStatusId,
            'status' => 1
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Add calling sub status error: " . $e->getMessage());
    http_response_code(400);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding calling sub status: ' . $e->getMessage()
    ));
}
?> 