<?php
// Regional Business Head Data Icons API
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Handle OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    echo json_encode(['status' => 'success', 'message' => 'OPTIONS handled']);
    exit();
}

try {
    // Include database configuration
    require_once 'db_config.php';
    
    // Get database connection using the function from db_config.php
    $pdo = getConnection();
    
    // Debug: Log all input methods
    $debug_input = [
        'request_method' => $_SERVER['REQUEST_METHOD'],
        'content_type' => $_SERVER['CONTENT_TYPE'] ?? 'not set',
        'raw_input' => file_get_contents('php://input'),
        'post_data' => $_POST,
        'get_data' => $_GET,
        'headers' => getallheaders()
    ];
    
    // Get POST data - Fix JSON parsing issue
    $input = null;
    
    // Method 1: Try to get JSON from raw input
    $rawInput = file_get_contents('php://input');
    if ($rawInput && !empty($rawInput)) {
        $input = json_decode($rawInput, true);
        if (json_last_error() !== JSON_ERROR_NONE) {
            throw new Exception("JSON decode error: " . json_last_error_msg() . " Raw input: " . substr($rawInput, 0, 100));
        }
        if ($input) {
            $debug_input['json_parsed'] = $input;
        }
    }
    
    // Method 2: Fallback to POST array if JSON fails
    if (!$input && !empty($_POST)) {
        $input = $_POST;
        $debug_input['using_post'] = true;
    }
    
    // Method 3: Fallback to GET parameters for testing
    if (!$input && !empty($_GET)) {
        $input = $_GET;
        $debug_input['using_get'] = true;
    }
    
    // If still no input, throw error with debug info
    if (!$input) {
        throw new Exception("No input data received. Debug info: " . json_encode($debug_input));
    }
    
    // Extract user data
    $userId = $input['user_id'] ?? null;
    $username = $input['username'] ?? null;
    
    if (!$userId) {
        throw new Exception("User ID is required. Received input: " . json_encode($input));
    }
    
    // Check if tables exist
    $table_check = $pdo->query("SHOW TABLES LIKE 'tbl_user'");
    if ($table_check->rowCount() == 0) {
        throw new Exception("Table 'tbl_user' does not exist");
    }
    
    $table_check = $pdo->query("SHOW TABLES LIKE 'tbl_data_icon'");
    if ($table_check->rowCount() == 0) {
        throw new Exception("Table 'tbl_data_icon' does not exist");
    }
    
    // Fetch user data
    $userQuery = "SELECT data_icons FROM tbl_user WHERE id = ?";
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
                'database_connected' => true,
                'input_received' => $input,
                'debug_input' => $debug_input
            ]
        ]);
        exit();
    }
    
    $dataIcons = $userData['data_icons'];
    if (empty($dataIcons)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No data icons assigned to this user',
            'data' => [],
            'debug_info' => [
                'user_id' => $userId,
                'data_icons' => $dataIcons,
                'tables_exist' => true,
                'database_connected' => true,
                'input_received' => $input,
                'debug_input' => $debug_input
            ]
        ]);
        exit();
    }
    
               // Parse data_icons - Handle both comma-separated and JSON array formats
           $iconIds = [];
           
           // Check if data_icons is a JSON array
           if (strpos($dataIcons, '[') === 0 && strpos($dataIcons, ']') === strlen($dataIcons) - 1) {
               // Parse as JSON array
               $jsonArray = json_decode($dataIcons, true);
               if (json_last_error() === JSON_ERROR_NONE && is_array($jsonArray)) {
                   $iconIds = $jsonArray;
               } else {
                   throw new Exception("Failed to parse JSON array from data_icons: " . json_last_error_msg());
               }
           } else {
               // Parse as comma-separated string
               $iconIds = explode(',', $dataIcons);
               $iconIds = array_map('trim', $iconIds);
           }
           
           $iconIds = array_filter($iconIds);
    
    if (empty($iconIds)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No valid icon IDs found',
            'data' => [],
            'debug_info' => [
                'user_id' => $userId,
                'data_icons' => $dataIcons,
                'parsed_ids' => $iconIds,
                'input_received' => $input,
                'debug_input' => $debug_input
            ]
        ]);
        exit();
    }
    
               // Debug: Log the parsed icon IDs
           error_log("Parsed icon IDs: " . json_encode($iconIds));
           
           // Fetch icons from tbl_data_icon
           $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
           $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description 
                         FROM tbl_data_icon 
                         WHERE id IN ($placeholders) 
                         ORDER BY icon_name ASC";
           
           error_log("SQL Query: " . $iconQuery);
           error_log("Icon IDs for query: " . json_encode($iconIds));
           
           $iconStmt = $pdo->prepare($iconQuery);
           $iconStmt->execute($iconIds);
           $icons = $iconStmt->fetchAll(PDO::FETCH_ASSOC);
           
           error_log("Icons found: " . count($icons));
    
    // Success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Data icons fetched successfully',
        'data' => $icons,
        'debug_info' => [
            'user_id' => $userId,
            'username' => $username,
            'data_icons' => $dataIcons,
            'icon_ids' => $iconIds,
            'icons_found' => count($icons),
            'tables_exist' => true,
            'database_connected' => true,
            'input_received' => $input,
            'debug_input' => $debug_input
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => 'Exception',
            'error_message' => $e->getMessage(),
            'user_id' => $userId ?? 'not provided',
            'username' => $username ?? 'not provided',
            'input_received' => $input ?? 'none',
            'php_version' => phpversion(),
            'server_time' => date('Y-m-d H:i:s'),
            'debug_input' => $debug_input ?? 'none'
        ]
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => 'PDOException',
            'error_message' => $e->getMessage(),
            'user_id' => $userId ?? 'not provided',
            'username' => $username ?? 'not provided',
            'input_received' => $input ?? 'none',
            'pdo_available' => extension_loaded('pdo'),
            'pdo_mysql_available' => extension_loaded('pdo_mysql'),
            'debug_input' => $debug_input ?? 'none'
        ]
    ]);
} catch (Error $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'PHP error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => 'Error',
            'error_message' => $e->getMessage(),
            'user_id' => $userId ?? 'not provided',
            'username' => $username ?? 'not provided',
            'input_received' => $input ?? 'none',
            'php_version' => phpversion(),
            'debug_input' => $debug_input ?? 'none'
        ]
    ]);
}
?>
