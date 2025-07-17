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
        echo json_encode([
            'status' => 'error',
            'message' => 'Table tbl_partner_users does not exist',
            'data' => [],
            'count' => 0
        ]);
        exit;
    }
    
    // Fetch all partner users with all specified columns
    $query = "SELECT 
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
                createdBy,
                updated_at
              FROM tbl_partner_users
              ORDER BY created_at DESC";
    
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
        'message' => 'Partner users fetched successfully',
        'data' => $partnerUsers,
        'count' => count($partnerUsers)
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