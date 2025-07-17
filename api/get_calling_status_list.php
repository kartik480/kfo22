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
require_once 'db_config_fixed.php';

try {
    $conn = getConnection();
    
    // Prepare and execute the query to get all calling statuses from tbl_partner_calling_status
    $stmt = $conn->prepare("SELECT id, calling_status FROM tbl_partner_calling_status WHERE calling_status IS NOT NULL AND calling_status != '' ORDER BY calling_status ASC");
    $stmt->execute();
    
    $callingStatuses = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return the list of statuses as a JSON response in the format expected by Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling statuses fetched successfully.',
        'data' => $callingStatuses
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    // Log the error and return a generic error message
    error_log("Get calling status list error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'An error occurred while fetching calling statuses: ' . $e->getMessage()
    ));
}
?> 