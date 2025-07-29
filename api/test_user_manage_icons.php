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

    // Get user ID from request
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    
    if (!$userId) {
        throw new Exception('User ID is required');
    }

    // Check if user exists and get manage_icons data
    $userQuery = "SELECT id, username, manage_icons FROM tbl_user WHERE id = ?";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    $userData = $userStmt->fetch(PDO::FETCH_ASSOC);

    if (!$userData) {
        echo json_encode([
            'success' => false,
            'message' => 'User not found with ID: ' . $userId,
            'user_data' => null,
            'manage_icons_raw' => null,
            'icon_ids' => [],
            'manage_icon_data' => []
        ]);
        exit();
    }

    $manageIcons = $userData['manage_icons'];
    
    // Check if manage_icons is empty, null, or has data
    $iconIds = [];
    $manageIconData = [];
    
    if (!empty($manageIcons)) {
        // Parse the manage_icons (assuming it's a comma-separated list of icon IDs)
        $iconIds = explode(',', $manageIcons);
        $iconIds = array_map('trim', $iconIds);
        $iconIds = array_filter($iconIds); // Remove empty values
        
        if (!empty($iconIds)) {
            // Create placeholders for the IN clause
            $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
            
            // Fetch manage icons data from tbl_manage_icon
            $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description, status 
                          FROM tbl_manage_icon 
                          WHERE id IN ($placeholders)
                          ORDER BY icon_name ASC";
            
            $iconStmt = $pdo->prepare($iconQuery);
            $iconStmt->execute($iconIds);
            $manageIconData = $iconStmt->fetchAll(PDO::FETCH_ASSOC);
        }
    }

    echo json_encode([
        'success' => true,
        'message' => 'User data retrieved successfully',
        'user_data' => [
            'id' => $userData['id'],
            'username' => $userData['username']
        ],
        'manage_icons_raw' => $manageIcons,
        'icon_ids' => $iconIds,
        'manage_icon_data' => $manageIconData,
        'total_icons_found' => count($manageIconData)
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'user_data' => null,
        'manage_icons_raw' => null,
        'icon_ids' => [],
        'manage_icon_data' => []
    ]);
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage(),
        'user_data' => null,
        'manage_icons_raw' => null,
        'icon_ids' => [],
        'manage_icon_data' => []
    ]);
}
?> 