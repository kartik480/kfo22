<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    // Validate input
    if (!$input) {
        throw new Exception('Invalid JSON input');
    }
    
    $user_id = isset($input['user_id']) ? $input['user_id'] : null;
    $username = isset($input['username']) ? $input['username'] : null;
    
    if (!$user_id && !$username) {
        throw new Exception('Either user_id or username is required');
    }
    
    // Build the query to get data_icons from tbl_user
    $userQuery = "SELECT data_icons FROM tbl_user WHERE ";
    $userParams = [];
    
    if ($user_id) {
        $userQuery .= "id = ?";
        $userParams[] = $user_id;
    } else {
        $userQuery .= "username = ?";
        $userParams[] = $username;
    }
    
    // Execute user query
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute($userParams);
    $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception('User not found');
    }
    
    $data_icons = $userResult['data_icons'];
    
    // If data_icons is empty or null, return empty array
    if (empty($data_icons)) {
        echo json_encode([
            'status' => 'success',
            'data' => [],
            'message' => 'No data icons found for this user'
        ]);
        exit();
    }
    
    // Parse the data_icons (assuming it's comma-separated IDs)
    $icon_ids = explode(',', $data_icons);
    $icon_ids = array_map('trim', $icon_ids); // Remove whitespace
    $icon_ids = array_filter($icon_ids); // Remove empty values
    
    if (empty($icon_ids)) {
        echo json_encode([
            'status' => 'success',
            'data' => [],
            'message' => 'No valid icon IDs found'
        ]);
        exit();
    }
    
    // Build the query to get icon details from tbl_data_icon
    $placeholders = str_repeat('?,', count($icon_ids) - 1) . '?';
    $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description 
                  FROM tbl_data_icon 
                  WHERE id IN ($placeholders)";
    
    // Execute icon query
    $iconStmt = $pdo->prepare($iconQuery);
    $iconStmt->execute($icon_ids);
    $iconResults = $iconStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $formattedData = [];
    foreach ($iconResults as $icon) {
        $formattedData[] = [
            'id' => $icon['id'],
            'icon_name' => $icon['icon_name'],
            'icon_url' => $icon['icon_url'],
            'icon_image' => $icon['icon_image'],
            'icon_description' => $icon['icon_description']
        ];
    }
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'data' => $formattedData,
        'message' => 'Data icons retrieved successfully',
        'count' => count($formattedData)
    ]);
    
} catch (PDOException $e) {
    // Database error
    error_log("Database error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection error',
        'debug' => $e->getMessage() // Remove this in production
    ]);
    
} catch (Exception $e) {
    // General error
    error_log("API error: " . $e->getMessage());
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>
