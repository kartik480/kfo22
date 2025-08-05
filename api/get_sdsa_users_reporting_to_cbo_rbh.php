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
    
    // Step 1: Find designation IDs for Chief Business Officer and Regional Business Head
    $designationSql = "SELECT id, designation_name FROM tbl_designation 
                       WHERE LOWER(designation_name) LIKE '%chief%' 
                       AND LOWER(designation_name) LIKE '%business%' 
                       AND LOWER(designation_name) LIKE '%officer%'
                       OR LOWER(designation_name) LIKE '%cbo%'
                       OR (LOWER(designation_name) LIKE '%regional%' 
                           AND LOWER(designation_name) LIKE '%business%' 
                           AND LOWER(designation_name) LIKE '%head%')
                       OR LOWER(designation_name) LIKE '%rbh%'
                       ORDER BY designation_name ASC";
    
    $designationResult = $conn->query($designationSql);
    
    if ($designationResult === false) {
        throw new Exception("Designation query failed: " . $conn->error);
    }
    
    $designationIds = array();
    $foundDesignations = array();
    
    if ($designationResult->num_rows > 0) {
        while($row = $designationResult->fetch_assoc()) {
            $designationIds[] = $row['id'];
            $foundDesignations[] = array(
                'id' => $row['id'],
                'name' => $row['designation_name']
            );
        }
    }
    
    // If no designations found, try a broader search
    if (empty($designationIds)) {
        $broadDesignationSql = "SELECT id, designation_name FROM tbl_designation 
                               WHERE LOWER(designation_name) LIKE '%chief%' 
                               OR LOWER(designation_name) LIKE '%business%' 
                               OR LOWER(designation_name) LIKE '%officer%'
                               OR LOWER(designation_name) LIKE '%regional%' 
                               OR LOWER(designation_name) LIKE '%head%'
                               OR LOWER(designation_name) LIKE '%cbo%'
                               OR LOWER(designation_name) LIKE '%rbh%'
                               ORDER BY designation_name ASC";
        
        $broadResult = $conn->query($broadDesignationSql);
        if ($broadResult && $broadResult->num_rows > 0) {
            while($row = $broadResult->fetch_assoc()) {
                $designationIds[] = $row['id'];
                $foundDesignations[] = array(
                    'id' => $row['id'],
                    'name' => $row['designation_name']
                );
            }
        }
    }
    
    // If still no designations found, return empty result
    if (empty($designationIds)) {
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No Chief Business Officer or Regional Business Head designations found',
            'count' => 0,
            'debug' => array(
                'found_designations' => $foundDesignations,
                'note' => 'No CBO/RBH designations found in database'
            )
        ));
        exit;
    }
    
    // Step 2: Find all users who have these designations (CBO and RBH users)
    $designationIdList = implode(',', $designationIds);
    $userSql = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.designation_id,
                    d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE u.designation_id IN ($designationIdList)
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND u.firstName IS NOT NULL 
                AND u.firstName != ''
                AND u.firstName != 'null'
                ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    
    $userResult = $conn->query($userSql);
    
    if ($userResult === false) {
        throw new Exception("User query failed: " . $conn->error);
    }
    
    $cboRbhUserIds = array();
    $cboRbhUsers = array();
    
    if ($userResult->num_rows > 0) {
        while($row = $userResult->fetch_assoc()) {
            $cboRbhUserIds[] = $row['id'];
            $cboRbhUsers[] = array(
                'id' => $row['id'],
                'name' => $row['firstName'] . ' ' . $row['lastName'],
                'designation' => $row['designation_name']
            );
        }
    }
    
    // If no CBO/RBH users found, return empty result
    if (empty($cboRbhUserIds)) {
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No Chief Business Officer or Regional Business Head users found',
            'count' => 0,
            'debug' => array(
                'found_designations' => $foundDesignations,
                'cbo_rbh_users' => $cboRbhUsers,
                'note' => 'No users with CBO/RBH designations found'
            )
        ));
        exit;
    }
    
    // Step 3: Find all SDSA users who are reporting to these CBO/RBH users
    $cboRbhUserIdList = implode(',', $cboRbhUserIds);
    $sdsaSql = "SELECT 
                    id, username, alias_name, first_name, last_name, password, 
                    Phone_number, email_id, alternative_mobile_number, company_name,
                    branch_state_name_id, branch_location_id, bank_id, account_type_id,
                    office_address, residential_address, aadhaar_number, pan_number,
                    account_number, ifsc_code, rank, status, reportingTo, employee_no,
                    department, designation, branchstate, branchloaction, bank_name,
                    account_type, pan_img, aadhaar_img, photo_img, bankproof_img,
                    user_id, createdBy, created_at, updated_at
                FROM tbl_sdsa_users 
                WHERE reportingTo IN ($cboRbhUserIdList)
                AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                AND first_name IS NOT NULL 
                AND first_name != ''
                ORDER BY first_name ASC, last_name ASC";
    
    $sdsaResult = $conn->query($sdsaSql);
    
    if ($sdsaResult === false) {
        throw new Exception("SDSA query failed: " . $conn->error);
    }
    
    $sdsaUsers = array();
    
    if ($sdsaResult->num_rows > 0) {
        while($row = $sdsaResult->fetch_assoc()) {
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            if (!empty($fullName)) {
                $sdsaUsers[] = array(
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
                    'user_id' => $row['user_id'],
                    'createdBy' => $row['createdBy'],
                    'created_at' => $row['created_at'],
                    'updated_at' => $row['updated_at']
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $sdsaUsers,
        'message' => 'SDSA users reporting to Chief Business Officer and Regional Business Head fetched successfully',
        'count' => count($sdsaUsers),
        'debug' => array(
            'found_designations' => $foundDesignations,
            'cbo_rbh_users' => $cboRbhUsers,
            'cbo_rbh_user_ids' => $cboRbhUserIds,
            'sdsa_sql' => $sdsaSql,
            'note' => 'SDSA users reporting to CBO and RBH designated users'
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_sdsa_users_reporting_to_cbo_rbh.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch SDSA users: ' . $e->getMessage(),
        'debug' => array(
            'error_details' => $e->getMessage(),
            'file' => __FILE__,
            'line' => __LINE__
        )
    ));
}
?> 