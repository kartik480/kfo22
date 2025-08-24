<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    
    $conn = getConnection();
    
    if (!$conn) {
        throw new Exception("Database connection failed");
    }
    
    $input = file_get_contents('php://input');
    $postData = $_POST;
    $getData = $_GET;
    
    $debugInfo = [
        'request_method' => $_SERVER['REQUEST_METHOD'],
        'content_type' => $_SERVER['CONTENT_TYPE'] ?? 'Not set',
        'raw_input' => $input,
        'post_data' => $postData,
        'get_data' => $getData
    ];
    
    // Check if tbl_bh_users table exists
    $tableCheckQuery = "SHOW TABLES LIKE 'tbl_bh_users'";
    $tableCheckStmt = $conn->prepare($tableCheckQuery);
    $tableCheckStmt->execute();
    $tableExists = $tableCheckStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$tableExists) {
        throw new Exception("Table 'tbl_bh_users' does not exist");
    }
    
    // Check table structure
    $structureQuery = "DESCRIBE tbl_bh_users";
    $structureStmt = $conn->prepare($structureQuery);
    $structureStmt->execute();
    $tableStructure = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check if tbl_user table exists
    $userTableCheckQuery = "SHOW TABLES LIKE 'tbl_user'";
    $userTableCheckStmt = $conn->prepare($userTableCheckQuery);
    $userTableCheckStmt->execute();
    $userTableExists = $userTableCheckStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userTableExists) {
        throw new Exception("Table 'tbl_user' does not exist");
    }
    
    // Get sample data from tbl_bh_users
    $sampleDataQuery = "SELECT COUNT(*) as total FROM tbl_bh_users LIMIT 1";
    $sampleDataStmt = $conn->prepare($sampleDataQuery);
    $sampleDataStmt->execute();
    $sampleData = $sampleDataStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get sample data from tbl_user
    $sampleUserQuery = "SELECT COUNT(*) as total FROM tbl_user LIMIT 1";
    $sampleUserStmt = $conn->prepare($sampleUserQuery);
    $sampleUserStmt->execute();
    $sampleUser = $sampleUserStmt->fetch(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'Debug information retrieved successfully',
        'debug_info' => [
            'request_info' => $debugInfo,
            'database_connection' => 'Connected successfully',
            'tbl_bh_users_exists' => $tableExists ? 'Yes' : 'No',
            'tbl_user_exists' => $userTableExists ? 'Yes' : 'No',
            'tbl_bh_users_structure' => $tableStructure,
            'tbl_bh_users_count' => $sampleData['total'],
            'tbl_user_count' => $sampleUser['total']
        ]
    ];
    
    echo json_encode($response);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Debug failed: ' . $e->getMessage(),
        'debug_info' => [
            'error_details' => $e->getMessage(),
            'trace' => $e->getTraceAsString()
        ]
    ]);
}
?>
