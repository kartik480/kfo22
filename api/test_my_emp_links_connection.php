<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

require_once 'db_config.php';

try {
    global $conn;
    
    $result = [
        'status' => 'success',
        'message' => 'Connection test completed',
        'data' => []
    ];
    
    // Test database connection
    if (!$conn) {
        throw new Exception('Database connection is null');
    }
    
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }
    
    $result['data']['connection'] = 'OK';
    
    // Test if tbl_user exists
    $userTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($userTableCheck && $userTableCheck->num_rows > 0) {
        $result['data']['tbl_user'] = 'EXISTS';
        
        // Check if K RAJESH KUMAR exists
        $userCheck = $conn->query("SELECT id, firstName, lastName FROM tbl_user WHERE id = '8'");
        if ($userCheck && $userCheck->num_rows > 0) {
            $user = $userCheck->fetch_assoc();
            $result['data']['rajesh_kumar'] = [
                'id' => $user['id'],
                'firstName' => $user['firstName'],
                'lastName' => $user['lastName'],
                'fullName' => trim($user['firstName'] . ' ' . $user['lastName'])
            ];
        } else {
            $result['data']['rajesh_kumar'] = 'NOT_FOUND';
        }
    } else {
        $result['data']['tbl_user'] = 'NOT_FOUND';
    }
    
    // Check icon tables
    $manageTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    $result['data']['tbl_manage_icon'] = ($manageTableCheck && $manageTableCheck->num_rows > 0) ? 'EXISTS' : 'NOT_FOUND';
    
    $dataTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_data_icon'");
    $result['data']['tbl_data_icon'] = ($dataTableCheck && $dataTableCheck->num_rows > 0) ? 'EXISTS' : 'NOT_FOUND';
    
    $workTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_work_icon'");
    $result['data']['tbl_work_icon'] = ($workTableCheck && $workTableCheck->num_rows > 0) ? 'EXISTS' : 'NOT_FOUND';
    
    // Check permission tables
    $managePermissionCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_icon_permissions'");
    $result['data']['tbl_user_icon_permissions'] = ($managePermissionCheck && $managePermissionCheck->num_rows > 0) ? 'EXISTS' : 'NOT_FOUND';
    
    $dataPermissionCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_data_icon_permissions'");
    $result['data']['tbl_user_data_icon_permissions'] = ($dataPermissionCheck && $dataPermissionCheck->num_rows > 0) ? 'EXISTS' : 'NOT_FOUND';
    
    $workPermissionCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_work_icon_permissions'");
    $result['data']['tbl_user_work_icon_permissions'] = ($workPermissionCheck && $workPermissionCheck->num_rows > 0) ? 'EXISTS' : 'NOT_FOUND';
    
    echo json_encode($result);

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