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
    if (!$input) {
        throw new Exception('Invalid JSON input');
    }

    // Validate required fields
    if (!isset($input['userId'])) {
        throw new Exception('Missing required field: userId');
    }

    $userId = trim($input['userId']);
    
    // Get Manage Icons with permission status for the specific user
    $query = "SELECT mi.id, mi.icon_name, mi.icon_image, mi.icon_description, mi.status, uip.has_permission 
              FROM tbl_manage_icon mi 
              LEFT JOIN tbl_user_icon_permissions uip ON mi.id = uip.icon_id AND uip.user_id = ?
              WHERE mi.status = 1 OR mi.status = 'Active' OR mi.status IS NULL OR mi.status = ''
              ORDER BY mi.icon_name ASC";
    
    $stmt = $conn->prepare($query);
    if (!$stmt) {
        throw new Exception('Failed to prepare statement: ' . $conn->error);
    }
    
    $stmt->bind_param('s', $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $icons = [];
    while ($row = $result->fetch_assoc()) {
        $icons[] = [
            'id' => $row['id'],
            'icon_name' => $row['icon_name'],
            'icon_image' => $row['icon_image'],
            'icon_description' => $row['icon_description'],
            'status' => $row['status'],
            'has_permission' => $row['has_permission'] ?? 'No'
        ];
    }
    
    $stmt->close();
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Manage icons with permissions fetched successfully',
        'data' => $icons
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}

if (isset($conn)) {
    $conn->close();
}
?> 