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
    if (!isset($input['userId']) || !isset($input['permissionType']) || !isset($input['permissionValue'])) {
        throw new Exception('Missing required fields: userId, permissionType, permissionValue');
    }

    $userId = trim($input['userId']);
    $permissionType = trim($input['permissionType']);
    $permissionValue = trim($input['permissionValue']);

    // Validate permission types
    $validPermissions = [
        'emp_manage_permission',
        'emp_data_permission', 
        'emp_work_permission',
        'payout_permission'
    ];

    if (!in_array($permissionType, $validPermissions)) {
        throw new Exception('Invalid permission type: ' . $permissionType);
    }

    // Check if tbl_user table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('Table tbl_user does not exist');
    }

    // Check if the permission columns exist, if not create them
    $columnsQuery = $conn->query("DESCRIBE tbl_user");
    $columns = [];
    while ($column = $columnsQuery->fetch_assoc()) {
        $columns[] = $column['Field'];
    }

    // Add permission columns if they don't exist
    foreach ($validPermissions as $permission) {
        if (!in_array($permission, $columns)) {
            $addColumnSql = "ALTER TABLE tbl_user ADD COLUMN $permission VARCHAR(10) DEFAULT 'No'";
            if (!$conn->query($addColumnSql)) {
                throw new Exception('Failed to add column ' . $permission . ': ' . $conn->error);
            }
        }
    }

    // Update the specific permission for the user
    $sql = "UPDATE tbl_user SET $permissionType = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        throw new Exception('Prepare failed: ' . $conn->error);
    }

    $stmt->bind_param('ss', $permissionValue, $userId);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                'status' => 'success',
                'message' => 'Permission updated successfully',
                'data' => [
                    'userId' => $userId,
                    'permissionType' => $permissionType,
                    'permissionValue' => $permissionValue
                ]
            ]);
        } else {
            throw new Exception('User not found or no changes made');
        }
    } else {
        throw new Exception('Failed to update permission: ' . $stmt->error);
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?> 