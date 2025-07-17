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
    // Query to get all vendor bank names
    $query = "SELECT DISTINCT vendor_bank_name FROM vendor_banks WHERE status = 'active' ORDER BY vendor_bank_name ASC";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $vendorBanks = array();
    while ($row = $result->fetch_assoc()) {
        $vendorBanks[] = $row['vendor_bank_name'];
    }
    
    echo json_encode(array(
        'success' => true,
        'vendor_banks' => $vendorBanks,
        'count' => count($vendorBanks)
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 