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

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Database connection successful',
        'debug_info' => [
            'server' => $servername,
            'database' => $dbname,
            'username' => $db_username
        ]
    ]);
    
    // Test if tbl_user exists
    try {
        $userQuery = "SELECT COUNT(*) as count FROM tbl_user";
        $userStmt = $pdo->prepare($userQuery);
        $userStmt->execute();
        $userCount = $userStmt->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'status' => 'success',
            'message' => 'tbl_user table exists',
            'user_count' => $userCount['count']
        ]);
    } catch (Exception $e) {
        echo json_encode([
            'status' => 'error',
            'message' => 'tbl_user table error: ' . $e->getMessage()
        ]);
    }
    
    // Test if tbl_sdsa_users exists
    try {
        $sdsaQuery = "SELECT COUNT(*) as count FROM tbl_sdsa_users";
        $sdsaStmt = $pdo->prepare($sdsaQuery);
        $sdsaStmt->execute();
        $sdsaCount = $sdsaStmt->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'status' => 'success',
            'message' => 'tbl_sdsa_users table exists',
            'sdsa_count' => $sdsaCount['count']
        ]);
    } catch (Exception $e) {
        echo json_encode([
            'status' => 'error',
            'message' => 'tbl_sdsa_users table error: ' . $e->getMessage()
        ]);
    }
    
    // Check if user ID 8 exists
    try {
        $checkUserQuery = "SELECT id, first_name, last_name FROM tbl_user WHERE id = 8";
        $checkUserStmt = $pdo->prepare($checkUserQuery);
        $checkUserStmt->execute();
        $userResult = $checkUserStmt->fetch(PDO::FETCH_ASSOC);
        
        if ($userResult) {
            echo json_encode([
                'status' => 'success',
                'message' => 'User ID 8 found',
                'user_data' => $userResult
            ]);
        } else {
            echo json_encode([
                'status' => 'error',
                'message' => 'User ID 8 not found in tbl_user'
            ]);
        }
    } catch (Exception $e) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Error checking user ID 8: ' . $e->getMessage()
        ]);
    }
    
    // Check tbl_sdsa_users structure
    try {
        $structureQuery = "DESCRIBE tbl_sdsa_users";
        $structureStmt = $pdo->prepare($structureQuery);
        $structureStmt->execute();
        $structure = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'status' => 'success',
            'message' => 'tbl_sdsa_users structure retrieved',
            'columns' => $structure
        ]);
    } catch (Exception $e) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Error getting table structure: ' . $e->getMessage()
        ]);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection error: ' . $e->getMessage(),
        'debug_info' => [
            'server' => $servername,
            'database' => $dbname,
            'username' => $db_username
        ]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 