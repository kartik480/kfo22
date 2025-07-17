<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Fetch vendor banks from tbl_vendor_bank
    $stmt = $pdo->prepare("SELECT id, vendor_bank_name FROM tbl_vendor_bank WHERE status = 1 ORDER BY vendor_bank_name ASC");
    $stmt->execute();
    $vendorBanks = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($vendorBanks) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Vendor banks fetched successfully',
            'data' => $vendorBanks
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No vendor banks found',
            'data' => []
        ]);
    }
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 