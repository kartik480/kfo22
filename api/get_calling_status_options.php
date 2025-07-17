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
    
    // Fetch calling status options from tbl_partner_calling_status
    $stmt = $conn->prepare("
        SELECT 
            id,
            calling_status,
            status,
            created_at,
            updated_at
        FROM tbl_partner_calling_status 
        WHERE status = 1 
        ORDER BY calling_status ASC
    ");
    $stmt->execute();
    $callingStatusOptions = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countStmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_partner_calling_status WHERE status = 1");
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling status options fetched successfully.',
        'data' => $callingStatusOptions,
        'total_count' => $totalCount
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get calling status options error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching calling status options: ' . $e->getMessage()
    ));
}
?> 