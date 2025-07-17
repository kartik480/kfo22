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
    
    // Fetch sub locations from tbl_sub_location table using sub_location column with alias
    $stmt = $conn->prepare("SELECT sub_location AS subLocationName FROM tbl_sub_location ORDER BY sub_location ASC");
    $stmt->execute();
    $subLocations = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Sub locations fetched successfully.',
        'data' => $subLocations
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get sub locations error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching sub locations: ' . $e->getMessage()
    ));
}
?> 