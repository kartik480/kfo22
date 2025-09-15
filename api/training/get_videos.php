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
    
    // Get filter parameters
    $vendorBankId = isset($_GET['vendor_bank_id']) ? (int)$_GET['vendor_bank_id'] : 0;
    $loanTypeId = isset($_GET['loan_type_id']) ? (int)$_GET['loan_type_id'] : 0;
    
    // Base query to fetch videos with vendor bank and loan type information
    $sql = "SELECT 
                v.id,
                v.name,
                vb.vendor_bank_name as vendor_bank,
                lt.loan_type,
                v.video_url
            FROM tbl_training_videos v
            LEFT JOIN tbl_vendor_bank vb ON v.vendor_bank_id = vb.id
            LEFT JOIN tbl_loan_type lt ON v.loan_type_id = lt.id
            WHERE 1=1";
    
    $params = [];
    
    // Add filters if provided
    if ($vendorBankId > 0) {
        $sql .= " AND v.vendor_bank_id = :vendor_bank_id";
        $params[':vendor_bank_id'] = $vendorBankId;
    }
    
    if ($loanTypeId > 0) {
        $sql .= " AND v.loan_type_id = :loan_type_id";
        $params[':loan_type_id'] = $loanTypeId;
    }
    
    $sql .= " ORDER BY v.name ASC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    
    $videos = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return JSON response
    echo json_encode($videos);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Server error: ' . $e->getMessage()]);
}
?>
