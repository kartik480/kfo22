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

    // Get data_icons for the user
    $dataIcons = '';
    $dataIconsQuery = $conn->prepare("SELECT data_icons FROM tbl_user WHERE id = ?");
    if (!$dataIconsQuery) {
        throw new Exception('Prepare failed for data_icons: ' . $conn->error);
    }
    $dataIconsQuery->bind_param('s', $userId);
    $dataIconsQuery->execute();
    $dataIconsResult = $dataIconsQuery->get_result();
    if ($dataIconsResult && $dataIconsResult->num_rows > 0) {
        $row = $dataIconsResult->fetch_assoc();
        $dataIcons = $row['data_icons'];
    }
    $dataIconsQuery->close();

    $iconNames = array_filter(array_map('trim', explode(',', $dataIcons)));
    $icons = [];
    if (!empty($iconNames)) {
        // Prepare placeholders for IN clause
        $placeholders = implode(',', array_fill(0, count($iconNames), '?'));
        $types = str_repeat('s', count($iconNames));
        $iconQuery = $conn->prepare("SELECT icon_name, icon_url, icon_image, icon_description FROM tbl_data_icon WHERE icon_name IN ($placeholders)");
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
        'message' => 'Data icons fetched for user',
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