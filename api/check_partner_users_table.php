<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Check if tbl_partner_users table exists
    $checkTableQuery = "SHOW TABLES LIKE 'tbl_partner_users'";
    $checkTableResult = $conn->query($checkTableQuery);
    
    if ($checkTableResult->num_rows == 0) {
        echo json_encode([
            'success' => false,
            'message' => 'Table tbl_partner_users does not exist',
            'table_exists' => false
        ]);
        exit;
    }
    
    // Check table structure
    $describeQuery = "DESCRIBE tbl_partner_users";
    $describeResult = $conn->query($describeQuery);
    
    $columns = [];
    $expectedColumns = [
        'id', 'username', 'alias_name', 'first_name', 'last_name', 'password',
        'Phone_number', 'email_id', 'alternative_mobile_number', 'company_name',
        'branch_state_name_id', 'branch_location_id', 'bank_id', 'account_type_id',
        'office_address', 'residential_address', 'aadhaar_number', 'pan_number',
        'account_number', 'ifsc_code', 'rank', 'status', 'reportingTo',
        'employee_no', 'department', 'designation', 'branchstate', 'branchloaction',
        'bank_name', 'account_type', 'partner_type_id', 'pan_img', 'aadhaar_img',
        'photo_img', 'bankproof_img', 'user_id', 'created_at', 'createdBy', 'updated_at'
    ];
    
    $foundColumns = [];
    while ($row = $describeResult->fetch_assoc()) {
        $columns[] = $row;
        $foundColumns[] = $row['Field'];
    }
    
    // Check which expected columns are missing
    $missingColumns = array_diff($expectedColumns, $foundColumns);
    $extraColumns = array_diff($foundColumns, $expectedColumns);
    
    // Get record count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_partner_users";
    $countResult = $conn->query($countQuery);
    $totalRecords = $countResult->fetch_assoc()['total'];
    
    echo json_encode([
        'success' => true,
        'message' => 'tbl_partner_users table exists',
        'table_exists' => true,
        'total_records' => $totalRecords,
        'table_structure' => $columns,
        'expected_columns' => $expectedColumns,
        'found_columns' => $foundColumns,
        'missing_columns' => array_values($missingColumns),
        'extra_columns' => array_values($extraColumns),
        'all_expected_columns_present' => empty($missingColumns)
    ]);
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 