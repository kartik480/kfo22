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

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // First, get the payout_type_id for "RBH Payout"
    $payoutTypeQuery = "SELECT id FROM tbl_payout_type WHERE payout_name = 'RBH Payout' AND status = 1";
    $payoutTypeStmt = $pdo->prepare($payoutTypeQuery);
    $payoutTypeStmt->execute();
    $payoutTypeResult = $payoutTypeStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$payoutTypeResult) {
        echo json_encode([
            'success' => false,
            'message' => 'RBH Payout type not found',
            'data' => []
        ]);
        exit();
    }
    
    $rbhPayoutTypeId = $payoutTypeResult['id'];
    
    // Fetch RBH Payout records with JOINs to get actual names
    $query = "
        SELECT 
            p.id,
            p.user_id,
            p.payout_type_id,
            p.loan_type_id,
            p.vendor_bank_id,
            p.category_id,
            p.payout,
            pt.payout_name as payout_type_name,
            lt.loan_type as loan_type_name,
            vb.vendor_bank_name as vendor_bank_name,
            pc.category_name as category_name
        FROM tbl_payout p
        LEFT JOIN tbl_payout_type pt ON p.payout_type_id = pt.id
        LEFT JOIN tbl_loan_type lt ON p.loan_type_id = lt.id
        LEFT JOIN tbl_vendor_bank vb ON p.vendor_bank_id = vb.id
        LEFT JOIN tbl_payout_category pc ON p.category_id = pc.id
        WHERE p.payout_type_id = :payout_type_id
        ORDER BY p.id DESC
    ";

    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':payout_type_id', $rbhPayoutTypeId, PDO::PARAM_INT);
    $stmt->execute();
    $payouts = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if ($payouts) {
        echo json_encode([
            'success' => true,
            'message' => 'RBH Payout records fetched successfully',
            'data' => $payouts,
            'count' => count($payouts)
        ]);
    } else {
        echo json_encode([
            'success' => true,
            'message' => 'No RBH Payout records found',
            'data' => [],
            'count' => 0
        ]);
    }

} catch (PDOException $e) {
    // Database error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ], JSON_PRETTY_PRINT);
} catch (Exception $e) {
    // General error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ], JSON_PRETTY_PRINT);
}
?> 