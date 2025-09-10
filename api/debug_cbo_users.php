<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    // Get the reportingTo value from GET parameter
    $reportingTo = isset($_GET['reportingTo']) ? trim($_GET['reportingTo']) : '';
    
    if (empty($reportingTo)) {
        throw new Exception('reportingTo parameter is required');
    }
    
    // First, check if the table exists
    $tableCheckSql = "SHOW TABLES LIKE 'tbl_cbo_users'";
    $tableStmt = $conn->prepare($tableCheckSql);
    $tableStmt->execute();
    $tableExists = $tableStmt->rowCount() > 0;
    
    if (!$tableExists) {
        throw new Exception('Table tbl_cbo_users does not exist');
    }
    
    // Get all unique reportingTo values to see what's available
    $reportingToSql = "SELECT DISTINCT reportingTo FROM tbl_cbo_users WHERE reportingTo IS NOT NULL AND reportingTo != ''";
    $reportingToStmt = $conn->prepare($reportingToSql);
    $reportingToStmt->execute();
    $reportingToValues = $reportingToStmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Get all users in the table
    $allUsersSql = "SELECT id, username, first_name, last_name, reportingTo, status FROM tbl_cbo_users LIMIT 10";
    $allUsersStmt = $conn->prepare($allUsersSql);
    $allUsersStmt->execute();
    $allUsers = $allUsersStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check specifically for the requested reportingTo value
    $specificSql = "SELECT COUNT(*) as count FROM tbl_cbo_users WHERE reportingTo = ?";
    $specificStmt = $conn->prepare($specificSql);
    $specificStmt->execute([$reportingTo]);
    $specificCount = $specificStmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Debug information retrieved successfully',
        'data' => [
            'table_exists' => $tableExists,
            'requested_reportingTo' => $reportingTo,
            'specific_count' => $specificCount,
            'available_reportingTo_values' => $reportingToValues,
            'sample_users' => $allUsers,
            'total_users_in_table' => count($allUsers)
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("Debug CBO Users Error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ], JSON_PRETTY_PRINT);
}
?>
