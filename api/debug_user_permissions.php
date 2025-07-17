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
    $debug = [];

    // Debug: Check if tables exist
    $tables = ['tbl_manage_icon', 'tbl_data_icon', 'tbl_work_icon', 
               'tbl_user_icon_permissions', 'tbl_user_data_icon_permissions', 'tbl_user_work_icon_permissions'];
    
    foreach ($tables as $table) {
        $tableCheck = $conn->query("SHOW TABLES LIKE '$table'");
        $debug['tables'][$table] = $tableCheck->num_rows > 0 ? 'EXISTS' : 'NOT_FOUND';
    }

    // Debug: Check if user exists
    $userCheck = $conn->query("SELECT id, firstName, lastName FROM tbl_user WHERE id = '$userId'");
    if ($userCheck->num_rows > 0) {
        $user = $userCheck->fetch_assoc();
        $debug['user'] = $user;
    } else {
        $debug['user'] = 'NOT_FOUND';
    }

    $permissions = [];

    // Try to get Manage Icon Permissions with error handling
    try {
        if ($debug['tables']['tbl_manage_icon'] === 'EXISTS') {
            $manageQuery = "SELECT mi.id, mi.icon_name, mi.icon_image, mi.icon_description, uip.has_permission 
                           FROM tbl_manage_icon mi 
                           LEFT JOIN tbl_user_icon_permissions uip ON mi.id = uip.icon_id AND uip.user_id = ?
                           WHERE mi.status = 1 OR mi.status = 'Active' OR mi.status IS NULL OR mi.status = ''
                           ORDER BY mi.icon_name ASC";
            
            $manageStmt = $conn->prepare($manageQuery);
            if ($manageStmt) {
                $manageStmt->bind_param('s', $userId);
                $manageStmt->execute();
                $manageResult = $manageStmt->get_result();
                
                $manageIcons = [];
                while ($row = $manageResult->fetch_assoc()) {
                    $manageIcons[] = [
                        'id' => $row['id'],
                        'icon_name' => $row['icon_name'],
                        'icon_image' => $row['icon_image'],
                        'icon_description' => $row['icon_description'],
                        'has_permission' => $row['has_permission'] ?? 'No',
                        'type' => 'manage'
                    ];
                }
                $permissions['manage_icons'] = $manageIcons;
                $manageStmt->close();
                $debug['manage_icons_count'] = count($manageIcons);
            } else {
                $debug['manage_icons_error'] = 'Failed to prepare statement';
            }
        } else {
            $debug['manage_icons_error'] = 'tbl_manage_icon table not found';
        }
    } catch (Exception $e) {
        $debug['manage_icons_error'] = $e->getMessage();
    }

    // Try to get Data Icon Permissions with error handling
    try {
        if ($debug['tables']['tbl_data_icon'] === 'EXISTS') {
            $dataQuery = "SELECT di.id, di.icon_name, di.icon_image, di.icon_description, udip.has_permission 
                          FROM tbl_data_icon di 
                          LEFT JOIN tbl_user_data_icon_permissions udip ON di.id = udip.icon_id AND udip.user_id = ?
                          WHERE di.status = 1 OR di.status = 'Active' OR di.status IS NULL OR di.status = ''
                          ORDER BY di.icon_name ASC";
            
            $dataStmt = $conn->prepare($dataQuery);
            if ($dataStmt) {
                $dataStmt->bind_param('s', $userId);
                $dataStmt->execute();
                $dataResult = $dataStmt->get_result();
                
                $dataIcons = [];
                while ($row = $dataResult->fetch_assoc()) {
                    $dataIcons[] = [
                        'id' => $row['id'],
                        'icon_name' => $row['icon_name'],
                        'icon_image' => $row['icon_image'],
                        'icon_description' => $row['icon_description'],
                        'has_permission' => $row['has_permission'] ?? 'No',
                        'type' => 'data'
                    ];
                }
                $permissions['data_icons'] = $dataIcons;
                $dataStmt->close();
                $debug['data_icons_count'] = count($dataIcons);
            } else {
                $debug['data_icons_error'] = 'Failed to prepare statement';
            }
        } else {
            $debug['data_icons_error'] = 'tbl_data_icon table not found';
        }
    } catch (Exception $e) {
        $debug['data_icons_error'] = $e->getMessage();
    }

    // Try to get Work Icon Permissions with error handling
    try {
        if ($debug['tables']['tbl_work_icon'] === 'EXISTS') {
            $workQuery = "SELECT wi.id, wi.icon_name, wi.icon_image, wi.icon_description, uwip.has_permission 
                          FROM tbl_work_icon wi 
                          LEFT JOIN tbl_user_work_icon_permissions uwip ON wi.id = uwip.icon_id AND uwip.user_id = ?
                          WHERE wi.status = 1 OR wi.status = 'Active' OR wi.status IS NULL OR wi.status = ''
                          ORDER BY wi.icon_name ASC";
            
            $workStmt = $conn->prepare($workQuery);
            if ($workStmt) {
                $workStmt->bind_param('s', $userId);
                $workStmt->execute();
                $workResult = $workStmt->get_result();
                
                $workIcons = [];
                while ($row = $workResult->fetch_assoc()) {
                    $workIcons[] = [
                        'id' => $row['id'],
                        'icon_name' => $row['icon_name'],
                        'icon_image' => $row['icon_image'],
                        'icon_description' => $row['icon_description'],
                        'has_permission' => $row['has_permission'] ?? 'No',
                        'type' => 'work'
                    ];
                }
                $permissions['work_icons'] = $workIcons;
                $workStmt->close();
                $debug['work_icons_count'] = count($workIcons);
            } else {
                $debug['work_icons_error'] = 'Failed to prepare statement';
            }
        } else {
            $debug['work_icons_error'] = 'tbl_work_icon table not found';
        }
    } catch (Exception $e) {
        $debug['work_icons_error'] = $e->getMessage();
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Debug information for user permissions',
        'debug' => $debug,
        'data' => $permissions
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'debug' => $debug ?? []
    ]);
}

if (isset($conn)) {
    $conn->close();
}
?> 