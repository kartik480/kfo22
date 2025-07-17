<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array('success' => false, 'error' => 'Method not allowed'));
    exit();
}

try {
    // Create connection using PDO
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $input = $_POST;
    }
    
    $branchLocationName = trim($input['branch_location'] ?? '');
    $branchStateName = trim($input['branch_state'] ?? '');
    
    // Validate input
    if (empty($branchLocationName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Branch location name is required'));
        exit();
    }
    
    if (empty($branchStateName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Branch state is required'));
        exit();
    }
    
    // Check if the branch_location table exists, create if not
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_branch_location LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_branch_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            branch_location VARCHAR(255) NOT NULL UNIQUE,
            branch_state_id INT,
            status TINYINT(1) DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (branch_state_id) REFERENCES tbl_branch_state(id) ON DELETE SET NULL
        )";
        
        $conn->exec($createTableSQL);
    }
    
    // Get branch_state_id from branch state name
    $branchStateId = null;
    $branchStateStmt = $conn->prepare("SELECT id FROM tbl_branch_state WHERE branch_state_name = ?");
    $branchStateStmt->execute([$branchStateName]);
    $branchStateResult = $branchStateStmt->fetch(PDO::FETCH_ASSOC);
    if ($branchStateResult) {
        $branchStateId = $branchStateResult['id'];
    } else {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Branch state not found: ' . $branchStateName));
        exit();
    }
    
    // Check if branch location already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_branch_location WHERE branch_location = ?");
    $checkStmt->execute([$branchLocationName]);
    
    if ($checkStmt->fetch()) {
        http_response_code(409);
        echo json_encode(array('success' => false, 'error' => 'Branch location already exists'));
        exit();
    }
    
    // Insert new branch location
    $insertStmt = $conn->prepare("INSERT INTO tbl_branch_location (branch_location, branch_state_id) VALUES (?, ?)");
    $insertStmt->execute([$branchLocationName, $branchStateId]);
    
    $branchLocationId = $conn->lastInsertId();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Branch location added successfully',
        'data' => array(
            'id' => $branchLocationId,
            'branch_location' => $branchLocationName,
            'branch_state_id' => $branchStateId,
            'branch_state_name' => $branchStateName,
            'status' => 'Active',
            'status_value' => 1
        )
    ));
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Add branch location error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding branch location: ' . $e->getMessage()
    ));
}
?> 