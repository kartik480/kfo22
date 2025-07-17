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
    
    $subLocationName = trim($input['sub_location'] ?? '');
    
    // Validate input
    if (empty($subLocationName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Sub location name is required'));
        exit();
    }
    
    // Check if sub location already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_sub_location WHERE sub_location = ?");
    $checkStmt->execute([$subLocationName]);
    
    if ($checkStmt->fetch()) {
        http_response_code(409);
        echo json_encode(array('success' => false, 'error' => 'Sub location already exists'));
        exit();
    }
    
    // Insert new sub location into the sub_location column
    $insertStmt = $conn->prepare("INSERT INTO tbl_sub_location (sub_location) VALUES (?)");
    $insertStmt->execute([$subLocationName]);
    
    $subLocationId = $conn->lastInsertId();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Sub location added successfully',
        'data' => array(
            'id' => $subLocationId,
            'sub_location' => $subLocationName
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Add sub location error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding sub location: ' . $e->getMessage()
    ));
}
?> 