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

// Include database configuration
require_once 'db_config.php';

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array('success' => false, 'error' => 'Method not allowed'));
    exit();
}

try {
    $conn = getConnection();
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $input = $_POST;
    }
    
    $locationName = trim($input['location'] ?? '');
    $stateName = trim($input['state'] ?? '');
    
    // Validate input
    if (empty($locationName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Location name is required'));
        exit();
    }
    
    // Check if the location table exists, create if not
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_location LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            location VARCHAR(255) NOT NULL UNIQUE,
            state_id INT,
            status ENUM('active', 'inactive') DEFAULT 'active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (state_id) REFERENCES tbl_state(id) ON DELETE SET NULL
        )";
        
        $conn->exec($createTableSQL);
    }
    
    // Get state_id if state name is provided
    $stateId = null;
    if (!empty($stateName)) {
        $stateStmt = $conn->prepare("SELECT id FROM tbl_state WHERE state_name = ?");
        $stateStmt->execute([$stateName]);
        $stateResult = $stateStmt->fetch(PDO::FETCH_ASSOC);
        if ($stateResult) {
            $stateId = $stateResult['id'];
        }
    }
    
    // Check if location already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_location WHERE location = ?");
    $checkStmt->execute([$locationName]);
    
    if ($checkStmt->fetch()) {
        http_response_code(409);
        echo json_encode(array('success' => false, 'error' => 'Location already exists'));
        exit();
    }
    
    // Insert new location
    $insertStmt = $conn->prepare("INSERT INTO tbl_location (location, state_id) VALUES (?, ?)");
    $insertStmt->execute([$locationName, $stateId]);
    
    $locationId = $conn->lastInsertId();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Location added successfully',
        'data' => array(
            'id' => $locationId,
            'location' => $locationName,
            'state' => $stateName
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Add location error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding location: ' . $e->getMessage()
    ));
}
?> 