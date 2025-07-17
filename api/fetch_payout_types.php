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
    
    // Check if table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_payout_type'");
    if ($tableCheck->rowCount() == 0) {
        // Create table if it doesn't exist
        $createTableSQL = "
            CREATE TABLE tbl_payout_type (
                id INT AUTO_INCREMENT PRIMARY KEY,
                payout_name VARCHAR(255) NOT NULL,
                status ENUM('Active', 'Inactive') DEFAULT 'Active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ";
        $pdo->exec($createTableSQL);
        
        // Insert sample data
        $sampleData = [
            'Branch/Full Payout',
            'CBO Payout',
            'Connector/Referral Payout',
            'Inter Branch Payout',
            'Lead Base/Agent Payout',
            'RBH Payout',
            'SDSA Payout',
            'Service/Partner Payout'
        ];
        
        $stmt = $pdo->prepare("INSERT INTO tbl_payout_type (payout_name, status) VALUES (?, 'Active')");
        foreach ($sampleData as $payoutType) {
            $stmt->execute([$payoutType]);
        }
    } else {
        // Check if status column exists, if not add it
        $columnCheck = $pdo->query("SHOW COLUMNS FROM tbl_payout_type LIKE 'status'");
        if ($columnCheck->rowCount() == 0) {
            $pdo->exec("ALTER TABLE tbl_payout_type ADD COLUMN status ENUM('Active', 'Inactive') DEFAULT 'Active'");
        }
    }
    
    // Fetch payout types
    $stmt = $pdo->prepare("SELECT id, payout_name, status FROM tbl_payout_type ORDER BY payout_name");
    $stmt->execute();
    $payoutTypes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Payout types fetched successfully',
        'data' => $payoutTypes
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