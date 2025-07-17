<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Test if tables exist
    $tables = ['tbl_user', 'tbl_designation'];
    $tableStatus = [];
    
    foreach ($tables as $table) {
        try {
            $stmt = $conn->prepare("SELECT COUNT(*) as count FROM $table");
            $stmt->execute();
            $count = $stmt->fetchColumn();
            $tableStatus[$table] = "exists with $count records";
        } catch (Exception $e) {
            $tableStatus[$table] = "error: " . $e->getMessage();
        }
    }
    
    // Check if designation names exist
    $designationQuery = "SELECT id, designation_name FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')";
    $stmt = $conn->prepare($designationQuery);
    $stmt->execute();
    $designations = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check users with these designations
    $userQuery = "
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            u.designation_id,
            u.status,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY d.designation_name, u.firstName, u.lastName
    ";
    
    $stmt = $conn->prepare($userQuery);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Debug information for designated users',
        'database_connection' => 'successful',
        'table_status' => $tableStatus,
        'available_designations' => $designations,
        'users_found' => count($users),
        'data' => $users
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'database_connection' => 'unknown'
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 