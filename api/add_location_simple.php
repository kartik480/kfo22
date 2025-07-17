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
    
    // Validate input
    if (empty($locationName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Location name is required'));
        exit();
    }
    
    // Check if location already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_location WHERE location = ?");
    $checkStmt->execute([$locationName]);
    
    if ($checkStmt->fetch()) {
        http_response_code(409);
        echo json_encode(array('success' => false, 'error' => 'Location already exists'));
        exit();
    }
    
    // Insert new location into the location column
    $insertStmt = $conn->prepare("INSERT INTO tbl_location (location) VALUES (?)");
    $insertStmt->execute([$locationName]);
    
    $locationId = $conn->lastInsertId();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Location added successfully',
        'data' => array(
            'id' => $locationId,
            'location' => $locationName
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Add location simple error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding location: ' . $e->getMessage()
    ));
}
?> 