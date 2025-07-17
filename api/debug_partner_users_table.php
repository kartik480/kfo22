<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    
    if (!isset($conn)) {
        throw new Exception('Database connection variable $conn is not set');
    }
    
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }
    
    // Check if tbl_partner_users table exists
    $sql = "SHOW TABLES LIKE 'tbl_partner_users'";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("SHOW TABLES query failed: " . $conn->error);
    }
    
    $tableExists = $result->num_rows > 0;
    
    if (!$tableExists) {
        // Check what tables exist that might be similar
        $sql = "SHOW TABLES LIKE '%partner%'";
        $result = $conn->query($sql);
        $similarTables = [];
        if ($result) {
            while ($row = $result->fetch_array()) {
                $similarTables[] = $row[0];
            }
        }
        
        echo json_encode([
            'status' => 'error',
            'message' => 'tbl_partner_users table does not exist',
            'debug_info' => [
                'table_exists' => false,
                'similar_tables' => $similarTables,
                'all_tables_query' => 'SHOW TABLES LIKE "%partner%"'
            ]
        ]);
        exit;
    }
    
    // Get table structure
    $sql = "DESCRIBE tbl_partner_users";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("DESCRIBE query failed: " . $conn->error);
    }
    
    $columns = [];
    while ($row = $result->fetch_assoc()) {
        $columns[] = $row;
    }
    
    // Check if required columns exist
    $requiredColumns = [
        'id', 'username', 'alias_name', 'first_name', 'last_name', 'password',
        'Phone_number', 'email_id', 'alternative_mobile_number', 'company_name',
        'branch_state_name_id', 'branch_location_id', 'bank_id', 'account_type_id',
        'office_address', 'residential_address', 'aadhaar_number', 'pan_number',
        'account_number', 'ifsc_code', 'rank', 'status', 'reportingTo',
        'employee_no', 'department', 'designation', 'branchstate', 'branchloaction',
        'bank_name', 'account_type', 'partner_type_id', 'pan_img', 'aadhaar_img',
        'photo_img', 'bankproof_img', 'user_id', 'created_at', 'createdBy'
    ];
    
    $existingColumns = array_column($columns, 'Field');
    $missingColumns = array_diff($requiredColumns, $existingColumns);
    $extraColumns = array_diff($existingColumns, $requiredColumns);
    
    // Try a simple SELECT query
    $sql = "SELECT COUNT(*) as count FROM tbl_partner_users";
    $result = $conn->query($sql);
    
    $rowCount = 0;
    if ($result) {
        $row = $result->fetch_assoc();
        $rowCount = $row['count'];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Table structure analysis complete',
        'debug_info' => [
            'table_exists' => true,
            'total_columns' => count($columns),
            'row_count' => $rowCount,
            'missing_columns' => array_values($missingColumns),
            'extra_columns' => array_values($extraColumns),
            'all_columns' => $existingColumns,
            'column_details' => $columns
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => get_class($e),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ]);
}
?> 