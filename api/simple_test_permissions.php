<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

try {
    // Test 1: Check if db_config.php exists and can be included
    if (!file_exists('db_config.php')) {
        throw new Exception('db_config.php file not found');
    }
    
    require_once 'db_config.php';
    
    // Test 2: Check if $conn is available
    if (!isset($conn)) {
        throw new Exception('Database connection variable $conn not found');
    }
    
    // Test 3: Check database connection
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }
    
    // Test 4: Check if tbl_user table exists
    $userTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($userTableCheck->num_rows == 0) {
        throw new Exception('tbl_user table does not exist');
    }
    
    // Test 5: Check if user ID 8 exists
    $userCheck = $conn->query("SELECT id, firstName, lastName FROM tbl_user WHERE id = 8");
    if ($userCheck->num_rows == 0) {
        throw new Exception('User ID 8 (K RAJESH KUMAR) not found in tbl_user table');
    }
    
    $user = $userCheck->fetch_assoc();
    
    // Test 6: Check if icon tables exist
    $tables = ['tbl_manage_icon', 'tbl_data_icon', 'tbl_work_icon'];
    $tableStatus = [];
    
    foreach ($tables as $table) {
        $tableCheck = $conn->query("SHOW TABLES LIKE '$table'");
        $tableStatus[$table] = $tableCheck->num_rows > 0 ? 'EXISTS' : 'NOT_FOUND';
    }
    
    // Test 7: Check if permission tables exist
    $permissionTables = ['tbl_user_icon_permissions', 'tbl_user_data_icon_permissions', 'tbl_user_work_icon_permissions'];
    $permissionTableStatus = [];
    
    foreach ($permissionTables as $table) {
        $tableCheck = $conn->query("SHOW TABLES LIKE '$table'");
        $permissionTableStatus[$table] = $tableCheck->num_rows > 0 ? 'EXISTS' : 'NOT_FOUND';
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Basic database tests passed',
        'data' => [
            'user' => $user,
            'icon_tables' => $tableStatus,
            'permission_tables' => $permissionTableStatus
        ]
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