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
    
    if (!isset($input['account_type']) || empty(trim($input['account_type']))) {
        throw new Exception('Account type is required');
    }
    
    $accountType = trim($input['account_type']);
    
    // Check if account type already exists
    $checkQuery = "SELECT id FROM tbl_account_type WHERE account_type = ?";
    $checkStmt = $conn->prepare($checkQuery);
    $checkStmt->bind_param('s', $accountType);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();
    
    if ($checkResult->num_rows > 0) {
        echo json_encode([
            'success' => false,
            'error' => 'Account type with this name already exists'
        ]);
        exit;
    }
    
    // Insert new account type
    $insertQuery = "INSERT INTO tbl_account_type (account_type, status, created_at) VALUES (?, 'active', NOW())";
    $insertStmt = $conn->prepare($insertQuery);
    $insertStmt->bind_param('s', $accountType);
    
    if ($insertStmt->execute()) {
        $accountTypeId = $conn->insert_id;
        
        echo json_encode([
            'success' => true,
            'message' => 'Account type added successfully',
            'data' => [
                'id' => $accountTypeId,
                'account_type' => $accountType
            ]
        ]);
    } else {
        throw new Exception('Failed to insert account type: ' . $insertStmt->error);
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 