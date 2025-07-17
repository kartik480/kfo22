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
    
    if (!isset($input['vendor_bank_name']) || empty(trim($input['vendor_bank_name']))) {
        throw new Exception('Vendor bank name is required');
    }
    
    $vendorBankName = trim($input['vendor_bank_name']);
    
    // Check if vendor bank already exists
    $checkQuery = "SELECT id FROM tbl_vendor_bank WHERE vendor_bank_name = ?";
    $checkStmt = $conn->prepare($checkQuery);
    $checkStmt->bind_param('s', $vendorBankName);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();
    
    if ($checkResult->num_rows > 0) {
        echo json_encode([
            'success' => false,
            'error' => 'Vendor bank with this name already exists'
        ]);
        exit;
    }
    
    // Insert new vendor bank
    $insertQuery = "INSERT INTO tbl_vendor_bank (vendor_bank_name, status, created_at) VALUES (?, 'active', NOW())";
    $insertStmt = $conn->prepare($insertQuery);
    $insertStmt->bind_param('s', $vendorBankName);
    
    if ($insertStmt->execute()) {
        $vendorBankId = $conn->insert_id;
        
        echo json_encode([
            'success' => true,
            'message' => 'Vendor bank added successfully',
            'data' => [
                'id' => $vendorBankId,
                'vendor_bank_name' => $vendorBankName
            ]
        ]);
    } else {
        throw new Exception('Failed to insert vendor bank: ' . $insertStmt->error);
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 