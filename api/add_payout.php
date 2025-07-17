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
    $payoutTypeId = $data['payout_type_id'] ?? null;
    $loanTypeId = $data['loan_type_id'] ?? null;
    $vendorBankId = $data['vendor_bank_id'] ?? null;
    $categoryId = $data['category_id'] ?? null;
    $payout = $data['payout'] ?? null;
    
    // Validate required fields
    if (empty($userId)) {
        throw new Exception('User ID is required');
    }
    
    if (empty($payoutTypeId)) {
        throw new Exception('Payout Type ID is required');
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
    
    // Insert the payout
    $stmt = $pdo->prepare("
        INSERT INTO tbl_payout (user_id, payout_type_id, loan_type_id, vendor_bank_id, category_id, payout) 
        VALUES (:user_id, :payout_type_id, :loan_type_id, :vendor_bank_id, :category_id, :payout)
    ");
    
    $stmt->execute([
        ':user_id' => $userId,
        ':payout_type_id' => $payoutTypeId,
        ':loan_type_id' => $loanTypeId,
        ':vendor_bank_id' => $vendorBankId,
        ':category_id' => $categoryId,
        ':payout' => $payout
    ]);
    
    $insertedId = $pdo->lastInsertId();
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Payout added successfully',
        'data' => [
            'id' => $insertedId,
            'user_id' => $userId,
            'payout_type_id' => $payoutTypeId,
            'loan_type_id' => $loanTypeId,
            'vendor_bank_id' => $vendorBankId,
            'category_id' => $categoryId,
            'payout' => $payout
        ]
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