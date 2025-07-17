<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database configuration
require_once 'db_config.php';

// Set headers for JSON response
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

try {
    $conn = getConnection();
    
    // Fetch all states from tbl_state table for dropdown
    $stmt = $conn->prepare("SELECT state_name FROM tbl_state ORDER BY state_name");
    $stmt->execute();
    
    $states = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $states[] = $row['state_name'];
    }
    
    // Return JSON response
    echo json_encode(array(
        'success' => true,
        'states' => $states,
        'count' => count($states)
    ));
    
} catch (Exception $e) {
    // Return error response
    http_response_code(500);
    echo json_encode(array(
        'success' => false,
        'error' => $e->getMessage()
    ));
} finally {
    if (isset($conn)) {
        closeConnection($conn);
    }
}
?> 