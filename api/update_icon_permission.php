<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
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
    if (!isset($input['iconId']) || !isset($input['userId']) || !isset($input['hasPermission'])) {
        throw new Exception('Missing required fields: iconId, userId, hasPermission');
    }

    $iconId = trim($input['iconId']);
    $userId = trim($input['userId']);
    $hasPermission = trim($input['hasPermission']);

    // Validate permission value
    if (!in_array($hasPermission, ['Yes', 'No'])) {
        throw new Exception('Invalid permission value. Must be "Yes" or "No"');
    }

    // Check if tbl_user_icon_permissions table exists, if not create it
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_icon_permissions'");
    if ($tableCheck->num_rows == 0) {
        $createTableSql = "CREATE TABLE tbl_user_icon_permissions (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id VARCHAR(50) NOT NULL,
            icon_id VARCHAR(50) NOT NULL,
            has_permission VARCHAR(10) DEFAULT 'No',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE KEY unique_user_icon (user_id, icon_id)
        )";
        
        if (!$conn->query($createTableSql)) {
            throw new Exception('Failed to create tbl_user_icon_permissions table: ' . $conn->error);
        }
    }

    // Check if permission already exists for this user and icon
    $checkSql = "SELECT id FROM tbl_user_icon_permissions WHERE user_id = ? AND icon_id = ?";
    $checkStmt = $conn->prepare($checkSql);
    
    if (!$checkStmt) {
        throw new Exception('Prepare failed for check: ' . $conn->error);
    }

    $checkStmt->bind_param('ss', $userId, $iconId);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();

    if ($checkResult->num_rows > 0) {
        // Update existing permission
        $updateSql = "UPDATE tbl_user_icon_permissions SET has_permission = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND icon_id = ?";
        $updateStmt = $conn->prepare($updateSql);
        
        if (!$updateStmt) {
            throw new Exception('Prepare failed for update: ' . $conn->error);
        }

        $updateStmt->bind_param('sss', $hasPermission, $userId, $iconId);
        
        if ($updateStmt->execute()) {
            echo json_encode([
                'status' => 'success',
                'message' => 'Icon permission updated successfully',
                'data' => [
                    'userId' => $userId,
                    'iconId' => $iconId,
                    'hasPermission' => $hasPermission
                ]
            ]);
        } else {
            throw new Exception('Failed to update icon permission: ' . $updateStmt->error);
        }
        
        $updateStmt->close();
    } else {
        // Insert new permission
        $insertSql = "INSERT INTO tbl_user_icon_permissions (user_id, icon_id, has_permission) VALUES (?, ?, ?)";
        $insertStmt = $conn->prepare($insertSql);
        
        if (!$insertStmt) {
            throw new Exception('Prepare failed for insert: ' . $conn->error);
        }

        $insertStmt->bind_param('sss', $userId, $iconId, $hasPermission);
        
        if ($insertStmt->execute()) {
            echo json_encode([
                'status' => 'success',
                'message' => 'Icon permission created successfully',
                'data' => [
                    'userId' => $userId,
                    'iconId' => $iconId,
                    'hasPermission' => $hasPermission
                ]
            ]);
        } else {
            throw new Exception('Failed to create icon permission: ' . $insertStmt->error);
        }
        
        $insertStmt->close();
    }

    // After updating/inserting permission, update manage_icons in tbl_user
    // Fetch icon_name for the given iconId
    $iconName = '';
    $iconNameQuery = $conn->prepare("SELECT icon_name FROM tbl_manage_icon WHERE id = ?");
    if ($iconNameQuery) {
        $iconNameQuery->bind_param('s', $iconId);
        $iconNameQuery->execute();
        $iconNameResult = $iconNameQuery->get_result();
        if ($iconNameResult && $iconNameResult->num_rows > 0) {
            $row = $iconNameResult->fetch_assoc();
            $iconName = $row['icon_name'];
        }
        $iconNameQuery->close();
    }
    if ($iconName !== '') {
        // Get permission type (manage, data, work)
        $permissionType = isset($input['permissionType']) ? trim($input['permissionType']) : 'manage';
        $columnMap = [
            'manage' => 'manage_icons',
            'data' => 'data_icons',
            'work' => 'work_icons'
        ];
        $columnName = isset($columnMap[$permissionType]) ? $columnMap[$permissionType] : 'manage_icons';

        // Get current icons for the user for the correct column
        $iconsValue = '';
        $iconsQuery = $conn->prepare("SELECT $columnName FROM tbl_user WHERE id = ?");
        if ($iconsQuery) {
            $iconsQuery->bind_param('s', $userId);
            $iconsQuery->execute();
            $iconsResult = $iconsQuery->get_result();
            if ($iconsResult && $iconsResult->num_rows > 0) {
                $row = $iconsResult->fetch_assoc();
                $iconsValue = $row[$columnName];
            }
            $iconsQuery->close();
        }
        // Convert to array
        $iconArray = array_filter(array_map('trim', explode(',', $iconsValue)));
        if ($hasPermission === 'Yes') {
            // Add iconName if not present
            if (!in_array($iconName, $iconArray)) {
                $iconArray[] = $iconName;
            }
        } else {
            // Remove iconName if present
            $iconArray = array_filter($iconArray, function($name) use ($iconName) {
                return $name !== $iconName;
            });
        }
        // Convert back to comma-separated string
        $newIconsValue = implode(',', $iconArray);
        // Update tbl_user
        $updateUserQuery = $conn->prepare("UPDATE tbl_user SET $columnName = ? WHERE id = ?");
        if ($updateUserQuery) {
            $updateUserQuery->bind_param('ss', $newIconsValue, $userId);
            $updateUserQuery->execute();
            $updateUserQuery->close();
        }
    }

    $checkStmt->close();

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