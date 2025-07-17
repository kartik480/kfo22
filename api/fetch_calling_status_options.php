<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Include database configuration
// This uses the fixed config file to prevent database connection errors
require_once 'db_config_fixed.php';

try {
    $conn = getConnection();
    
    // Prepare and execute the query to get calling statuses from tbl_partner_calling_status
    $stmt = $conn->prepare("SELECT calling_status FROM tbl_partner_calling_status WHERE calling_status IS NOT NULL AND calling_status != '' ORDER BY calling_status ASC");
    $stmt->execute();
    
    $callingStatuses = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
    
    // Return the list of statuses as a JSON response
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling status options fetched successfully.',
        'calling_statuses' => $callingStatuses
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    // Log the error and return a generic error message
    error_log("Fetch calling status error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'An error occurred while fetching calling status options.'
    ));
}
?> 