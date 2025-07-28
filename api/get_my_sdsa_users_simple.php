<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Step 1: Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Step 2: Get the name of user with ID 8 from tbl_user
    $userQuery = "SELECT CONCAT(first_name, ' ', last_name) as full_name FROM tbl_user WHERE id = 8";
    $userStmt = $pdo->prepare($userQuery);
    $userStmt->execute();
    $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception("User with ID 8 not found in tbl_user");
    }
    
    $managerName = $userResult['full_name'];
    
    // Step 3: Query to fetch users who report to the manager (by name)
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
                pan_img,
                aadhaar_img,
                photo_img,
                bankproof_img,
                user_id,
                createdBy,
                created_at,
                updated_at
              FROM tbl_sdsa_users 
              WHERE reportingTo LIKE ?
              AND status = 'active'
              ORDER BY first_name, last_name";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute(['%' . $managerName . '%']);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'] ?? '',
            'alias_name' => $user['alias_name'] ?? '',
            'first_name' => $user['first_name'] ?? '',
            'last_name' => $user['last_name'] ?? '',
            'full_name' => trim(($user['first_name'] ?? '') . ' ' . ($user['last_name'] ?? '')),
            'password' => $user['password'] ?? '',
            'phone_number' => $user['Phone_number'] ?? '',
            'email_id' => $user['email_id'] ?? '',
            'alternative_mobile_number' => $user['alternative_mobile_number'] ?? '',
            'company_name' => $user['company_name'] ?? '',
            'branch_state_name_id' => $user['branch_state_name_id'] ?? '',
            'branch_location_id' => $user['branch_location_id'] ?? '',
            'bank_id' => $user['bank_id'] ?? '',
            'account_type_id' => $user['account_type_id'] ?? '',
            'office_address' => $user['office_address'] ?? '',
            'residential_address' => $user['residential_address'] ?? '',
            'aadhaar_number' => $user['aadhaar_number'] ?? '',
            'pan_number' => $user['pan_number'] ?? '',
            'account_number' => $user['account_number'] ?? '',
            'ifsc_code' => $user['ifsc_code'] ?? '',
            'rank' => $user['rank'] ?? '',
            'status' => $user['status'] ?? '',
            'reportingTo' => $user['reportingTo'] ?? '',
            'employee_no' => $user['employee_no'] ?? '',
            'department' => $user['department'] ?? '',
            'designation' => $user['designation'] ?? '',
            'branchstate' => $user['branchstate'] ?? '',
            'branchloaction' => $user['branchloaction'] ?? '',
            'bank_name' => $user['bank_name'] ?? '',
            'account_type' => $user['account_type'] ?? '',
            'pan_img' => $user['pan_img'] ?? '',
            'aadhaar_img' => $user['aadhaar_img'] ?? '',
            'photo_img' => $user['photo_img'] ?? '',
            'bankproof_img' => $user['bankproof_img'] ?? '',
            'user_id' => $user['user_id'] ?? '',
            'createdBy' => $user['createdBy'] ?? '',
            'created_at' => $user['created_at'] ?? '',
            'updated_at' => $user['updated_at'] ?? ''
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'SDSA users fetched successfully',
        'manager_name' => $managerName,
        'data' => $formattedUsers,
        'count' => count($formattedUsers)
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'error_type' => 'PDOException'
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'error_type' => 'Exception'
    ]);
}
?> 