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
    
    // Check if tbl_user table exists
    $checkUserTable = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
    $checkUserTable->execute();
    $userTableExists = $checkUserTable->fetch();
    
    if (!$userTableExists) {
        echo json_encode([
            'success' => false,
            'message' => 'tbl_user table does not exist',
            'users' => []
        ]);
        exit;
    }
    
    // Check if tbl_designation table exists
    $checkDesignationTable = $conn->prepare("SHOW TABLES LIKE 'tbl_designation'");
    $checkDesignationTable->execute();
    $designationTableExists = $checkDesignationTable->fetch();
    
    if (!$designationTableExists) {
        echo json_encode([
            'success' => false,
            'message' => 'tbl_designation table does not exist',
            'users' => []
        ]);
        exit;
    }
    
    // First, get the designation ID for "Regional Business Head"
    $designationSql = "SELECT id FROM tbl_designation WHERE designation_name = 'Regional Business Head'";
    $designationStmt = $conn->prepare($designationSql);
    $designationStmt->execute();
    $designation = $designationStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$designation) {
        echo json_encode([
            'success' => false,
            'message' => 'Regional Business Head designation not found',
            'users' => []
        ]);
        exit;
    }
    
    $rbhDesignationId = $designation['id'];
    
    // Fetch all Regional Business Head users
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
            WHERE u.designation_id = :designation_id
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            ORDER BY u.firstName ASC, u.lastName ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':designation_id', $rbhDesignationId);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total FROM tbl_user WHERE designation_id = :designation_id AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')";
    $countStmt = $conn->prepare($countSql);
    $countStmt->bindParam(':designation_id', $rbhDesignationId);
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Regional Business Head users fetched successfully',
        'data' => [
            'designation_info' => [
                'id' => $rbhDesignationId,
                'name' => 'Regional Business Head'
            ],
            'users' => $users,
            'statistics' => [
                'total_rbh_users' => $totalCount
            ]
        ],
        'count' => $totalCount
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