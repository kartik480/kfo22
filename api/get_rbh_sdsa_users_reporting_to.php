<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database configuration
require_once 'db_config.php';

try {
    // Get database connection
    $conn = getConnection();
    
    if (!$conn) {
        throw new Exception("Database connection failed");
    }
    
    // Query to fetch all unique values from reportingTo column
    $query = "SELECT DISTINCT reportingTo FROM tbl_sdsa_users WHERE reportingTo IS NOT NULL AND reportingTo != '' ORDER BY reportingTo";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Extract the reportingTo values
    $reportingToList = array_column($result, 'reportingTo');
    
    // Prepare response
    $response = array(
        'status' => 'success',
        'message' => 'Reporting To list fetched successfully',
        'data' => array(
            'reporting_to_list' => $reportingToList,
            'total_count' => count($reportingToList)
        ),
        'debug_info' => array(
            'query_executed' => $query,
            'rows_returned' => count($result)
        )
    );
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $errorResponse = array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug_info' => array(
            'error_type' => get_class($e),
            'error_file' => $e->getFile(),
            'error_line' => $e->getLine()
        )
    );
    
    http_response_code(500);
    echo json_encode($errorResponse, JSON_PRETTY_PRINT);
} finally {
    if (isset($conn)) {
        $conn = null;
    }
}
?>
