<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    // Include database configuration
    require_once 'db_config.php';
    
    // Check if database connection exists
    if (!isset($conn)) {
        throw new Exception('Database connection variable $conn is not set');
    }
    
    // Check database connection
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }
    
    // Check if tbl_partner_users table exists
    $checkTableSql = "SHOW TABLES LIKE 'tbl_partner_users'";
    $tableResult = $conn->query($checkTableSql);
    
    if ($tableResult === false) {
        throw new Exception("Table check query failed: " . $conn->error);
    }
    
    if ($tableResult->num_rows == 0) {
        // Table doesn't exist, return empty list
        echo json_encode([
            'status' => 'success',
            'data' => [],
            'message' => 'tbl_partner_users table not found',
            'count' => 0
        ]);
        exit;
    }
    
    // Fetch all partner users data with all specified columns
    $sql = "SELECT 
                id,
                username,
                alias_name,
                first_name,
                last_name,
                password,
                Phone_number,
                email_id,
                alternative_mobile_number,
                company_name,
                branch_state_name_id,
                branch_location_id,
                bank_id,
                account_type_id,
                office_address,
                residential_address,
                aadhaar_number,
                pan_number,
                account_number,
                ifsc_code,
                rank,
                status,
                reportingTo,
                employee_no,
                department,
                designation,
                branchstate,
                branchloaction,
                bank_name,
                account_type,
                partner_type_id,
                pan_img,
                aadhaar_img,
                photo_img,
                bankproof_img,
                user_id,
                created_at,
                createdBy
            FROM tbl_partner_users
            ORDER BY created_at DESC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("SELECT query failed: " . $conn->error);
    }
    
    $partnerUsers = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine first name and last name for display
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            if (!empty($row['alias_name'])) {
                $fullName .= ' (' . $row['alias_name'] . ')';
            }
            
            $partnerUsers[] = array(
                'id' => $row['id'],
                'username' => $row['username'] ?? 'N/A',
                'alias_name' => $row['alias_name'] ?? 'N/A',
                'first_name' => $row['first_name'] ?? 'N/A',
                'last_name' => $row['last_name'] ?? 'N/A',
                'full_name' => $fullName,
                'password' => $row['password'] ?? 'N/A',
                'phone_number' => $row['Phone_number'] ?? 'N/A',
                'email_id' => $row['email_id'] ?? 'N/A',
                'alternative_mobile_number' => $row['alternative_mobile_number'] ?? 'N/A',
                'company_name' => $row['company_name'] ?? 'N/A',
                'branch_state_name_id' => $row['branch_state_name_id'] ?? 'N/A',
                'branch_location_id' => $row['branch_location_id'] ?? 'N/A',
                'bank_id' => $row['bank_id'] ?? 'N/A',
                'account_type_id' => $row['account_type_id'] ?? 'N/A',
                'office_address' => $row['office_address'] ?? 'N/A',
                'residential_address' => $row['residential_address'] ?? 'N/A',
                'aadhaar_number' => $row['aadhaar_number'] ?? 'N/A',
                'pan_number' => $row['pan_number'] ?? 'N/A',
                'account_number' => $row['account_number'] ?? 'N/A',
                'ifsc_code' => $row['ifsc_code'] ?? 'N/A',
                'rank' => $row['rank'] ?? 'N/A',
                'status' => $row['status'] ?? 'Active',
                'reportingTo' => $row['reportingTo'] ?? 'N/A',
                'employee_no' => $row['employee_no'] ?? 'N/A',
                'department' => $row['department'] ?? 'N/A',
                'designation' => $row['designation'] ?? 'N/A',
                'branchstate' => $row['branchstate'] ?? 'N/A',
                'branchloaction' => $row['branchloaction'] ?? 'N/A',
                'bank_name' => $row['bank_name'] ?? 'N/A',
                'account_type' => $row['account_type'] ?? 'N/A',
                'partner_type_id' => $row['partner_type_id'] ?? 'N/A',
                'pan_img' => $row['pan_img'] ?? 'N/A',
                'aadhaar_img' => $row['aadhaar_img'] ?? 'N/A',
                'photo_img' => $row['photo_img'] ?? 'N/A',
                'bankproof_img' => $row['bankproof_img'] ?? 'N/A',
                'user_id' => $row['user_id'] ?? 'N/A',
                'created_at' => $row['created_at'] ?? date('Y-m-d H:i:s'),
                'createdBy' => $row['createdBy'] ?? 'N/A'
            );
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $partnerUsers,
        'message' => 'Partner users fetched successfully',
        'count' => count($partnerUsers)
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ]);
}
?> 