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

    $input = file_get_contents('php://input');
    $postData = $_POST;
    $getData = $_GET;

    $debugInput = [
        'request_method' => $_SERVER['REQUEST_METHOD'],
        'content_type' => $_SERVER['CONTENT_TYPE'] ?? 'Not set',
        'raw_input' => $input,
        'post_data' => $postData,
        'get_data' => $getData,
        'headers' => getallheaders()
    ];

    $loggedInUser = null;
    if (!empty($postData)) {
        if (isset($postData['user_id']) && !empty($postData['user_id'])) {
            $loggedInUser = ['id' => $postData['user_id']];
        } elseif (isset($postData['username']) && !empty($postData['username'])) {
            $loggedInUser = ['username' => $postData['username']];
        }
    }
    if (!$loggedInUser && !empty($getData)) {
        if (isset($getData['user_id']) && !empty($getData['user_id'])) {
            $loggedInUser = ['id' => $getData['user_id']];
        } elseif (isset($getData['username']) && !empty($getData['username'])) {
            $loggedInUser = ['username' => $getData['username']];
        }
    }
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

    $query = "";
    $params = [];
    if (isset($loggedInUser['id'])) {
        $query = "SELECT work_icons FROM tbl_user WHERE id = ?";
        $params = [$loggedInUser['id']];
    } else {
        $query = "SELECT work_icons FROM tbl_user WHERE username = ?";
        $params = [$loggedInUser['username']];
    }

    $stmt = $conn->prepare($query);
    $stmt->execute($params);
    $userResult = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$userResult) {
        throw new Exception("User not found in tbl_user");
    }

    $workIcons = $userResult['work_icons'];

    if (empty($workIcons)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No work icons found for this user',
            'data' => [],
            'count' => 0,
            'debug_info' => [
                'logged_in_user' => $loggedInUser,
                'work_icons_raw' => $workIcons,
                'query_executed' => $query,
                'input_debug' => $debugInput
            ]
        ]);
        exit();
    }

    $iconIds = [];
    $jsonIcons = json_decode($workIcons, true);
    if (json_last_error() === JSON_ERROR_NONE && is_array($jsonIcons)) {
        $iconIds = $jsonIcons;
    } else {
        $iconIds = array_filter(array_map('trim', explode(',', $workIcons)));
    }

    if (empty($iconIds)) {
        throw new Exception("No valid icon IDs found in work_icons");
    }

    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description
                  FROM tbl_work_icon
                  WHERE id IN ($placeholders)
                  ORDER BY icon_name ASC";

    $iconStmt = $conn->prepare($iconQuery);
    $iconStmt->execute($iconIds);
    $icons = $iconStmt->fetchAll(PDO::FETCH_ASSOC);

    $totalUsersQuery = "SELECT COUNT(*) as total FROM tbl_user";
    $totalUsersStmt = $conn->prepare($totalUsersQuery);
    $totalUsersStmt->execute();
    $totalUsers = $totalUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];

    $totalWorkIconsQuery = "SELECT COUNT(*) as total FROM tbl_work_icon";
    $totalWorkIconsStmt = $conn->prepare($totalWorkIconsQuery);
    $totalWorkIconsStmt->execute();
    $totalWorkIcons = $totalWorkIconsStmt->fetch(PDO::FETCH_ASSOC)['total'];

    echo json_encode([
        'status' => 'success',
        'message' => 'Work icons fetched successfully for Managing Director user',
        'data' => $icons,
        'count' => count($icons),
        'debug_info' => [
            'logged_in_user' => $loggedInUser,
            'work_icons_raw' => $workIcons,
            'icon_ids_parsed' => $iconIds,
            'query_executed' => $iconQuery,
            'database_stats' => [
                'total_users' => $totalUsers,
                'total_work_icons' => $totalWorkIcons,
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
        'message' => 'Failed to fetch work icons: ' . $e->getMessage(),
        'debug_info' => [
            'error_details' => $e->getMessage(),
            'trace' => $e->getTraceAsString(),
            'input_debug' => $debugInput ?? 'No input debug available'
        ]
    ]);
}
?>
