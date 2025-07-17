<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn || $conn->connect_error) {
        throw new Exception('Database connection failed: ' . ($conn ? $conn->connect_error : 'No connection'));
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
    $permissions = [];

    // Check if user exists first
    $userCheck = $conn->query("SELECT id, firstName, lastName FROM tbl_user WHERE id = '$userId'");
    if ($userCheck && $userCheck->num_rows == 0) {
        throw new Exception('User not found with ID: ' . $userId);
    }

    // Initialize empty arrays for all permission types
    $permissions['manage_icons'] = [];
    $permissions['data_icons'] = [];
    $permissions['work_icons'] = [];

    // Get Manage Icon Permissions (only if table exists)
    try {
        $manageTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_manage_icon'");
        if ($manageTableCheck && $manageTableCheck->num_rows > 0) {
            // Check if permission table exists
            $permissionTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_icon_permissions'");
            if ($permissionTableCheck && $permissionTableCheck->num_rows > 0) {
                $manageQuery = "SELECT mi.id, mi.icon_name, mi.icon_image, mi.icon_description, 
                                       COALESCE(uip.has_permission, 'No') as has_permission 
                                FROM tbl_manage_icon mi 
                                LEFT JOIN tbl_user_icon_permissions uip ON mi.id = uip.icon_id AND uip.user_id = ?
                                WHERE mi.status = 1 OR mi.status = 'Active' OR mi.status IS NULL OR mi.status = ''
                                ORDER BY mi.icon_name ASC";
            } else {
                // If permission table doesn't exist, just get icons without permissions
                $manageQuery = "SELECT id, icon_name, icon_image, icon_description, 'No' as has_permission 
                               FROM tbl_manage_icon 
                               WHERE status = 1 OR status = 'Active' OR status IS NULL OR status = ''
                               ORDER BY icon_name ASC";
            }
            
            $manageStmt = $conn->prepare($manageQuery);
            if ($manageStmt) {
                if (strpos($manageQuery, '?') !== false) {
                    $manageStmt->bind_param('s', $userId);
                }
                $manageStmt->execute();
                $manageResult = $manageStmt->get_result();
                
                $manageIcons = [];
                while ($row = $manageResult->fetch_assoc()) {
                    $manageIcons[] = [
                        'id' => $row['id'],
                        'icon_name' => $row['icon_name'],
                        'icon_image' => $row['icon_image'],
                        'icon_description' => $row['icon_description'],
                        'has_permission' => $row['has_permission'],
                        'type' => 'manage'
                    ];
                }
                $permissions['manage_icons'] = $manageIcons;
                $manageStmt->close();
            }
        }
    } catch (Exception $e) {
        // Log error but continue with other permissions
        error_log("Error fetching manage icons: " . $e->getMessage());
    }

    // Get Data Icon Permissions (only if table exists)
    try {
        $dataTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_data_icon'");
        if ($dataTableCheck && $dataTableCheck->num_rows > 0) {
            // Check if permission table exists
            $permissionTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_data_icon_permissions'");
            if ($permissionTableCheck && $permissionTableCheck->num_rows > 0) {
                $dataQuery = "SELECT di.id, di.icon_name, di.icon_image, di.icon_description, 
                                     COALESCE(udip.has_permission, 'No') as has_permission 
                              FROM tbl_data_icon di 
                              LEFT JOIN tbl_user_data_icon_permissions udip ON di.id = udip.icon_id AND udip.user_id = ?
                              WHERE di.status = 1 OR di.status = 'Active' OR di.status IS NULL OR di.status = ''
                              ORDER BY di.icon_name ASC";
            } else {
                // If permission table doesn't exist, just get icons without permissions
                $dataQuery = "SELECT id, icon_name, icon_image, icon_description, 'No' as has_permission 
                             FROM tbl_data_icon 
                             WHERE status = 1 OR status = 'Active' OR status IS NULL OR status = ''
                             ORDER BY icon_name ASC";
            }
            
            $dataStmt = $conn->prepare($dataQuery);
            if ($dataStmt) {
                if (strpos($dataQuery, '?') !== false) {
                    $dataStmt->bind_param('s', $userId);
                }
                $dataStmt->execute();
                $dataResult = $dataStmt->get_result();
                
                $dataIcons = [];
                while ($row = $dataResult->fetch_assoc()) {
                    $dataIcons[] = [
                        'id' => $row['id'],
                        'icon_name' => $row['icon_name'],
                        'icon_image' => $row['icon_image'],
                        'icon_description' => $row['icon_description'],
                        'has_permission' => $row['has_permission'],
                        'type' => 'data'
                    ];
                }
                $permissions['data_icons'] = $dataIcons;
                $dataStmt->close();
            }
        }
    } catch (Exception $e) {
        // Log error but continue with other permissions
        error_log("Error fetching data icons: " . $e->getMessage());
    }

    // Get Work Icon Permissions (only if table exists)
    try {
        $workTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_work_icon'");
        if ($workTableCheck && $workTableCheck->num_rows > 0) {
            // Check if permission table exists
            $permissionTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user_work_icon_permissions'");
            if ($permissionTableCheck && $permissionTableCheck->num_rows > 0) {
                $workQuery = "SELECT wi.id, wi.icon_name, wi.icon_image, wi.icon_description, 
                                     COALESCE(uwip.has_permission, 'No') as has_permission 
                              FROM tbl_work_icon wi 
                              LEFT JOIN tbl_user_work_icon_permissions uwip ON wi.id = uwip.icon_id AND uwip.user_id = ?
                              WHERE wi.status = 1 OR wi.status = 'Active' OR wi.status IS NULL OR wi.status = ''
                              ORDER BY wi.icon_name ASC";
            } else {
                // If permission table doesn't exist, just get icons without permissions
                $workQuery = "SELECT id, icon_name, icon_image, icon_description, 'No' as has_permission 
                             FROM tbl_work_icon 
                             WHERE status = 1 OR status = 'Active' OR status IS NULL OR status = ''
                             ORDER BY icon_name ASC";
            }
            
            $workStmt = $conn->prepare($workQuery);
            if ($workStmt) {
                if (strpos($workQuery, '?') !== false) {
                    $workStmt->bind_param('s', $userId);
                }
                $workStmt->execute();
                $workResult = $workStmt->get_result();
                
                $workIcons = [];
                while ($row = $workResult->fetch_assoc()) {
                    $workIcons[] = [
                        'id' => $row['id'],
                        'icon_name' => $row['icon_name'],
                        'icon_image' => $row['icon_image'],
                        'icon_description' => $row['icon_description'],
                        'has_permission' => $row['has_permission'],
                        'type' => 'work'
                    ];
                }
                $permissions['work_icons'] = $workIcons;
                $workStmt->close();
            }
        }
    } catch (Exception $e) {
        // Log error but continue with other permissions
        error_log("Error fetching work icons: " . $e->getMessage());
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'User permissions fetched successfully',
        'data' => $permissions
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