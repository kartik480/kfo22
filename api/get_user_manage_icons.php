<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

require_once 'db_config.php';

try {
    global $conn;
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }

    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input || !isset($input['userId'])) {
        throw new Exception('Missing required field: userId');
    }
    $userId = trim($input['userId']);

    // Get manage_icons for the user
    $manageIcons = '';
    $manageIconsQuery = $conn->prepare("SELECT manage_icons FROM tbl_user WHERE id = ?");
    if (!$manageIconsQuery) {
        throw new Exception('Prepare failed for manage_icons: ' . $conn->error);
    }
    $manageIconsQuery->bind_param('s', $userId);
    $manageIconsQuery->execute();
    $manageIconsResult = $manageIconsQuery->get_result();
    if ($manageIconsResult && $manageIconsResult->num_rows > 0) {
        $row = $manageIconsResult->fetch_assoc();
        $manageIcons = $row['manage_icons'];
    }
    $manageIconsQuery->close();

    $iconNames = array_filter(array_map('trim', explode(',', $manageIcons)));
    $icons = [];
    if (!empty($iconNames)) {
        // Prepare placeholders for IN clause
        $placeholders = implode(',', array_fill(0, count($iconNames), '?'));
        $types = str_repeat('s', count($iconNames));
        $iconQuery = $conn->prepare("SELECT icon_name, icon_url, icon_image, icon_description FROM tbl_manage_icon WHERE icon_name IN ($placeholders)");
        if ($iconQuery) {
            $iconQuery->bind_param($types, ...$iconNames);
            $iconQuery->execute();
            $result = $iconQuery->get_result();
            while ($row = $result->fetch_assoc()) {
                $icons[] = [
                    'icon_name' => $row['icon_name'],
                    'icon_url' => $row['icon_url'],
                    'icon_image' => $row['icon_image'],
                    'icon_description' => $row['icon_description']
                ];
            }
            $iconQuery->close();
        }
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Manage icons fetched for user',
        'data' => $icons
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'data' => []
    ]);
}

if (isset($conn)) {
    $conn->close();
} 