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
    
    // Fetch all calling sub statuses with their parent calling status names
    $stmt = $conn->prepare("
        SELECT 
            cs.id,
            cs.calling_sub_status,
            cs.calling_status_id,
            cs.status,
            cst.calling_status as parent_calling_status
        FROM tbl_partner_calling_sub_status cs
        LEFT JOIN tbl_partner_calling_status cst ON cs.calling_status_id = cst.id
        WHERE cs.status = 1 
        ORDER BY cst.calling_status ASC, cs.calling_sub_status ASC
    ");
    $stmt->execute();
    $callingSubStatuses = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countStmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_partner_calling_sub_status WHERE status = 1");
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling sub statuses fetched successfully.',
        'data' => $callingSubStatuses,
        'total_count' => $totalCount
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Fetch all calling sub statuses error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching calling sub statuses: ' . $e->getMessage()
    ));
}
?> 