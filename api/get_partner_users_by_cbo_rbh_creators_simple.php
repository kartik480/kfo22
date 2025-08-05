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
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, let's test if we can connect and get basic data
    $testSql = "SELECT COUNT(*) as total FROM tbl_partner_users";
    $testStmt = $pdo->prepare($testSql);
    $testStmt->execute();
    $testResult = $testStmt->fetch(PDO::FETCH_ASSOC);
    
    // Now let's check if the tables exist and have the right structure
    $checkTablesSql = "SHOW TABLES LIKE 'tbl_user'";
    $checkTablesStmt = $pdo->prepare($checkTablesSql);
    $checkTablesStmt->execute();
    $userTableExists = $checkTablesStmt->rowCount() > 0;
    
    $checkDesignationSql = "SHOW TABLES LIKE 'tbl_designation'";
    $checkDesignationStmt = $pdo->prepare($checkDesignationSql);
    $checkDesignationStmt->execute();
    $designationTableExists = $checkDesignationStmt->rowCount() > 0;
    
    // Safe query with only fields we know exist
    $sql = "SELECT 
                pu.id, pu.username, pu.first_name, pu.last_name, pu.email_id, pu.Phone_number,
                pu.company_name, pu.department, pu.designation, pu.status, pu.createdBy, pu.created_at,
                pu.employee_no, pu.reportingTo, pu.rank, pu.partner_type_id,
                creator.username as creator_username,
                creator.firstName as creator_first_name,
                creator.lastName as creator_last_name
            FROM tbl_partner_users pu
            LEFT JOIN tbl_user creator ON pu.createdBy = creator.id
            WHERE (pu.status = 'Active' OR pu.status = 1 OR pu.status IS NULL OR pu.status = '')
            AND pu.first_name IS NOT NULL 
            AND pu.first_name != ''
            ORDER BY pu.first_name ASC, pu.last_name ASC
            LIMIT 20";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'Simple partner users query executed successfully',
        'debug_info' => [
            'total_partner_users' => $testResult['total'],
            'user_table_exists' => $userTableExists,
            'designation_table_exists' => $designationTableExists,
            'results_count' => count($results)
        ],
        'data' => $results
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'sql_state' => $e->getCode()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'General error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 