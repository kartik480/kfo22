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
    echo json_encode([
        'status' => 'error',
        'message' => 'Only POST method is allowed',
        'data' => []
    ]);
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
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Invalid JSON data received',
            'data' => []
        ]);
        exit();
    }
    
    // Extract and validate required fields
    $vendorBankName = trim($input['vendor_bank_name'] ?? '');
    $loanTypeName = trim($input['loan_type_name'] ?? '');
    $image = trim($input['image'] ?? '');
    $content = trim($input['content'] ?? '');
    
    // Validation
    if (empty($vendorBankName)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Vendor bank name is required',
            'data' => []
        ]);
        exit();
    }
    
    if (empty($loanTypeName)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Loan type name is required',
            'data' => []
        ]);
        exit();
    }
    
    if (empty($content)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Content is required',
            'data' => []
        ]);
        exit();
    }
    
    // Get vendor bank ID from vendor bank name
    $stmt = $pdo->prepare("SELECT id FROM tbl_vendor_bank WHERE vendor_bank_name = ? AND status = 1");
    $stmt->execute([$vendorBankName]);
    $vendorBank = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$vendorBank) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Vendor bank not found: ' . $vendorBankName,
            'data' => []
        ]);
        exit();
    }
    
    $vendorBankId = $vendorBank['id'];
    
    // Get loan type ID from loan type name
    $stmt = $pdo->prepare("SELECT id FROM tbl_loan_type WHERE loan_type = ? AND status = 1");
    $stmt->execute([$loanTypeName]);
    $loanType = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loanType) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Loan type not found: ' . $loanTypeName,
            'data' => []
        ]);
        exit();
    }
    
    $loanTypeId = $loanType['id'];
    
    // Insert new policy
    $stmt = $pdo->prepare("
        INSERT INTO tbl_policy (vendor_bank_id, loan_type_id, image, content, status, created_at) 
        VALUES (?, ?, ?, ?, 1, NOW())
    ");
    
    $result = $stmt->execute([$vendorBankId, $loanTypeId, $image, $content]);
    
    if ($result) {
        $policyId = $pdo->lastInsertId();
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Policy added successfully',
            'data' => [
                'id' => $policyId,
                'vendor_bank_id' => $vendorBankId,
                'loan_type_id' => $loanTypeId,
                'image' => $image,
                'content' => $content,
                'vendor_bank_name' => $vendorBankName,
                'loan_type' => $loanTypeName
            ]
        ]);
    } else {
        echo json_encode([
            'status' => 'error',
            'message' => 'Failed to add policy',
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