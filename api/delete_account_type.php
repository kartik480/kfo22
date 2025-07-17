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
    
    // Check if account type exists
    $checkQuery = "SELECT id FROM tbl_account_type WHERE account_type = ?";
    $checkStmt = $conn->prepare($checkQuery);
    $checkStmt->bind_param('s', $accountType);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();
    
    if ($checkResult->num_rows == 0) {
        echo json_encode([
            'success' => false,
            'error' => 'Account type not found'
        ]);
        exit;
    }
    
    // Delete the account type
    $deleteQuery = "DELETE FROM tbl_account_type WHERE account_type = ?";
    $deleteStmt = $conn->prepare($deleteQuery);
    $deleteStmt->bind_param('s', $accountType);
    
    if ($deleteStmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Account type deleted successfully',
            'data' => [
                'account_type' => $accountType
            ]
        ]);
    } else {
        throw new Exception('Failed to delete account type: ' . $deleteStmt->error);
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 