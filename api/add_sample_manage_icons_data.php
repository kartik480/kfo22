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

    // Check if tbl_manage_icon has any data
    $countQuery = "SELECT COUNT(*) as count FROM tbl_manage_icon";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute();
    $currentCount = $countStmt->fetch(PDO::FETCH_ASSOC)['count'];

    if ($currentCount > 0) {
        echo json_encode([
            'success' => true,
            'message' => 'tbl_manage_icon already has ' . $currentCount . ' records',
            'current_count' => $currentCount
        ]);
        exit();
    }

    // Add sample data to tbl_manage_icon
    $sampleData = [
        [
            'icon_name' => 'Employee Management',
            'icon_url' => 'https://kfinone.com/employee',
            'icon_image' => 'emp_management.png',
            'icon_description' => 'Manage employee data and information',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Data Analytics',
            'icon_url' => 'https://kfinone.com/analytics',
            'icon_image' => 'data_analytics.png',
            'icon_description' => 'View business analytics and reports',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Work Management',
            'icon_url' => 'https://kfinone.com/work',
            'icon_image' => 'work_management.png',
            'icon_description' => 'Manage work tasks and projects',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Team Dashboard',
            'icon_url' => 'https://kfinone.com/team',
            'icon_image' => 'team_dashboard.png',
            'icon_description' => 'View team performance and metrics',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Reports',
            'icon_url' => 'https://kfinone.com/reports',
            'icon_image' => 'reports.png',
            'icon_description' => 'Generate and view reports',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Settings',
            'icon_url' => 'https://kfinone.com/settings',
            'icon_image' => 'settings.png',
            'icon_description' => 'Manage application settings',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Profile',
            'icon_url' => 'https://kfinone.com/profile',
            'icon_image' => 'profile.png',
            'icon_description' => 'View and edit user profile',
            'status' => 'active'
        ],
        [
            'icon_name' => 'Notifications',
            'icon_url' => 'https://kfinone.com/notifications',
            'icon_image' => 'notifications.png',
            'icon_description' => 'View system notifications',
            'status' => 'active'
        ]
    ];

    $insertQuery = "INSERT INTO tbl_manage_icon (icon_name, icon_url, icon_image, icon_description, status) VALUES (?, ?, ?, ?, ?)";
    $insertStmt = $pdo->prepare($insertQuery);

    $insertedCount = 0;
    foreach ($sampleData as $data) {
        $insertStmt->execute([
            $data['icon_name'],
            $data['icon_url'],
            $data['icon_image'],
            $data['icon_description'],
            $data['status']
        ]);
        $insertedCount++;
    }

    echo json_encode([
        'success' => true,
        'message' => 'Sample data added successfully to tbl_manage_icon',
        'inserted_count' => $insertedCount,
        'total_records' => $insertedCount
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