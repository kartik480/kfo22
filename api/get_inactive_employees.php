<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, let's see what columns actually exist in tbl_user
    $describeQuery = "DESCRIBE tbl_user";
    $describeStmt = $pdo->prepare($describeQuery);
    $describeStmt->execute();
    $actualColumns = $describeStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get column names only
    $columnNames = array_column($actualColumns, 'Field');
    
    // Now let's try a simple query with just basic columns that should exist
    $simpleQuery = "
        SELECT 
            id,
            username,
            firstName,
            lastName,
            mobile,
            email_id,
            status,
            employee_no,
            created_at
        FROM tbl_user 
        WHERE status = '0' OR status = 0
        ORDER BY firstName, lastName
        LIMIT 5
    ";
    
    $stmt = $pdo->prepare($simpleQuery);
    $stmt->execute();
    
    $employees = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $count = count($employees);
    
    // Prepare response with debug info
    $response = [
        'success' => true,
        'message' => 'Inactive employees retrieved successfully',
        'count' => $count,
        'employees' => $employees,
        'debug_info' => [
            'actual_columns_in_table' => $columnNames,
            'total_columns_found' => count($columnNames),
            'note' => 'Using only basic columns to avoid errors. Check debug_info for actual table structure.'
        ]
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Database error
    $response = [
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'count' => 0,
        'employees' => [],
        'debug_info' => [
            'error_code' => $e->getCode(),
            'error_message' => $e->getMessage(),
            'note' => 'This will show us exactly what columns exist in your table'
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response);
    
} catch (Exception $e) {
    // General error
    $response = [
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'count' => 0,
        'employees' => [],
        'debug_info' => [
            'error_code' => $e->getCode(),
            'error_message' => $e->getMessage()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response);
}
?>