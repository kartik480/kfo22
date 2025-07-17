<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
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
        // Try alternative table names
        $alternativeTables = ['tbl_partners', 'partner_users', 'partners'];
        $tableFound = false;
        $actualTableName = '';
        
        foreach ($alternativeTables as $tableName) {
            $checkAltQuery = "SHOW TABLES LIKE '$tableName'";
            $checkAltResult = $conn->query($checkAltQuery);
            if ($checkAltResult->num_rows > 0) {
                $tableFound = true;
                $actualTableName = $tableName;
                break;
            }
        }
        
        if (!$tableFound) {
            echo json_encode([
                'status' => 'error',
                'message' => 'No partner users table found. Checked: tbl_partner_users, tbl_partners, partner_users, partners',
                'data' => [],
                'count' => 0
            ]);
            exit;
        }
    } else {
        $actualTableName = 'tbl_partner_users';
    }
    
    // Get table structure to determine available columns
    $describeQuery = "DESCRIBE $actualTableName";
    $describeResult = $conn->query($describeQuery);
    
    $availableColumns = [];
    while ($row = $describeResult->fetch_assoc()) {
        $availableColumns[] = $row['Field'];
    }
    
    // Define expected columns and their fallbacks
    $expectedColumns = [
        'id' => 'id',
        'username' => 'username',
        'alias_name' => 'alias_name',
        'first_name' => 'first_name',
        'last_name' => 'last_name',
        'password' => 'password',
        'Phone_number' => 'Phone_number',
        'email_id' => 'email_id',
        'alternative_mobile_number' => 'alternative_mobile_number',
        'company_name' => 'company_name',
        'branch_state_name_id' => 'branch_state_name_id',
        'branch_location_id' => 'branch_location_id',
        'bank_id' => 'bank_id',
        'account_type_id' => 'account_type_id',
        'office_address' => 'office_address',
        'residential_address' => 'residential_address',
        'aadhaar_number' => 'aadhaar_number',
        'pan_number' => 'pan_number',
        'account_number' => 'account_number',
        'ifsc_code' => 'ifsc_code',
        'rank' => 'rank',
        'status' => 'status',
        'reportingTo' => 'reportingTo',
        'employee_no' => 'employee_no',
        'department' => 'department',
        'designation' => 'designation',
        'branchstate' => 'branchstate',
        'branchloaction' => 'branchloaction',
        'bank_name' => 'bank_name',
        'account_type' => 'account_type',
        'partner_type_id' => 'partner_type_id',
        'pan_img' => 'pan_img',
        'aadhaar_img' => 'aadhaar_img',
        'photo_img' => 'photo_img',
        'bankproof_img' => 'bankproof_img',
        'user_id' => 'user_id',
        'created_at' => 'created_at',
        'createdBy' => 'createdBy',
        'updated_at' => 'updated_at'
    ];
    
    // Build SELECT query with only available columns
    $selectColumns = [];
    foreach ($expectedColumns as $expectedCol => $fallbackCol) {
        if (in_array($expectedCol, $availableColumns)) {
            $selectColumns[] = "$expectedCol as $expectedCol";
        } elseif (in_array($fallbackCol, $availableColumns)) {
            $selectColumns[] = "$fallbackCol as $expectedCol";
        } else {
            $selectColumns[] = "'' as $expectedCol";
        }
    }
    
    $query = "SELECT " . implode(', ', $selectColumns) . " FROM $actualTableName ORDER BY created_at DESC";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $partnerUsers = array();
    while ($row = $result->fetch_assoc()) {
        $partnerUsers[] = array(
            'id' => $row['id'] ?? '',
            'username' => $row['username'] ?? '',
            'alias_name' => $row['alias_name'] ?? '',
            'first_name' => $row['first_name'] ?? '',
            'last_name' => $row['last_name'] ?? '',
            'password' => $row['password'] ?? '',
            'Phone_number' => $row['Phone_number'] ?? '',
            'email_id' => $row['email_id'] ?? '',
            'alternative_mobile_number' => $row['alternative_mobile_number'] ?? '',
            'company_name' => $row['company_name'] ?? '',
            'branch_state_name_id' => $row['branch_state_name_id'] ?? '',
            'branch_location_id' => $row['branch_location_id'] ?? '',
            'bank_id' => $row['bank_id'] ?? '',
            'account_type_id' => $row['account_type_id'] ?? '',
            'office_address' => $row['office_address'] ?? '',
            'residential_address' => $row['residential_address'] ?? '',
            'aadhaar_number' => $row['aadhaar_number'] ?? '',
            'pan_number' => $row['pan_number'] ?? '',
            'account_number' => $row['account_number'] ?? '',
            'ifsc_code' => $row['ifsc_code'] ?? '',
            'rank' => $row['rank'] ?? '',
            'status' => $row['status'] ?? '',
            'reportingTo' => $row['reportingTo'] ?? '',
            'employee_no' => $row['employee_no'] ?? '',
            'department' => $row['department'] ?? '',
            'designation' => $row['designation'] ?? '',
            'branchstate' => $row['branchstate'] ?? '',
            'branchloaction' => $row['branchloaction'] ?? '',
            'bank_name' => $row['bank_name'] ?? '',
            'account_type' => $row['account_type'] ?? '',
            'partner_type_id' => $row['partner_type_id'] ?? '',
            'pan_img' => $row['pan_img'] ?? '',
            'aadhaar_img' => $row['aadhaar_img'] ?? '',
            'photo_img' => $row['photo_img'] ?? '',
            'bankproof_img' => $row['bankproof_img'] ?? '',
            'user_id' => $row['user_id'] ?? '',
            'created_at' => $row['created_at'] ?? '',
            'createdBy' => $row['createdBy'] ?? '',
            'updated_at' => $row['updated_at'] ?? ''
        );
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => "Partner users fetched successfully from $actualTableName",
        'data' => $partnerUsers,
        'count' => count($partnerUsers),
        'table_used' => $actualTableName,
        'available_columns' => $availableColumns
    ]);
    
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ]);
}

$conn->close();
?> 