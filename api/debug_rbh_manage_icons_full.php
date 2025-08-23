<?php
// Full debug version to identify the exact cause of 500 error
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

$debug_steps = [];

try {
    // Step 1: Basic PHP functionality
    $debug_steps[] = ['step' => 1, 'description' => 'Basic PHP test', 'status' => 'success'];
    
    // Step 2: Check request method
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        echo json_encode([
            'status' => 'success',
            'message' => 'OPTIONS request handled',
            'debug_steps' => $debug_steps
        ]);
        exit();
    }
    
    $debug_steps[] = ['step' => 2, 'description' => 'Request method check', 'status' => 'success', 'method' => $_SERVER['REQUEST_METHOD']];
    
    // Step 3: Check if db_config.php exists
    $db_config_path = __DIR__ . '/db_config.php';
    if (!file_exists($db_config_path)) {
        throw new Exception("Database config file not found at: " . $db_config_path);
    }
    $debug_steps[] = ['step' => 3, 'description' => 'db_config.php exists', 'status' => 'success', 'path' => $db_config_path];
    
    // Step 4: Include database connection
    require_once 'db_config.php';
    $debug_steps[] = ['step' => 4, 'description' => 'db_config.php included', 'status' => 'success'];
    
    // Step 5: Check if PDO variable exists
    if (!isset($pdo)) {
        throw new Exception("PDO connection variable not found in db_config.php");
    }
    $debug_steps[] = ['step' => 5, 'description' => 'PDO variable exists', 'status' => 'success'];
    
    // Step 6: Test database connection
    $test_query = $pdo->query("SELECT 1 as test");
    $test_result = $test_query->fetch();
    if ($test_result['test'] != 1) {
        throw new Exception("Database connection test failed");
    }
    $debug_steps[] = ['step' => 6, 'description' => 'Database connection test', 'status' => 'success'];
    
    // Step 7: Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        // Try to get as array if JSON decode fails
        $input = $_POST;
    }
    $debug_steps[] = ['step' => 7, 'description' => 'POST data received', 'status' => 'success', 'data' => $input];
    
    // Step 8: Check if required fields exist
    $userId = $input['user_id'] ?? null;
    $username = $input['username'] ?? null;
    
    if (!$userId) {
        throw new Exception("User ID is required");
    }
    $debug_steps[] = ['step' => 8, 'description' => 'Required fields check', 'status' => 'success', 'user_id' => $userId, 'username' => $username];
    
    // Step 9: Check if tbl_user table exists
    $table_check = $pdo->query("SHOW TABLES LIKE 'tbl_user'");
    if ($table_check->rowCount() == 0) {
        throw new Exception("Table 'tbl_user' does not exist");
    }
    $debug_steps[] = ['step' => 9, 'description' => 'tbl_user table exists', 'status' => 'success'];
    
    // Step 10: Check if tbl_manage_icon table exists
    $table_check = $pdo->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($table_check->rowCount() == 0) {
        throw new Exception("Table 'tbl_manage_icon' does not exist");
    }
    $debug_steps[] = ['step' => 10, 'description' => 'tbl_manage_icon table exists', 'status' => 'success'];
    
    // Step 11: Try to fetch user data
    $userQuery = "SELECT manage_icons FROM tbl_user WHERE id = ?";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    $userData = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userData) {
        throw new Exception("User not found with ID: " . $userId);
    }
    $debug_steps[] = ['step' => 11, 'description' => 'User data fetched', 'status' => 'success', 'manage_icons' => $userData['manage_icons']];
    
    // Step 12: Parse manage_icons
    $manageIcons = $userData['manage_icons'];
    if (empty($manageIcons)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No manage icons assigned to this user',
            'data' => [],
            'debug_steps' => $debug_steps
        ]);
        exit();
    }
    
    $iconIds = explode(',', $manageIcons);
    $iconIds = array_map('trim', $iconIds);
    $iconIds = array_filter($iconIds);
    $debug_steps[] = ['step' => 12, 'description' => 'Manage icons parsed', 'status' => 'success', 'icon_ids' => $iconIds];
    
    // Step 13: Fetch icons from tbl_manage_icon
    if (empty($iconIds)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No valid icon IDs found',
            'data' => [],
            'debug_steps' => $debug_steps
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
    $debug_steps[] = ['step' => 13, 'description' => 'Icons fetched', 'status' => 'success', 'count' => count($icons)];
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'All steps completed successfully',
        'data' => $icons,
        'debug_steps' => $debug_steps
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine(),
        'debug_steps' => $debug_steps,
        'error_type' => 'Exception'
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine(),
        'debug_steps' => $debug_steps,
        'error_type' => 'PDOException'
    ]);
} catch (Error $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'PHP Error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine(),
        'debug_steps' => $debug_steps,
        'error_type' => 'Error'
    ]);
}
?>
