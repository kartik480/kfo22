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
    
    // Simple query to get locations from the location column with alias
    $stmt = $conn->prepare("SELECT location AS locationName FROM tbl_location ORDER BY location ASC");
    $stmt->execute();
    $locations = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Locations fetched successfully.',
        'data' => $locations
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get locations simple error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching locations: ' . $e->getMessage()
    ));
}
?> 