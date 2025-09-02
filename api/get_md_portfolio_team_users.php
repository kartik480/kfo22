<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create connection using PDO
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if required tables exist
    $tables = ['tbl_user', 'tbl_designation'];
    foreach ($tables as $table) {
        $checkTable = $conn->prepare("SHOW TABLES LIKE '$table'");
        $checkTable->execute();
        $tableExists = $checkTable->fetch();
        
        if (!$tableExists) {
            echo json_encode([
                'success' => false,
                'message' => "$table table does not exist",
                'users' => []
            ]);
            exit;
        }
    }
    
    // Get the designation IDs for the required designations
    $designationSql = "SELECT id, designation_name FROM tbl_designation 
                       WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')";
    $designationStmt = $conn->prepare($designationSql);
    $designationStmt->execute();
    $designations = $designationStmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($designations)) {
        echo json_encode([
            'success' => false,
            'message' => 'Required designations not found',
            'users' => []
        ]);
        exit;
    }
    
    // Extract designation IDs
    $designationIds = array_column($designations, 'id');
    $designationIdsStr = implode(',', $designationIds);
    
    // Get users with the required designations
    $sql = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.designation_id,
                d.designation_name,
                CONCAT(u.firstName, ' ', u.lastName) as full_name,
                u.email_id,
                u.mobile,
                u.status
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.designation_id IN ($designationIdsStr)
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Group users by designation
    $usersByDesignation = [];
    foreach ($users as $user) {
        $designation = $user['designation_name'];
        if (!isset($usersByDesignation[$designation])) {
            $usersByDesignation[$designation] = [];
        }
        $usersByDesignation[$designation][] = $user;
    }
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Users fetched successfully',
        'data' => [
            'designation_info' => $designations,
            'users' => $users,
            'users_by_designation' => $usersByDesignation,
            'statistics' => [
                'total_users' => count($users),
                'designation_counts' => array_map('count', $usersByDesignation)
            ]
        ],
        'count' => count($users)
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'users' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'users' => []
    ]);
}
?>
