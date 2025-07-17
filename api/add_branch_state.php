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
    
    $branchStateName = trim($input['branch_state_name'] ?? '');
    
    // Validate branch state name
    if (empty($branchStateName)) {
        echo json_encode(['success' => false, 'error' => 'Branch state name is required']);
        exit();
    }
    
    // Check if tbl_branch_state table exists, create if not
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_branch_state'");
    if ($tableCheck->rowCount() == 0) {
        $createTableSQL = "
            CREATE TABLE tbl_branch_state (
                id INT AUTO_INCREMENT PRIMARY KEY,
                branch_state_name VARCHAR(255) NOT NULL UNIQUE,
                status ENUM('active', 'inactive') DEFAULT 'active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        ";
        
        $pdo->exec($createTableSQL);
    }
    
    // Check if branch state name already exists
    $checkStmt = $pdo->prepare("SELECT id FROM tbl_branch_state WHERE branch_state_name = ?");
    $checkStmt->execute([$branchStateName]);
    
    if ($checkStmt->rowCount() > 0) {
        echo json_encode(['success' => false, 'error' => 'Branch state name already exists']);
        exit();
    }
    
    // Insert new branch state
    $insertStmt = $pdo->prepare("
        INSERT INTO tbl_branch_state (branch_state_name, status) 
        VALUES (?, 'active')
    ");
    
    $insertStmt->execute([$branchStateName]);
    
    $newId = $pdo->lastInsertId();
    
    echo json_encode([
        'success' => true,
        'message' => 'Branch state added successfully',
        'data' => [
            'id' => $newId,
            'branch_state_name' => $branchStateName,
            'status' => 'active'
        ]
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in add_branch_state.php: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error in add_branch_state.php: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'error' => 'General error: ' . $e->getMessage()
    ]);
}
?> 