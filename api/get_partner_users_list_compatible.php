<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Create connection
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    // Check connection
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    // Set charset to utf8
    $conn->set_charset("utf8");
    
    // SQL query to fetch only the specified columns
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
                createdBy,
                updated_at
            FROM tbl_partner_users 
            ORDER BY id DESC";
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $partnerUsers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Convert null values to empty strings to avoid JSON parsing issues
            $partnerUser = array(
                'id' => isset($row['id']) ? $row['id'] : '',
                'username' => isset($row['username']) ? $row['username'] : '',
                'alias_name' => isset($row['alias_name']) ? $row['alias_name'] : '',
                'first_name' => isset($row['first_name']) ? $row['first_name'] : '',
                'last_name' => isset($row['last_name']) ? $row['last_name'] : '',
                'password' => isset($row['password']) ? $row['password'] : '',
                'Phone_number' => isset($row['Phone_number']) ? $row['Phone_number'] : '',
                'email_id' => isset($row['email_id']) ? $row['email_id'] : '',
                'alternative_mobile_number' => isset($row['alternative_mobile_number']) ? $row['alternative_mobile_number'] : '',
                'company_name' => isset($row['company_name']) ? $row['company_name'] : '',
                'branch_state_name_id' => isset($row['branch_state_name_id']) ? $row['branch_state_name_id'] : '',
                'branch_location_id' => isset($row['branch_location_id']) ? $row['branch_location_id'] : '',
                'bank_id' => isset($row['bank_id']) ? $row['bank_id'] : '',
                'account_type_id' => isset($row['account_type_id']) ? $row['account_type_id'] : '',
                'office_address' => isset($row['office_address']) ? $row['office_address'] : '',
                'residential_address' => isset($row['residential_address']) ? $row['residential_address'] : '',
                'aadhaar_number' => isset($row['aadhaar_number']) ? $row['aadhaar_number'] : '',
                'pan_number' => isset($row['pan_number']) ? $row['pan_number'] : '',
                'account_number' => isset($row['account_number']) ? $row['account_number'] : '',
                'ifsc_code' => isset($row['ifsc_code']) ? $row['ifsc_code'] : '',
                'rank' => isset($row['rank']) ? $row['rank'] : '',
                'status' => isset($row['status']) ? $row['status'] : '',
                'reportingTo' => isset($row['reportingTo']) ? $row['reportingTo'] : '',
                'employee_no' => isset($row['employee_no']) ? $row['employee_no'] : '',
                'department' => isset($row['department']) ? $row['department'] : '',
                'designation' => isset($row['designation']) ? $row['designation'] : '',
                'branchstate' => isset($row['branchstate']) ? $row['branchstate'] : '',
                'branchloaction' => isset($row['branchloaction']) ? $row['branchloaction'] : '',
                'bank_name' => isset($row['bank_name']) ? $row['bank_name'] : '',
                'account_type' => isset($row['account_type']) ? $row['account_type'] : '',
                'partner_type_id' => isset($row['partner_type_id']) ? $row['partner_type_id'] : '',
                'pan_img' => isset($row['pan_img']) ? $row['pan_img'] : '',
                'aadhaar_img' => isset($row['aadhaar_img']) ? $row['aadhaar_img'] : '',
                'photo_img' => isset($row['photo_img']) ? $row['photo_img'] : '',
                'bankproof_img' => isset($row['bankproof_img']) ? $row['bankproof_img'] : '',
                'user_id' => isset($row['user_id']) ? $row['user_id'] : '',
                'created_at' => isset($row['created_at']) ? $row['created_at'] : '',
                'createdBy' => isset($row['createdBy']) ? $row['createdBy'] : '',
                'updated_at' => isset($row['updated_at']) ? $row['updated_at'] : ''
            );
            
            $partnerUsers[] = $partnerUser;
        }
    }
    
    // Return success response
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Partner users fetched successfully',
        'data' => $partnerUsers,
        'count' => count($partnerUsers)
    ), JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    // Return error response
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 