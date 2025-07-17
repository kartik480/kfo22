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

    echo json_encode([
        'status' => 'success',
        'message' => 'Testing K RAJESH KUMAR (User ID: 8)',
        'tests' => []
    ]);

    $tests = [];

    // Test 1: Check if tbl_user table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows > 0) {
        $tests[] = [
            'test' => 'tbl_user table exists',
            'status' => 'PASS',
            'details' => 'Table found'
        ];
    } else {
        $tests[] = [
            'test' => 'tbl_user table exists',
            'status' => 'FAIL',
            'details' => 'Table not found'
        ];
        throw new Exception('tbl_user table does not exist');
    }

    // Test 2: Check table structure
    $columnsQuery = $conn->query("DESCRIBE tbl_user");
    $columns = [];
    while ($row = $columnsQuery->fetch_assoc()) {
        $columns[] = $row['Field'];
    }
    
    $tests[] = [
        'test' => 'Table structure',
        'status' => 'INFO',
        'details' => 'Columns found: ' . implode(', ', $columns)
    ];

    // Test 3: Check if user ID 8 exists
    $userQuery = "SELECT id, firstName, lastName, username, status FROM tbl_user WHERE id = 8";
    $userResult = $conn->query($userQuery);
    
    if ($userResult->num_rows > 0) {
        $user = $userResult->fetch_assoc();
        $tests[] = [
            'test' => 'K RAJESH KUMAR exists (ID: 8)',
            'status' => 'PASS',
            'details' => 'User found: ' . $user['firstName'] . ' ' . $user['lastName'] . ' (Username: ' . $user['username'] . ')'
        ];
    } else {
        $tests[] = [
            'test' => 'K RAJESH KUMAR exists (ID: 8)',
            'status' => 'FAIL',
            'details' => 'User ID 8 not found'
        ];
    }

    // Test 4: Check all users to see if there are any with similar names
    $allUsersQuery = "SELECT id, firstName, lastName, username FROM tbl_user WHERE firstName LIKE '%rajesh%' OR firstName LIKE '%RAJESH%' OR lastName LIKE '%kumar%' OR lastName LIKE '%KUMAR%'";
    $allUsersResult = $conn->query($allUsersQuery);
    
    $similarUsers = [];
    while ($row = $allUsersResult->fetch_assoc()) {
        $similarUsers[] = [
            'id' => $row['id'],
            'name' => $row['firstName'] . ' ' . $row['lastName'],
            'username' => $row['username']
        ];
    }
    
    $tests[] = [
        'test' => 'Users with similar names',
        'status' => 'INFO',
        'details' => 'Found ' . count($similarUsers) . ' users with similar names',
        'users' => $similarUsers
    ];

    // Test 5: Check if user ID 8 has any permissions
    $permissionQuery = "SELECT COUNT(*) as count FROM tbl_user_icon_permissions WHERE user_id = '8'";
    $permissionResult = $conn->query($permissionQuery);
    $permissionCount = $permissionResult->fetch_assoc()['count'];
    
    $tests[] = [
        'test' => 'K RAJESH KUMAR permissions',
        'status' => 'INFO',
        'details' => 'Has ' . $permissionCount . ' manage icon permissions'
    ];

    // Test 6: Check data icon permissions
    $dataPermissionQuery = "SELECT COUNT(*) as count FROM tbl_user_data_icon_permissions WHERE user_id = '8'";
    $dataPermissionResult = $conn->query($dataPermissionQuery);
    $dataPermissionCount = $dataPermissionResult->fetch_assoc()['count'];
    
    $tests[] = [
        'test' => 'K RAJESH KUMAR data permissions',
        'status' => 'INFO',
        'details' => 'Has ' . $dataPermissionCount . ' data icon permissions'
    ];

    // Test 7: Check work icon permissions
    $workPermissionQuery = "SELECT COUNT(*) as count FROM tbl_user_work_icon_permissions WHERE user_id = '8'";
    $workPermissionResult = $conn->query($workPermissionQuery);
    $workPermissionCount = $workPermissionResult->fetch_assoc()['count'];
    
    $tests[] = [
        'test' => 'K RAJESH KUMAR work permissions',
        'status' => 'INFO',
        'details' => 'Has ' . $workPermissionCount . ' work icon permissions'
    ];

    echo json_encode([
        'status' => 'success',
        'message' => 'K RAJESH KUMAR user test completed',
        'tests' => $tests
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'tests' => $tests ?? []
    ]);
}

if (isset($conn)) {
    $conn->close();
}
?> 