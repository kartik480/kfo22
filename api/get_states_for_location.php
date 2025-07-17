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

try {
    $conn = getConnection();
    
    // Fetch states from tbl_state table using state_name column
    $stmt = $conn->prepare("SELECT state_name FROM tbl_state ORDER BY state_name ASC");
    $stmt->execute();
    $states = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'States fetched successfully.',
        'states' => $states
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get states for location error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching states: ' . $e->getMessage()
    ));
}
?> 