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
    echo json_encode(array(
        'success' => false,
        'error' => 'Method not allowed. Use POST.'
    ));
    exit();
}

try {
    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['state_name'])) {
        http_response_code(400);
        echo json_encode(array(
            'success' => false,
            'error' => 'Missing state_name parameter'
        ));
        exit();
    }
    
    $stateName = trim($input['state_name']);
    
    if (empty($stateName)) {
        http_response_code(400);
        echo json_encode(array(
            'success' => false,
            'error' => 'State name cannot be empty'
        ));
        exit();
    }
    
    $conn = getConnection();
    
    // Check if state already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_state WHERE state_name = ?");
    $checkStmt->execute([$stateName]);
    
    if ($checkStmt->rowCount() > 0) {
        http_response_code(409);
        echo json_encode(array(
            'success' => false,
            'error' => 'State already exists'
        ));
        exit();
    }
    
    // Create the state table if it doesn't exist
    $createTableSQL = "
    CREATE TABLE IF NOT EXISTS tbl_state (
        id INT AUTO_INCREMENT PRIMARY KEY,
        state_name VARCHAR(255) NOT NULL UNIQUE,
        status ENUM('active', 'inactive') DEFAULT 'active',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    )";
    
    $conn->exec($createTableSQL);
    
    // Insert new state
    $stmt = $conn->prepare("INSERT INTO tbl_state (state_name) VALUES (?)");
    $result = $stmt->execute([$stateName]);
    
    if ($result) {
        $newId = $conn->lastInsertId();
        echo json_encode(array(
            'success' => true,
            'message' => 'State added successfully',
            'data' => array(
                'id' => $newId,
                'state_name' => $stateName,
                'status' => 'active'
            )
        ));
    } else {
        http_response_code(500);
        echo json_encode(array(
            'success' => false,
            'error' => 'Failed to add state'
        ));
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Add state error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding state: ' . $e->getMessage()
    ));
}
?> 