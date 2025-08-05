<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // Get all partner users from tbl_partner_users - focusing on reportingTo column
    $sql = "SELECT 
                id, username, alias_name, first_name, last_name, password,
                Phone_number, email_id, alternative_mobile_number, company_name,
                branch_state_name_id, branch_location_id, bank_id, account_type_id,
                office_address, residential_address, aadhaar_number, pan_number,
                account_number, ifsc_code, rank, status, reportingTo, employee_no,
                department, designation, branchstate, branchloaction, bank_name,
                account_type, pan_img, aadhaar_img, photo_img, bankproof_img,
                createdBy, created_at, updated_at
            FROM tbl_partner_users
            WHERE (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
            AND first_name IS NOT NULL
            AND first_name != ''
            ORDER BY first_name ASC, last_name ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $partnerUsers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            if (!empty($fullName)) {
                $partnerUsers[] = array(
                    'id' => $row['id'],
                    'username' => $row['username'],
                    'alias_name' => $row['alias_name'],
                    'first_name' => $row['first_name'],
                    'last_name' => $row['last_name'],
                    'full_name' => $fullName,
                    'password' => $row['password'],
                    'phone_number' => $row['Phone_number'],
                    'email_id' => $row['email_id'],
                    'alternative_mobile_number' => $row['alternative_mobile_number'],
                    'company_name' => $row['company_name'],
                    'branch_state_name_id' => $row['branch_state_name_id'],
                    'branch_location_id' => $row['branch_location_id'],
                    'bank_id' => $row['bank_id'],
                    'account_type_id' => $row['account_type_id'],
                    'office_address' => $row['office_address'],
                    'residential_address' => $row['residential_address'],
                    'aadhaar_number' => $row['aadhaar_number'],
                    'pan_number' => $row['pan_number'],
                    'account_number' => $row['account_number'],
                    'ifsc_code' => $row['ifsc_code'],
                    'rank' => $row['rank'],
                    'status' => $row['status'],
                    'reportingTo' => $row['reportingTo'],
                    'employee_no' => $row['employee_no'],
                    'department' => $row['department'],
                    'designation' => $row['designation'],
                    'branchstate' => $row['branchstate'],
                    'branchloaction' => $row['branchloaction'],
                    'bank_name' => $row['bank_name'],
                    'account_type' => $row['account_type'],
                    'pan_img' => $row['pan_img'],
                    'aadhaar_img' => $row['aadhaar_img'],
                    'photo_img' => $row['photo_img'],
                    'bankproof_img' => $row['bankproof_img'],
                    'createdBy' => $row['createdBy'],
                    'created_at' => $row['created_at'],
                    'updated_at' => $row['updated_at']
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $partnerUsers,
        'message' => 'Partner users fetched successfully',
        'count' => count($partnerUsers),
        'debug' => array(
            'sql_used' => $sql,
            'note' => 'All partner users from tbl_partner_users table'
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_partner_users_reporting_to.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch partner users: ' . $e->getMessage(),
        'debug' => array(
            'error_details' => $e->getMessage(),
            'file' => __FILE__,
            'line' => __LINE__
        )
    ));
}
?> 