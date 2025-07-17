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

    // Get user ID from request
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input || !isset($input['userId'])) {
        throw new Exception('User ID is required');
    }
    
    $userId = trim($input['userId']);
    if (empty($userId)) {
        throw new Exception('User ID cannot be empty');
    }

    // Check if tbl_work_icon table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_work_icon'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('Table tbl_work_icon does not exist');
    }

    // Check if tbl_user_work_icon_permissions table exists
    $permissionTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_work_icon_permissions'");
    if ($permissionTableCheck->num_rows == 0) {
        // If permission table doesn't exist, just get icons without permissions
        $sql = "SELECT id, icon_name, icon_image, icon_description, status, 
                       'No' as has_permission 
                FROM tbl_work_icon 
                WHERE status = 1 OR status = 'Active' OR status IS NULL OR status = '' 
                ORDER BY icon_name ASC";
    } else {
        // Join with permissions table to get user permissions
        $sql = "SELECT wi.id, wi.icon_name, wi.icon_image, wi.icon_description, wi.status,
                       COALESCE(uwip.has_permission, 'No') as has_permission 
                FROM tbl_work_icon wi 
                LEFT JOIN tbl_user_work_icon_permissions uwip ON wi.id = uwip.icon_id AND uwip.user_id = ?
                WHERE wi.status = 1 OR wi.status = 'Active' OR wi.status IS NULL OR wi.status = ''
                ORDER BY wi.icon_name ASC";
    }
    
    if ($permissionTableCheck->num_rows > 0) {
        // Use prepared statement if permission table exists
        $stmt = $conn->prepare($sql);
        if (!$stmt) {
            throw new Exception('Prepare failed: ' . $conn->error);
        }
        $stmt->bind_param('s', $userId);
        $stmt->execute();
        $result = $stmt->get_result();
    } else {
        // Use direct query if permission table doesn't exist
        $result = $conn->query($sql);
    }
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }

    $icons = [];
    $totalCount = 0;
    $activeCount = 0;
    $inactiveCount = 0;
    $permittedCount = 0;

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $status = isset($row['status']) ? $row['status'] : 'Active';
            if ($status == 'Active' || $status == '1' || empty($status)) {
                $activeCount++;
            } else {
                $inactiveCount++;
            }
            $totalCount++;

            // Count permitted icons
            if (isset($row['has_permission']) && $row['has_permission'] === 'Yes') {
                $permittedCount++;
            }

            $iconData = [
                'id' => isset($row['id']) ? $row['id'] : '',
                'icon_name' => isset($row['icon_name']) ? $row['icon_name'] : '',
                'icon_image' => isset($row['icon_image']) ? $row['icon_image'] : '',
                'icon_description' => isset($row['icon_description']) ? $row['icon_description'] : '',
                'status' => $status,
                'has_permission' => isset($row['has_permission']) ? $row['has_permission'] : 'No'
            ];

            $icons[] = $iconData;
        }
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Work icons fetched successfully',
        'data' => $icons,
        'summary' => [
            'total_icons' => $totalCount,
            'active_icons' => $activeCount,
            'inactive_icons' => $inactiveCount,
            'permitted_icons' => $permittedCount
        ]
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_icons' => 0,
            'active_icons' => 0,
            'inactive_icons' => 0,
            'permitted_icons' => 0
        ]
    ]);
}

if (isset($conn)) {
    $conn->close();
}
?> 