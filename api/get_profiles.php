<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Get optional filter parameters
    $vendorBankId = isset($_GET['vendor_bank_id']) ? (int)$_GET['vendor_bank_id'] : null;
    $loanTypeId = isset($_GET['loan_type_id']) ? (int)$_GET['loan_type_id'] : null;
    
    $sql = "SELECT tp.id, vb.vendor_bank_name as vendor_bank, lt.loan_type, tp.image, tp.file 
            FROM tbl_training_profile tp 
            LEFT JOIN tbl_vendor_bank vb ON tp.vendor_bank_id = vb.id 
            LEFT JOIN tbl_loan_type lt ON tp.loan_type_id = lt.id";
    
    $params = [];
    $conditions = [];
    
    if ($vendorBankId && $vendorBankId > 0) {
        $conditions[] = "tp.vendor_bank_id = ?";
        $params[] = $vendorBankId;
    }
    
    if ($loanTypeId && $loanTypeId > 0) {
        $conditions[] = "tp.loan_type_id = ?";
        $params[] = $loanTypeId;
    }
    
    if (!empty($conditions)) {
        $sql .= " WHERE " . implode(" AND ", $conditions);
    }
    
    $sql .= " ORDER BY vb.vendor_bank_name, lt.loan_type";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $profiles = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($profiles);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>
