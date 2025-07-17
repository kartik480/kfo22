<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Method not allowed. Only POST requests are accepted.'
    ]);
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
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    if (!$data) {
        throw new Exception('Invalid JSON input');
    }
    
    // Extract data
    $userId = $data['user_id'] ?? null;
    $loanTypeId = $data['loan_type_id'] ?? null;
    $vendorBankId = $data['vendor_bank_id'] ?? null;
    $categoryId = $data['category_id'] ?? null;
    $payout = $data['payout'] ?? null;
    
    // Validate required fields
    if (empty($userId)) {
        throw new Exception('User ID is required');
    }
    
    if (empty($loanTypeId)) {
        throw new Exception('Loan Type ID is required');
    }
    
    if (empty($vendorBankId)) {
        throw new Exception('Vendor Bank ID is required');
    }
    
    if (empty($categoryId)) {
        throw new Exception('Category ID is required');
    }
    
    if (empty($payout)) {
        throw new Exception('Payout amount is required');
    }
    
    // Validate numeric values
    if (!is_numeric($payout)) {
        throw new Exception('Payout amount must be numeric');
    }
    
    if ($payout <= 0) {
        throw new Exception('Payout amount must be greater than 0');
    }
    
    // Get the payout_type_id for "Lead Base/Agent Payout"
    $payoutTypeQuery = "SELECT id FROM tbl_payout_type WHERE payout_name = 'Lead Base/Agent Payout' AND status = 1";
    $payoutTypeStmt = $pdo->prepare($payoutTypeQuery);
    $payoutTypeStmt->execute();
    $payoutTypeResult = $payoutTypeStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$payoutTypeResult) {
        throw new Exception('Lead Base/Agent Payout type not found in the system');
    }
    
    $leadBaseAgentPayoutTypeId = $payoutTypeResult['id'];
    
    // Validate that user exists
    $userCheckQuery = "SELECT id FROM tbl_users WHERE id = :user_id AND status = 1";
    $userCheckStmt = $pdo->prepare($userCheckQuery);
    $userCheckStmt->bindParam(':user_id', $userId, PDO::PARAM_INT);
    $userCheckStmt->execute();
    
    if ($userCheckStmt->rowCount() == 0) {
        throw new Exception('User not found or inactive');
    }
    
    // Validate that loan type exists
    $loanTypeCheckQuery = "SELECT id FROM tbl_loan_type WHERE id = :loan_type_id AND status = 1";
    $loanTypeCheckStmt = $pdo->prepare($loanTypeCheckQuery);
    $loanTypeCheckStmt->bindParam(':loan_type_id', $loanTypeId, PDO::PARAM_INT);
    $loanTypeCheckStmt->execute();
    
    if ($loanTypeCheckStmt->rowCount() == 0) {
        throw new Exception('Loan type not found or inactive');
    }
    
    // Validate that vendor bank exists
    $vendorBankCheckQuery = "SELECT id FROM tbl_vendor_bank WHERE id = :vendor_bank_id AND status = 1";
    $vendorBankCheckStmt = $pdo->prepare($vendorBankCheckQuery);
    $vendorBankCheckStmt->bindParam(':vendor_bank_id', $vendorBankId, PDO::PARAM_INT);
    $vendorBankCheckStmt->execute();
    
    if ($vendorBankCheckStmt->rowCount() == 0) {
        throw new Exception('Vendor bank not found or inactive');
    }
    
    // Validate that category exists
    $categoryCheckQuery = "SELECT id FROM tbl_payout_category WHERE id = :category_id AND status = 1";
    $categoryCheckStmt = $pdo->prepare($categoryCheckQuery);
    $categoryCheckStmt->bindParam(':category_id', $categoryId, PDO::PARAM_INT);
    $categoryCheckStmt->execute();
    
    if ($categoryCheckStmt->rowCount() == 0) {
        throw new Exception('Category not found or inactive');
    }
    
    // Check if table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_payout'");
    if ($tableCheck->rowCount() == 0) {
        // Create table if it doesn't exist
        $createTableSQL = "
            CREATE TABLE tbl_payout (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                payout_type_id INT NOT NULL,
                loan_type_id INT NOT NULL,
                vendor_bank_id INT NOT NULL,
                category_id INT NOT NULL,
                payout DECIMAL(10,2) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_user_id (user_id),
                INDEX idx_payout_type_id (payout_type_id),
                INDEX idx_loan_type_id (loan_type_id),
                INDEX idx_vendor_bank_id (vendor_bank_id),
                INDEX idx_category_id (category_id)
            )
        ";
        $pdo->exec($createTableSQL);
    }
    
    // Check for duplicate entry (same user, payout type, loan type, vendor bank, and category)
    $duplicateCheckQuery = "
        SELECT id FROM tbl_payout 
        WHERE user_id = :user_id 
        AND payout_type_id = :payout_type_id 
        AND loan_type_id = :loan_type_id 
        AND vendor_bank_id = :vendor_bank_id 
        AND category_id = :category_id
    ";
    $duplicateCheckStmt = $pdo->prepare($duplicateCheckQuery);
    $duplicateCheckStmt->execute([
        ':user_id' => $userId,
        ':payout_type_id' => $leadBaseAgentPayoutTypeId,
        ':loan_type_id' => $loanTypeId,
        ':vendor_bank_id' => $vendorBankId,
        ':category_id' => $categoryId
    ]);
    
    if ($duplicateCheckStmt->rowCount() > 0) {
        throw new Exception('A Lead Base/Agent Payout record already exists for this combination of user, loan type, vendor bank, and category');
    }
    
    // Insert the Lead Base/Agent Payout
    $stmt = $pdo->prepare("
        INSERT INTO tbl_payout (user_id, payout_type_id, loan_type_id, vendor_bank_id, category_id, payout) 
        VALUES (:user_id, :payout_type_id, :loan_type_id, :vendor_bank_id, :category_id, :payout)
    ");
    
    $stmt->execute([
        ':user_id' => $userId,
        ':payout_type_id' => $leadBaseAgentPayoutTypeId,
        ':loan_type_id' => $loanTypeId,
        ':vendor_bank_id' => $vendorBankId,
        ':category_id' => $categoryId,
        ':payout' => $payout
    ]);
    
    $insertedId = $pdo->lastInsertId();
    
    // Fetch the inserted record with related data for response
    $fetchQuery = "
        SELECT 
            p.id,
            p.user_id,
            p.payout_type_id,
            p.loan_type_id,
            p.vendor_bank_id,
            p.category_id,
            p.payout,
            p.created_at,
            pt.payout_name as payout_type_name,
            lt.loan_type as loan_type_name,
            vb.vendor_bank_name as vendor_bank_name,
            pc.category_name as category_name
        FROM tbl_payout p
        LEFT JOIN tbl_payout_type pt ON p.payout_type_id = pt.id
        LEFT JOIN tbl_loan_type lt ON p.loan_type_id = lt.id
        LEFT JOIN tbl_vendor_bank vb ON p.vendor_bank_id = vb.id
        LEFT JOIN tbl_payout_category pc ON p.category_id = pc.id
        WHERE p.id = :id
    ";
    
    $fetchStmt = $pdo->prepare($fetchQuery);
    $fetchStmt->bindParam(':id', $insertedId, PDO::PARAM_INT);
    $fetchStmt->execute();
    $insertedRecord = $fetchStmt->fetch(PDO::FETCH_ASSOC);
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Lead Base/Agent Payout added successfully',
        'data' => $insertedRecord
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?> 