<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($input['designation_name']) || empty(trim($input['designation_name']))) {
        throw new Exception('Designation name is required');
    }
    
    $designationName = trim($input['designation_name']);
    
    // Check if designation already exists
    $checkQuery = "SELECT id FROM tbl_banker_designation WHERE designation_name = ?";
    $checkStmt = $conn->prepare($checkQuery);
    $checkStmt->bind_param('s', $designationName);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();
    
    if ($checkResult->num_rows > 0) {
        echo json_encode([
            'success' => false,
            'error' => 'Banker designation with this name already exists'
        ]);
        exit;
    }
    
    // Insert new designation
    $insertQuery = "INSERT INTO tbl_banker_designation (designation_name, created_at) VALUES (?, NOW())";
    $insertStmt = $conn->prepare($insertQuery);
    $insertStmt->bind_param('s', $designationName);
    
    if ($insertStmt->execute()) {
        $designationId = $conn->insert_id;
        
        echo json_encode([
            'success' => true,
            'message' => 'Banker designation added successfully',
            'data' => [
                'id' => $designationId,
                'designation_name' => $designationName
            ]
        ]);
    } else {
        throw new Exception('Failed to insert banker designation: ' . $insertStmt->error);
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 