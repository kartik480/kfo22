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
    error_log("Inactive SDSA Users API - Attempting database connection");
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    error_log("Inactive SDSA Users API - Database connection successful");
    
    // Query to get inactive SDSA users (status = 0) with joined data
    $query = "
        SELECT 
            u.*,
            bs.branch_state_name,
            bl.branch_location,
            b.bank_name as actual_bank_name,
            at.account_type as actual_account_type,
            CONCAT(u.first_name, ' ', u.last_name) as fullName,
            CASE 
                WHEN u.alias_name IS NOT NULL AND u.alias_name != '' 
                THEN CONCAT(u.first_name, ' ', u.last_name, ' (', u.alias_name, ')')
                ELSE CONCAT(u.first_name, ' ', u.last_name)
            END as displayName
        FROM tbl_sdsa_users u
        LEFT JOIN tbl_branch_state bs ON u.branch_state_name_id = bs.id
        LEFT JOIN tbl_branch_location bl ON u.branch_location_id = bl.id
        LEFT JOIN tbl_bank b ON u.bank_id = b.id
        LEFT JOIN tbl_account_type at ON u.account_type_id = at.id
        WHERE u.status = '0'
        ORDER BY u.first_name, u.last_name
    ";
    
    error_log("Inactive SDSA Users API - Executing query");
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $count = count($users);
    error_log("Inactive SDSA Users API - Query executed successfully. Found $count users");
    
    // Prepare response
    $response = [
        'success' => true,
        'message' => 'Inactive SDSA users retrieved successfully',
        'count' => $count,
        'users' => $users
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Database error
    error_log("Inactive SDSA Users API - Database Error: " . $e->getMessage());
    $response = [
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'count' => 0,
        'users' => []
    ];
    
    http_response_code(500);
    echo json_encode($response);
    
} catch (Exception $e) {
    // General error
    error_log("Inactive SDSA Users API - General Error: " . $e->getMessage());
    $response = [
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'count' => 0,
        'users' => []
    ];
    
    http_response_code(500);
    echo json_encode($response);
}
?> 