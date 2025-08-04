<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
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
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if createdBy column exists in tbl_user
    $columnCheckQuery = "SHOW COLUMNS FROM tbl_user LIKE 'createdBy'";
    $columnCheckStmt = $pdo->prepare($columnCheckQuery);
    $columnCheckStmt->execute();
    $columnExists = $columnCheckStmt->fetch();
    
    if (!$columnExists) {
        // If createdBy column doesn't exist, show table structure
        $structureQuery = "DESCRIBE tbl_user";
        $structureStmt = $pdo->prepare($structureQuery);
        $structureStmt->execute();
        $columns = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'status' => 'error',
            'message' => 'createdBy column not found in tbl_user',
            'available_columns' => array_column($columns, 'Field')
        ]);
        exit();
    }
    
    // Get all users with their createdBy information
    $query = "SELECT id, firstName, lastName, username, createdBy FROM tbl_user WHERE createdBy IS NOT NULL AND createdBy != '' ORDER BY createdBy ASC, firstName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $allUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Group users by createdBy
    $usersByCreator = [];
    foreach ($allUsers as $user) {
        $creator = $user['createdBy'];
        if (!isset($usersByCreator[$creator])) {
            $usersByCreator[$creator] = [];
        }
        $usersByCreator[$creator][] = [
            'id' => $user['id'],
            'firstName' => $user['firstName'],
            'lastName' => $user['lastName'],
            'username' => $user['username']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'users_by_creator' => $usersByCreator,
        'total_creators' => count($usersByCreator),
        'total_users_with_creator' => count($allUsers)
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 