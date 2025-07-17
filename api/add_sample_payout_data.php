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
    
    // Sample payout data
    $sampleData = [
        [
            'user_id' => 1,
            'payout_type_id' => 1,
            'loan_type_id' => 1,
            'vendor_bank_id' => 1,
            'category_id' => 1,
            'payout' => '5000.00'
        ],
        [
            'user_id' => 1,
            'payout_type_id' => 2,
            'loan_type_id' => 2,
            'vendor_bank_id' => 2,
            'category_id' => 2,
            'payout' => '7500.00'
        ],
        [
            'user_id' => 2,
            'payout_type_id' => 1,
            'loan_type_id' => 1,
            'vendor_bank_id' => 1,
            'category_id' => 1,
            'payout' => '3000.00'
        ],
        [
            'user_id' => 2,
            'payout_type_id' => 2,
            'loan_type_id' => 2,
            'vendor_bank_id' => 2,
            'category_id' => 2,
            'payout' => '4500.00'
        ],
        [
            'user_id' => 3,
            'payout_type_id' => 1,
            'loan_type_id' => 1,
            'vendor_bank_id' => 1,
            'category_id' => 1,
            'payout' => '6000.00'
        ]
    ];
    
    // Insert sample data
    $stmt = $pdo->prepare("
        INSERT INTO tbl_payout (user_id, payout_type_id, loan_type_id, vendor_bank_id, category_id, payout) 
        VALUES (:user_id, :payout_type_id, :loan_type_id, :vendor_bank_id, :category_id, :payout)
    ");
    
    $insertedCount = 0;
    foreach ($sampleData as $data) {
        $stmt->execute($data);
        $insertedCount++;
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => "Successfully inserted $insertedCount sample payout records",
        'inserted_count' => $insertedCount
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