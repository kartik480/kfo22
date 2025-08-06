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
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get the logged-in user ID from request parameters
    $loggedInUserId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    $loggedInUsername = isset($_GET['username']) ? $_GET['username'] : null;
    
    if (!$loggedInUserId && !$loggedInUsername) {
        throw new Exception('User ID or username parameter is required');
    }
    
    // Get the CBO username from tbl_user table
    $userSql = "SELECT 
                    u.id,
                    u.username,
                    u.firstName,
                    u.lastName,
                    u.email_id,
                    u.mobile,
                    u.employee_no,
                    u.designation_id,
                    d.designation_name,
                    CONCAT(u.firstName, ' ', u.lastName) as fullName
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE d.designation_name = 'Chief Business Officer'
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND (";
    
    if ($loggedInUserId) {
        $userSql .= "u.id = :user_id";
        $userParams = [':user_id' => $loggedInUserId];
    } else {
        $userSql .= "u.username = :username";
        $userParams = [':username' => $loggedInUsername];
    }
    
    $userSql .= ")";
    
    $userStmt = $pdo->prepare($userSql);
    $userStmt->execute($userParams);
    $loggedInUser = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        throw new Exception('User not found or is not a Chief Business Officer');
    }
    
    $cboUsername = $loggedInUser['username']; // This will be "90000" in your case
    
    // Now fetch all partner users created by this CBO username from tbl_partner_users
    $partnerUsersSql = "SELECT 
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
                            created_at,
                            createdBy,
                            updated_at,
                            CONCAT(first_name, ' ', last_name) as full_name
                        FROM tbl_partner_users 
                        WHERE createdBy = :cbo_username
                        AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                        AND first_name IS NOT NULL 
                        AND first_name != ''
                        ORDER BY first_name ASC, last_name ASC";
    
    $partnerStmt = $pdo->prepare($partnerUsersSql);
    $partnerStmt->execute([':cbo_username' => $cboUsername]);
    $partnerUsers = $partnerStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics
    $totalUsers = count($partnerUsers);
    $activeUsers = 0;
    $inactiveUsers = 0;
    
    foreach ($partnerUsers as $user) {
        if ($user['status'] === 'Active' || $user['status'] === '1') {
            $activeUsers++;
        } else {
            $inactiveUsers++;
        }
    }
    
    // Format the response
    $formattedUsers = [];
    foreach ($partnerUsers as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'],
            'alias_name' => $user['alias_name'],
            'first_name' => $user['first_name'],
            'last_name' => $user['last_name'],
            'full_name' => $user['full_name'],
            'phone_number' => $user['Phone_number'],
            'email_id' => $user['email_id'],
            'alternative_mobile_number' => $user['alternative_mobile_number'],
            'company_name' => $user['company_name'],
            'branch_state_name_id' => $user['branch_state_name_id'],
            'branch_location_id' => $user['branch_location_id'],
            'bank_id' => $user['bank_id'],
            'account_type_id' => $user['account_type_id'],
            'office_address' => $user['office_address'],
            'residential_address' => $user['residential_address'],
            'aadhaar_number' => $user['aadhaar_number'],
            'pan_number' => $user['pan_number'],
            'account_number' => $user['account_number'],
            'ifsc_code' => $user['ifsc_code'],
            'rank' => $user['rank'],
            'status' => $user['status'],
            'reportingTo' => $user['reportingTo'],
            'employee_no' => $user['employee_no'],
            'department' => $user['department'],
            'designation' => $user['designation'],
            'branchstate' => $user['branchstate'],
            'branchloaction' => $user['branchloaction'],
            'bank_name' => $user['bank_name'],
            'account_type' => $user['account_type'],
            'partner_type_id' => $user['partner_type_id'],
            'pan_img' => $user['pan_img'],
            'aadhaar_img' => $user['aadhaar_img'],
            'photo_img' => $user['photo_img'],
            'bankproof_img' => $user['bankproof_img'],
            'created_at' => $user['created_at'],
            'createdBy' => $user['createdBy'],
            'updated_at' => $user['updated_at']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Partner users created by Chief Business Officer fetched successfully',
        'logged_in_user' => [
            'id' => $loggedInUser['id'],
            'username' => $loggedInUser['username'],
            'full_name' => $loggedInUser['fullName'],
            'designation' => $loggedInUser['designation_name'],
            'email' => $loggedInUser['email_id'],
            'mobile' => $loggedInUser['mobile'],
            'employee_no' => $loggedInUser['employee_no']
        ],
        'data' => $formattedUsers,
        'statistics' => [
            'total_users' => $totalUsers,
            'active_users' => $activeUsers,
            'inactive_users' => $inactiveUsers
        ],
        'count' => $totalUsers,
        'cbo_username' => $cboUsername
    ], JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'data' => []
    ]);
}
?> 