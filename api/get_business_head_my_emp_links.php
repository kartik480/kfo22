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

    // First, get the manage_icons from tbl_user for the specific user
    $userQuery = "SELECT manage_icons FROM tbl_user WHERE id = ?";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    $userData = $userStmt->fetch(PDO::FETCH_ASSOC);

    if (!$userData) {
        throw new Exception('User not found');
    }

    $manageIcons = $userData['manage_icons'];
    
    // If manage_icons is empty or null, try to get some default icons
    if (empty($manageIcons)) {
        // Get some default icons from tbl_manage_icon (first 5)
        $defaultIconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description, status 
                            FROM tbl_manage_icon 
                            ORDER BY id ASC 
                            LIMIT 5";
        $defaultIconStmt = $pdo->prepare($defaultIconQuery);
        $defaultIconStmt->execute();
        $defaultIcons = $defaultIconStmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (empty($defaultIcons)) {
            // If no icons exist in tbl_manage_icon, return empty array
            echo json_encode([
                'success' => true,
                'message' => 'No manage icons found in database',
                'data' => []
            ]);
            exit();
        }
        
        // Return the default icons
        $formattedIcons = [];
        foreach ($defaultIcons as $icon) {
            $formattedIcons[] = [
                'id' => $icon['id'],
                'icon_name' => $icon['icon_name'],
                'icon_url' => $icon['icon_url'],
                'icon_image' => $icon['icon_image'],
                'icon_description' => $icon['icon_description'],
                'status' => $icon['status']
            ];
        }
        
        echo json_encode([
            'success' => true,
            'message' => 'Default manage icons retrieved successfully',
            'data' => $formattedIcons,
            'total_count' => count($formattedIcons)
        ]);
        exit();
    }

    // Parse the manage_icons (assuming it's a comma-separated list of icon IDs)
    $iconIds = explode(',', $manageIcons);
    $iconIds = array_map('trim', $iconIds);
    $iconIds = array_filter($iconIds); // Remove empty values

    if (empty($iconIds)) {
        echo json_encode([
            'success' => true,
            'message' => 'No valid icon IDs found',
            'data' => []
        ]);
        exit();
    }

    // Create placeholders for the IN clause
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    
    // Fetch manage icons data from tbl_manage_icon
    $iconQuery = "SELECT id, icon_name, icon_url, icon_image, icon_description, status 
                  FROM tbl_manage_icon 
                  WHERE id IN ($placeholders)
                  ORDER BY icon_name ASC";
    
    $iconStmt = $pdo->prepare($iconQuery);
    $iconStmt->execute($iconIds);
    $icons = $iconStmt->fetchAll(PDO::FETCH_ASSOC);

    // Format the response
    $formattedIcons = [];
    foreach ($icons as $icon) {
        $formattedIcons[] = [
            'id' => $icon['id'],
            'icon_name' => $icon['icon_name'],
            'icon_url' => $icon['icon_url'],
            'icon_image' => $icon['icon_image'],
            'icon_description' => $icon['icon_description'],
            'status' => $icon['status']
        ];
    }

    echo json_encode([
        'success' => true,
        'message' => 'Manage icons retrieved successfully',
        'data' => $formattedIcons,
        'total_count' => count($formattedIcons)
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage(),
        'data' => []
    ]);
}
?> 