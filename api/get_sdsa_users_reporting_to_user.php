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
    
    // Get user_id from query parameter
    $user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;
    
    if ($user_id <= 0) {
        throw new Exception("Invalid user_id parameter. Please provide a valid user_id.");
    }
    
    // Step 1: Get the current logged-in user's information from tbl_user
    $userSql = "SELECT u.id, u.firstName, u.lastName, u.designation_id, u.status,
                        d.designation_name, d.designation_type
                 FROM tbl_user u
                 LEFT JOIN tbl_designation d ON u.designation_id = d.id
                 WHERE u.id = ?";
    
    $userStmt = $conn->prepare($userSql);
    $userStmt->bind_param("i", $user_id);
    $userStmt->execute();
    $userResult = $userStmt->get_result();
    
    if ($userResult->num_rows === 0) {
        throw new Exception("User with ID $user_id not found in tbl_user table.");
    }
    
    $currentUser = $userResult->fetch_assoc();
    $currentUserDesignation = $currentUser['designation_name'];
    $currentUserDesignationType = $currentUser['designation_type'];
    
    // Step 2: Find all SDSA users who are reporting to this user
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
                WHERE reportingTo = ?
                AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                AND first_name IS NOT NULL 
                AND first_name != ''
                ORDER BY first_name ASC, last_name ASC";
    
    $sdsaStmt = $conn->prepare($sdsaSql);
    $sdsaStmt->bind_param("i", $user_id);
    $sdsaStmt->execute();
    $sdsaResult = $sdsaStmt->get_result();
    
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
    
    // Step 3: Also check if this user is a Business Head and get their employees
    $businessHeadSql = "SELECT 
                            id, username, alias_name, first_name, last_name, password, 
                            Phone_number, email_id, alternative_mobile_number, company_name,
                            branch_state_name_id, branch_location_id, bank_id, account_type_id,
                            office_address, residential_address, aadhaar_number, pan_number,
                            account_number, ifsc_code, rank, status, reportingTo, employee_no,
                            department, designation, branchstate, branchloaction, bank_name,
                            account_type, pan_img, aadhaar_img, photo_img, bankproof_img,
                            user_id, createdBy, created_at, updated_at
                        FROM tbl_sdsa_users 
                        WHERE createdBy = ?
                        AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                        AND first_name IS NOT NULL 
                        AND first_name != ''
                        ORDER BY first_name ASC, last_name ASC";
    
    $bhStmt = $conn->prepare($businessHeadSql);
    $bhStmt->bind_param("i", $user_id);
    $bhStmt->execute();
    $bhResult = $bhStmt->get_result();
    
    $businessHeadUsers = array();
    
    if ($bhResult->num_rows > 0) {
        while($row = $bhResult->fetch_assoc()) {
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            if (!empty($fullName)) {
                $businessHeadUsers[] = array(
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
    
    // Merge both arrays and remove duplicates based on id
    $allUsers = array_merge($sdsaUsers, $businessHeadUsers);
    $uniqueUsers = array();
    $seenIds = array();
    
    foreach ($allUsers as $user) {
        if (!in_array($user['id'], $seenIds)) {
            $uniqueUsers[] = $user;
            $seenIds[] = $user['id'];
        }
    }
    
    // Sort by full name
    usort($uniqueUsers, function($a, $b) {
        return strcmp($a['full_name'], $b['full_name']);
    });
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $uniqueUsers,
        'message' => 'SDSA users reporting to user fetched successfully',
        'count' => count($uniqueUsers),
        'current_user' => array(
            'id' => $currentUser['id'],
            'name' => $currentUser['firstName'] . ' ' . $currentUser['lastName'],
            'designation' => $currentUserDesignation,
            'designation_type' => $currentUserDesignationType
        ),
        'debug' => array(
            'user_id_requested' => $user_id,
            'reporting_users_count' => count($sdsaUsers),
            'business_head_users_count' => count($businessHeadUsers),
            'total_unique_users' => count($uniqueUsers)
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_sdsa_users_reporting_to_user.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch SDSA users: ' . $e->getMessage()
    ));
}
?> 