<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Fetch payouts with JOINs to get actual names
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
            c.category_name as category_name
        FROM tbl_payout p
        LEFT JOIN tbl_payout_type pt ON p.payout_type_id = pt.id
        LEFT JOIN tbl_loan_type lt ON p.loan_type_id = lt.id
        LEFT JOIN tbl_vendor_bank vb ON p.vendor_bank_id = vb.id
        LEFT JOIN tbl_category c ON p.category_id = c.id
        ORDER BY p.id DESC
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $payouts = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Payouts fetched successfully',
        'data' => $payouts
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 