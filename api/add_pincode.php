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
    echo json_encode(['success' => false, 'error' => 'Method not allowed']);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $input = $_POST;
    }
    
    $pincode = trim($input['pincode'] ?? '');
    $stateId = $input['state_id'] ?? null;
    $locationId = $input['location_id'] ?? null;
    $subLocationId = $input['sub_location_id'] ?? null;
    
    // Validate PIN code
    if (empty($pincode)) {
        echo json_encode(['success' => false, 'error' => 'PIN code is required']);
        exit();
    }
    
    if (!preg_match('/^\d{6}$/', $pincode)) {
        echo json_encode(['success' => false, 'error' => 'PIN code must be exactly 6 digits']);
        exit();
    }
    
    // Check if tbl_pincode table exists, create if not
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_pincode'");
    if ($tableCheck->rowCount() == 0) {
        $createTableSQL = "
            CREATE TABLE tbl_pincode (
                id INT AUTO_INCREMENT PRIMARY KEY,
                pincode VARCHAR(6) NOT NULL UNIQUE,
                state_id INT,
                location_id INT,
                sub_location_id INT,
                status ENUM('active', 'inactive') DEFAULT 'active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (state_id) REFERENCES tbl_branch_state(id) ON DELETE SET NULL,
                FOREIGN KEY (location_id) REFERENCES tbl_branch_location(id) ON DELETE SET NULL,
                FOREIGN KEY (sub_location_id) REFERENCES tbl_sub_location(id) ON DELETE SET NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        ";
        
        $pdo->exec($createTableSQL);
    }
    
    // Check if PIN code already exists
    $checkStmt = $pdo->prepare("SELECT id FROM tbl_pincode WHERE pincode = ?");
    $checkStmt->execute([$pincode]);
    
    if ($checkStmt->rowCount() > 0) {
        echo json_encode(['success' => false, 'error' => 'PIN code already exists']);
        exit();
    }
    
    // Validate foreign keys if provided
    if ($stateId !== null) {
        $stateCheck = $pdo->prepare("SELECT id FROM tbl_branch_state WHERE id = ?");
        $stateCheck->execute([$stateId]);
        if ($stateCheck->rowCount() == 0) {
            echo json_encode(['success' => false, 'error' => 'Invalid state ID']);
            exit();
        }
    }
    
    if ($locationId !== null) {
        $locationCheck = $pdo->prepare("SELECT id FROM tbl_branch_location WHERE id = ?");
        $locationCheck->execute([$locationId]);
        if ($locationCheck->rowCount() == 0) {
            echo json_encode(['success' => false, 'error' => 'Invalid location ID']);
            exit();
        }
    }
    
    if ($subLocationId !== null) {
        $subLocationCheck = $pdo->prepare("SELECT id FROM tbl_sub_location WHERE id = ?");
        $subLocationCheck->execute([$subLocationId]);
        if ($subLocationCheck->rowCount() == 0) {
            echo json_encode(['success' => false, 'error' => 'Invalid sub location ID']);
            exit();
        }
    }
    
    // Insert new PIN code
    $insertStmt = $pdo->prepare("
        INSERT INTO tbl_pincode (pincode, state_id, location_id, sub_location_id, status) 
        VALUES (?, ?, ?, ?, 'active')
    ");
    
    $insertStmt->execute([$pincode, $stateId, $locationId, $subLocationId]);
    
    $newId = $pdo->lastInsertId();
    
    echo json_encode([
        'success' => true,
        'message' => 'PIN code added successfully',
        'data' => [
            'id' => $newId,
            'pincode' => $pincode,
            'state_id' => $stateId,
            'location_id' => $locationId,
            'sub_location_id' => $subLocationId,
            'status' => 'active'
        ]
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in add_pincode.php: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error in add_pincode.php: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'error' => 'General error: ' . $e->getMessage()
    ]);
}
?> 