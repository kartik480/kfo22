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
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : 41; // Default to 41 if not provided
    
    // First, check if user exists
    $userQuery = "SELECT id, username FROM tbl_user WHERE id = ?";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute([$userId]);
    $userData = $userStmt->fetch(PDO::FETCH_ASSOC);

    if (!$userData) {
        throw new Exception('User not found with ID: ' . $userId);
    }

    // Check if tbl_manage_icon has any data
    $iconCountQuery = "SELECT COUNT(*) as count FROM tbl_manage_icon";
    $iconCountStmt = $pdo->prepare($iconCountQuery);
    $iconCountStmt->execute();
    $iconCount = $iconCountStmt->fetch(PDO::FETCH_ASSOC)['count'];

    if ($iconCount == 0) {
        // Add sample manage_icon data
        $sampleIcons = [
            ['icon_name' => 'Employee Management', 'icon_url' => 'https://example.com/emp', 'icon_image' => 'emp_icon.png', 'icon_description' => 'Manage employee data and information', 'status' => 'active'],
            ['icon_name' => 'Data Analytics', 'icon_url' => 'https://example.com/analytics', 'icon_image' => 'analytics_icon.png', 'icon_description' => 'View business analytics and reports', 'status' => 'active'],
            ['icon_name' => 'Work Management', 'icon_url' => 'https://example.com/work', 'icon_image' => 'work_icon.png', 'icon_description' => 'Manage work tasks and projects', 'status' => 'active'],
            ['icon_name' => 'Team Dashboard', 'icon_url' => 'https://example.com/team', 'icon_image' => 'team_icon.png', 'icon_description' => 'View team performance and metrics', 'status' => 'active'],
            ['icon_name' => 'Reports', 'icon_url' => 'https://example.com/reports', 'icon_image' => 'reports_icon.png', 'icon_description' => 'Generate and view reports', 'status' => 'active']
        ];

        $insertIconQuery = "INSERT INTO tbl_manage_icon (icon_name, icon_url, icon_image, icon_description, status) VALUES (?, ?, ?, ?, ?)";
        $insertIconStmt = $pdo->prepare($insertIconQuery);

        foreach ($sampleIcons as $icon) {
            $insertIconStmt->execute([
                $icon['icon_name'],
                $icon['icon_url'],
                $icon['icon_image'],
                $icon['icon_description'],
                $icon['status']
            ]);
        }

        echo json_encode([
            'success' => true,
            'message' => 'Sample manage_icon data added successfully',
            'icons_added' => count($sampleIcons)
        ]);
    } else {
        // Get existing icon IDs
        $existingIconsQuery = "SELECT id FROM tbl_manage_icon LIMIT 5";
        $existingIconsStmt = $pdo->prepare($existingIconsQuery);
        $existingIconsStmt->execute();
        $existingIcons = $existingIconsStmt->fetchAll(PDO::FETCH_COLUMN);

        // Update user's manage_icons with existing icon IDs
        $manageIconsString = implode(',', $existingIcons);
        
        $updateUserQuery = "UPDATE tbl_user SET manage_icons = ? WHERE id = ?";
        $updateUserStmt = $pdo->prepare($updateUserQuery);
        $updateUserStmt->execute([$manageIconsString, $userId]);

        echo json_encode([
            'success' => true,
            'message' => 'User manage_icons updated with existing icon IDs',
            'user_id' => $userId,
            'manage_icons' => $manageIconsString,
            'icon_ids' => $existingIcons
        ]);
    }

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