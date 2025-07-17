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

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
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
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        throw new Exception('Invalid JSON data received');
    }

    // Validate required fields
    $requiredFields = ['user_id', 'vendor_bank_id', 'loan_type_id', 'category_id', 'payout'];
    foreach ($requiredFields as $field) {
        if (!isset($input[$field]) || empty($input[$field])) {
            throw new Exception("Missing required field: $field");
        }
    }

    // Validate data types
    $userId = (int)$input['user_id'];
    $vendorBankId = (int)$input['vendor_bank_id'];
    $loanTypeId = (int)$input['loan_type_id'];
    $categoryId = (int)$input['category_id'];
    $payout = (float)$input['payout'];

    if ($userId <= 0 || $vendorBankId <= 0 || $loanTypeId <= 0 || $categoryId <= 0 || $payout < 0) {
        throw new Exception('Invalid data values provided');
    }

    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Get Payout Team type ID
    $payoutTypeQuery = "SELECT id FROM tbl_payout_type WHERE payout_name = 'Payout Team' AND status = 1";
    $payoutTypeStmt = $pdo->prepare($payoutTypeQuery);
    $payoutTypeStmt->execute();
    $payoutTypeResult = $payoutTypeStmt->fetch(PDO::FETCH_ASSOC);

    if (!$payoutTypeResult) {
        // Create Payout Team type if it doesn't exist
        $createPayoutTypeQuery = "INSERT INTO tbl_payout_type (payout_name, status, created_at) VALUES ('Payout Team', 1, NOW())";
        $pdo->exec($createPayoutTypeQuery);
        $payoutTypeId = $pdo->lastInsertId();
    } else {
        $payoutTypeId = $payoutTypeResult['id'];
    }

    // Validate that user exists
    $userQuery = "SELECT id FROM tbl_users WHERE id = ? AND status = 1";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    if (!$userStmt->fetch()) {
        throw new Exception('User not found or inactive');
    }

    // Validate that vendor bank exists
    $vendorBankQuery = "SELECT id FROM tbl_vendor_bank WHERE id = ? AND status = 1";
    $vendorBankStmt = $pdo->prepare($vendorBankQuery);
    $vendorBankStmt->execute([$vendorBankId]);
    if (!$vendorBankStmt->fetch()) {
        throw new Exception('Vendor bank not found or inactive');
    }

    // Validate that loan type exists
    $loanTypeQuery = "SELECT id FROM tbl_loan_type WHERE id = ? AND status = 1";
    $loanTypeStmt = $pdo->prepare($loanTypeQuery);
    $loanTypeStmt->execute([$loanTypeId]);
    if (!$loanTypeStmt->fetch()) {
        throw new Exception('Loan type not found or inactive');
    }

    // Validate that category exists
    $categoryQuery = "SELECT id FROM tbl_payout_category WHERE id = ? AND status = 1";
    $categoryStmt = $pdo->prepare($categoryQuery);
    $categoryStmt->execute([$categoryId]);
    if (!$categoryStmt->fetch()) {
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
    $duplicateCheckStmt->bindParam(':user_id', $userId, PDO::PARAM_INT);
    $duplicateCheckStmt->bindParam(':payout_type_id', $payoutTypeId, PDO::PARAM_INT);
    $duplicateCheckStmt->bindParam(':loan_type_id', $loanTypeId, PDO::PARAM_INT);
    $duplicateCheckStmt->bindParam(':vendor_bank_id', $vendorBankId, PDO::PARAM_INT);
    $duplicateCheckStmt->bindParam(':category_id', $categoryId, PDO::PARAM_INT);
    $duplicateCheckStmt->execute();
    
    if ($duplicateCheckStmt->fetch()) {
        throw new Exception('Payout record already exists for this combination');
    }

    // Insert new payout record
    $insertQuery = "
        INSERT INTO tbl_payout (
            user_id, 
            payout_type_id, 
            loan_type_id, 
            vendor_bank_id, 
            category_id, 
            payout
        ) VALUES (:user_id, :payout_type_id, :loan_type_id, :vendor_bank_id, :category_id, :payout)
    ";

    $insertStmt = $pdo->prepare($insertQuery);
    $insertStmt->bindParam(':user_id', $userId, PDO::PARAM_INT);
    $insertStmt->bindParam(':payout_type_id', $payoutTypeId, PDO::PARAM_INT);
    $insertStmt->bindParam(':loan_type_id', $loanTypeId, PDO::PARAM_INT);
    $insertStmt->bindParam(':vendor_bank_id', $vendorBankId, PDO::PARAM_INT);
    $insertStmt->bindParam(':category_id', $categoryId, PDO::PARAM_INT);
    $insertStmt->bindParam(':payout', $payout, PDO::PARAM_STR);
    $insertStmt->execute();

    $payoutId = $pdo->lastInsertId();

    // Get the created record with related data
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
    $fetchStmt->bindParam(':id', $payoutId, PDO::PARAM_INT);
    $fetchStmt->execute();
    $insertedRecord = $fetchStmt->fetch(PDO::FETCH_ASSOC);

    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Payout Team added successfully',
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