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
    
    if (!isset($input['bank_name']) || empty(trim($input['bank_name']))) {
        throw new Exception('Bank name is required');
    }
    
    $bankName = trim($input['bank_name']);
    
    // Check if bank already exists
    $checkQuery = "SELECT id FROM tbl_bank WHERE bank_name = ?";
    $checkStmt = $conn->prepare($checkQuery);
    $checkStmt->bind_param('s', $bankName);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();
    
    if ($checkResult->num_rows > 0) {
        echo json_encode([
            'success' => false,
            'error' => 'Bank with this name already exists'
        ]);
        exit;
    }
    
    // Insert new bank
    $insertQuery = "INSERT INTO tbl_bank (bank_name, status, created_at) VALUES (?, 'active', NOW())";
    $insertStmt = $conn->prepare($insertQuery);
    $insertStmt->bind_param('s', $bankName);
    
    if ($insertStmt->execute()) {
        $bankId = $conn->insert_id;
        
        echo json_encode([
            'success' => true,
            'message' => 'Bank added successfully',
            'data' => [
                'id' => $bankId,
                'bank_name' => $bankName
            ]
        ]);
    } else {
        throw new Exception('Failed to insert bank: ' . $insertStmt->error);
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 