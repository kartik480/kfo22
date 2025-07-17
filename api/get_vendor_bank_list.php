<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    $sql = 'SELECT id, vendor_bank_name FROM tbl_vendor_bank ORDER BY vendor_bank_name ASC';
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    
    $vendorBanks = [];
    while ($row = $result->fetch_assoc()) {
        $vendorBanks[] = [
            'id' => $row['id'],
            'vendor_bank_name' => $row['vendor_bank_name']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $vendorBanks,
        'count' => count($vendorBanks)
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?> 