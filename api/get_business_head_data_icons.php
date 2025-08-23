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

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    if (!$conn) {
        throw new Exception("Database connection failed");
    }
    
    // Get input data
    $input = file_get_contents('php://input');
    $postData = $_POST;
    $getData = $_GET;
    
    // Log all possible input methods for debugging
    $debugInput = [
        'request_method' => $_SERVER['REQUEST_METHOD'],
        'content_type' => $_SERVER['CONTENT_TYPE'] ?? 'Not set',
        'raw_input' => $input,
        'post_data' => $postData,
        'get_data' => $getData,
        'headers' => getallheaders()
    ];
    
    // Determine which user data to use
    $loggedInUser = null;
    
    // Try to get user data from POST first
    if (!empty($postData)) {
        if (isset($postData['user_id']) && !empty($postData['user_id'])) {
            $loggedInUser = ['id' => $postData['user_id']];
        } elseif (isset($postData['username']) && !empty($postData['username'])) {
            $loggedInUser = ['username' => $postData['username']];
        }
    }
    
    // If no POST data, try GET
    if (!$loggedInUser && !empty($getData)) {
        if (isset($getData['user_id']) && !empty($getData['user_id'])) {
            $loggedInUser = ['id' => $getData['user_id']];
        } elseif (isset($getData['username']) && !empty($getData['username'])) {
            $loggedInUser = ['username' => $getData['username']];
        }
    }
    
    // If still no user data, try raw input
    if (!$loggedInUser && !empty($input)) {
        $jsonInput = json_decode($input, true);
        if ($jsonInput && json_last_error() === JSON_ERROR_NONE) {
            if (isset($jsonInput['user_id']) && !empty($jsonInput['user_id'])) {
                $loggedInUser = ['id' => $jsonInput['user_id']];
            } elseif (isset($jsonInput['username']) && !empty($jsonInput['username'])) {
                $loggedInUser = ['username' => $jsonInput['username']];
            }
        }
    }
    
    if (!$loggedInUser) {
        throw new Exception("No user information provided. Please send user_id or username.");
    }
    
    // Build the query based on available user data
    $query = "";
    $params = [];
    
    if (isset($loggedInUser['id'])) {
        $query = "SELECT data_icons FROM tbl_user WHERE id = ?";
        $params = [$loggedInUser['id']];
    } else {
        $query = "SELECT data_icons FROM tbl_user WHERE username = ?";
        $params = [$loggedInUser['username']];
    }
    
    $stmt = $conn->prepare($query);
    $stmt->execute($params);
    $userResult = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception("User not found in tbl_user");
    }
    
    $dataIcons = $userResult['data_icons'];
    
    if (empty($dataIcons)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No data icons found for this user',
            'data' => [],
            'count' => 0,
            'debug_info' => [
                'logged_in_user' => $loggedInUser,
                'data_icons_raw' => $dataIcons,
                'query_executed' => $query,
                'input_debug' => $debugInput
            ]
        ]);
        exit();
    }
    
    // Parse data_icons - check if it's JSON or comma-separated
    $iconIds = [];
    
    // Try to parse as JSON first
    $jsonIcons = json_decode($dataIcons, true);
    if (json_last_error() === JSON_ERROR_NONE && is_array($jsonIcons)) {
        $iconIds = $jsonIcons;
    } else {
        // Parse as comma-separated string
        $iconIds = array_filter(array_map('trim', explode(',', $dataIcons)));
    }
    
    if (empty($iconIds)) {
        throw new Exception("No valid icon IDs found in data_icons");
    }
    
    // Create placeholders for the IN clause
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    
    // Fetch icon details from tbl_data_icon
    $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description
                  FROM tbl_data_icon
                  WHERE id IN ($placeholders)
                  ORDER BY icon_name ASC";
    
    $iconStmt = $conn->prepare($iconQuery);
    $iconStmt->execute($iconIds);
    $icons = $iconStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get database statistics
    $totalUsersQuery = "SELECT COUNT(*) as total FROM tbl_user";
    $totalUsersStmt = $conn->prepare($totalUsersQuery);
    $totalUsersStmt->execute();
    $totalUsers = $totalUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    $totalDataIconsQuery = "SELECT COUNT(*) as total FROM tbl_data_icon";
    $totalDataIconsStmt = $conn->prepare($totalDataIconsQuery);
    $totalDataIconsStmt->execute();
    $totalDataIcons = $totalDataIconsStmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Data icons fetched successfully for Business Head user',
        'data' => $icons,
        'count' => count($icons),
        'debug_info' => [
            'logged_in_user' => $loggedInUser,
            'data_icons_raw' => $dataIcons,
            'icon_ids_parsed' => $iconIds,
            'query_executed' => $iconQuery,
            'database_stats' => [
                'total_users' => $totalUsers,
                'total_data_icons' => $totalDataIcons,
                'user_icon_count' => count($iconIds),
                'active_icons_found' => count($icons)
            ],
            'input_debug' => $debugInput
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch data icons: ' . $e->getMessage(),
        'debug_info' => [
            'error_details' => $e->getMessage(),
            'trace' => $e->getTraceAsString(),
            'input_debug' => $debugInput ?? 'No input debug available'
        ]
    ]);
}
?>
