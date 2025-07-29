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
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : 41;
    
    // Test 1: Check tbl_manage_icon - get all IDs
    $iconQuery = "SELECT id FROM tbl_manage_icon ORDER BY id ASC";
    $iconStmt = $pdo->prepare($iconQuery);
    $iconStmt->execute();
    $iconIds = $iconStmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Test 2: Check tbl_user - get user's manage_icons
    $userQuery = "SELECT id, username, manage_icons FROM tbl_user WHERE id = ?";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    $userData = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userData) {
        throw new Exception('User not found with ID: ' . $userId);
    }
    
    // Test 3: Check if user's manage_icons match any icon IDs
    $userManageIcons = $userData['manage_icons'];
    $userIconIds = [];
    $matchingIcons = [];
    
    if (!empty($userManageIcons)) {
        $userIconIds = explode(',', $userManageIcons);
        $userIconIds = array_map('trim', $userIconIds);
        $userIconIds = array_filter($userIconIds);
        
        // Find matching icons
        foreach ($userIconIds as $userIconId) {
            if (in_array($userIconId, $iconIds)) {
                $matchingIcons[] = $userIconId;
            }
        }
    }

    echo json_encode([
        'success' => true,
        'message' => 'Data retrieved successfully',
        'user_id' => $userId,
        'user_username' => $userData['username'],
        'tbl_manage_icon_ids' => $iconIds,
        'tbl_manage_icon_count' => count($iconIds),
        'user_manage_icons_raw' => $userManageIcons,
        'user_manage_icons_parsed' => $userIconIds,
        'user_manage_icons_count' => count($userIconIds),
        'matching_icons' => $matchingIcons,
        'matching_count' => count($matchingIcons),
        'analysis' => [
            'has_manage_icons_data' => !empty($userManageIcons),
            'has_icon_table_data' => count($iconIds) > 0,
            'has_matches' => count($matchingIcons) > 0
        ]
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}
?> 