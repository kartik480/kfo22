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
    
    // Fetch policies with vendor bank and loan type names using JOINs
    $stmt = $pdo->prepare("
        SELECT 
            p.id,
            p.vendor_bank_id,
            p.loan_type_id,
            p.image,
            p.content,
            vb.vendor_bank_name,
            lt.loan_type
        FROM tbl_policy p
        LEFT JOIN tbl_vendor_bank vb ON p.vendor_bank_id = vb.id
        LEFT JOIN tbl_loan_type lt ON p.loan_type_id = lt.id
        WHERE p.status = 1
        ORDER BY p.id DESC
    ");
    $stmt->execute();
    $policies = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($policies) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Policies fetched successfully',
            'data' => $policies
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No policies found',
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