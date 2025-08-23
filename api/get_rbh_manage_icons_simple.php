<?php
// Simplified version of get_rbh_manage_icons.php with better error handling
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Handle OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    echo json_encode(['status' => 'success', 'message' => 'OPTIONS handled']);
    exit();
}

try {
    // Step 1: Check if db_config.php exists
    $db_config_path = __DIR__ . '/db_config.php';
    if (!file_exists($db_config_path)) {
        throw new Exception("Database config file not found at: " . $db_config_path);
    }
    
    // Step 2: Include database connection
    require_once 'db_config.php';
    
    // Step 3: Check if PDO variable exists
    if (!isset($pdo)) {
        throw new Exception("PDO connection variable not found in db_config.php");
    }
    
    // Step 4: Test database connection
    $test_query = $pdo->query("SELECT 1 as test");
    $test_result = $test_query->fetch();
    if ($test_result['test'] != 1) {
        throw new Exception("Database connection test failed");
    }
    
    // Step 5: Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        $input = $_POST; // Fallback to POST array
    }
    
    $userId = $input['user_id'] ?? null;
    $username = $input['username'] ?? null;
    
    if (!$userId) {
        throw new Exception("User ID is required");
    }
    
    // Step 6: Check if tables exist
    $table_check = $pdo->query("SHOW TABLES LIKE 'tbl_user'");
    if ($table_check->rowCount() == 0) {
        throw new Exception("Table 'tbl_user' does not exist");
    }
    
    $table_check = $pdo->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($table_check->rowCount() == 0) {
        throw new Exception("Table 'tbl_manage_icon' does not exist");
    }
    
    // Step 7: Fetch user data
    $userQuery = "SELECT manage_icons FROM tbl_user WHERE id = ?";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    $userData = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userData) {
        echo json_encode([
            'status' => 'success',
            'message' => 'User not found',
            'data' => [],
            'debug_info' => [
                'user_id_requested' => $userId,
                'username' => $username,
                'tables_exist' => true,
                'database_connected' => true
            ]
        ]);
        exit();
    }
    
    $manageIcons = $userData['manage_icons'];
    if (empty($manageIcons)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No manage icons assigned to this user',
            'data' => [],
            'debug_info' => [
                'user_id' => $userId,
                'manage_icons' => $manageIcons,
                'tables_exist' => true,
                'database_connected' => true
            ]
        ]);
        exit();
    }
    
    // Step 8: Parse and fetch icons
    $iconIds = explode(',', $manageIcons);
    $iconIds = array_map('trim', $iconIds);
    $iconIds = array_filter($iconIds);
    
    if (empty($iconIds)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No valid icon IDs found',
            'data' => [],
            'debug_info' => [
                'user_id' => $userId,
                'manage_icons' => $manageIcons,
                'parsed_ids' => $iconIds
            ]
        ]);
        exit();
    }
    
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description, status 
                  FROM tbl_manage_icon 
                  WHERE id IN ($placeholders) 
                  AND (status = 'Active' OR status = 'active' OR status = 1 OR status IS NULL OR status = '')
                  ORDER BY icon_name ASC";
    
    $iconStmt = $pdo->prepare($iconQuery);
    $iconStmt->execute($iconIds);
    $icons = $iconStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Manage icons fetched successfully',
        'data' => $icons,
        'debug_info' => [
            'user_id' => $userId,
            'username' => $username,
            'manage_icons' => $manageIcons,
            'icon_ids' => $iconIds,
            'icons_found' => count($icons),
            'tables_exist' => true,
            'database_connected' => true
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine(),
        'error_type' => 'Exception',
        'debug_info' => [
            'php_version' => phpversion(),
            'server_time' => date('Y-m-d H:i:s'),
            'request_method' => $_SERVER['REQUEST_METHOD'] ?? 'unknown'
        ]
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine(),
        'error_type' => 'PDOException',
        'debug_info' => [
            'php_version' => phpversion(),
            'server_time' => date('Y-m-d H:i:s'),
            'pdo_available' => extension_loaded('pdo'),
            'pdo_mysql_available' => extension_loaded('pdo_mysql')
        ]
    ]);
} catch (Error $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'PHP Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine(),
        'error_type' => 'Error',
        'debug_info' => [
            'php_version' => phpversion(),
            'server_time' => date('Y-m-d H:i:s'),
            'error_reporting' => error_reporting()
        ]
    ]);
}
?>
