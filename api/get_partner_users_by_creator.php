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
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get the logged-in Managing Director user's username from GET parameter
    $loggedInUsername = trim($_GET['username'] ?? '');
    $selectedCreatorId = trim($_GET['creator_id'] ?? '');
    
    if (empty($loggedInUsername)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Username parameter is required to identify the logged-in Managing Director user',
            'data' => []
        ]);
        exit();
    }
    
    if (empty($selectedCreatorId)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Creator ID parameter is required to filter partner users',
            'data' => []
        ]);
        exit();
    }
    
    // Log the API call for debugging
    error_log("Partner Users by Creator API called with username: " . $loggedInUsername . " and creator_id: " . $selectedCreatorId);
    
    // First, verify that the user is actually a Managing Director
    $managerQuery = "SELECT 
                        u.id,
                        u.username,
                        u.firstName,
                        u.lastName,
                        u.email_id,
                        u.mobile,
                        u.employee_no,
                        u.designation_id,
                        u.department_id,
                        u.reportingTo,
                        u.status,
                        d.designation_name,
                        dept.department_name,
                        CONCAT(u.firstName, ' ', u.lastName) as fullName
                    FROM tbl_user u
                    INNER JOIN tbl_designation d ON u.designation_id = d.id
                    LEFT JOIN tbl_department dept ON u.department_id = dept.id
                    WHERE u.username = :username 
                    AND d.designation_name = 'Managing Director'
                    AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')";
    
    $managerStmt = $pdo->prepare($managerQuery);
    $managerStmt->bindParam(':username', $loggedInUsername, PDO::PARAM_STR);
    $managerStmt->execute();
    $managerResult = $managerStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$managerResult) {
        error_log("Managing Director API: User not found or not a Managing Director - " . $loggedInUsername);
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found or is not a Managing Director user',
            'data' => []
        ]);
        exit();
    }
    
    // Log successful Managing Director verification
    error_log("Managing Director API: Valid Managing Director found - " . $loggedInUsername . " (ID: " . $managerResult['id'] . ")");
    
    // Get the creator's information
    $creatorQuery = "SELECT 
                        u.id,
                        u.username,
                        u.firstName,
                        u.lastName,
                        d.designation_name,
                        dept.department_name,
                        CONCAT(u.firstName, ' ', u.lastName) as fullName
                    FROM tbl_user u
                    INNER JOIN tbl_designation d ON u.designation_id = d.id
                    LEFT JOIN tbl_department dept ON u.department_id = dept.id
                    WHERE u.id = :creator_id";
    
    $creatorStmt = $pdo->prepare($creatorQuery);
    $creatorStmt->bindParam(':creator_id', $selectedCreatorId, PDO::PARAM_STR);
    $creatorStmt->execute();
    $creatorResult = $creatorStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$creatorResult) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Selected creator not found',
            'data' => []
        ]);
        exit();
    }
    
    // Query to fetch partner users created by the selected creator
    $query = "SELECT 
                id,
                username,
                alias_name,
                first_name,
                last_name,
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
                updated_at,
                CONCAT(first_name, ' ', last_name) as fullName
              FROM tbl_partner_users 
              WHERE createdBy = :creator_id
              AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
              AND first_name IS NOT NULL 
              AND first_name != ''
              ORDER BY first_name ASC, last_name ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':creator_id', $selectedCreatorId, PDO::PARAM_STR);
    $stmt->execute();
    $partnerUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics for partner users created by the selected creator
    $statsQuery = "SELECT 
                    COUNT(DISTINCT id) as total_users,
                    COUNT(DISTINCT CASE WHEN status = 'Active' OR status = 1 OR status IS NULL OR status = '' THEN id END) as active_users,
                    COUNT(DISTINCT CASE WHEN rank = 'SDSA' THEN id END) as sdsa_count,
                    COUNT(DISTINCT CASE WHEN rank = 'Partner' THEN id END) as partner_count,
                    COUNT(DISTINCT CASE WHEN rank = 'Agent' THEN id END) as agent_count
                FROM tbl_partner_users 
                WHERE createdBy = :creator_id";
    
    $statsStmt = $pdo->prepare($statsQuery);
    $statsStmt->bindParam(':creator_id', $selectedCreatorId, PDO::PARAM_STR);
    $statsStmt->execute();
    $statistics = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format the response
    $formattedUsers = [];
    foreach ($partnerUsers as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'],
            'alias_name' => $user['alias_name'],
            'first_name' => $user['first_name'],
            'last_name' => $user['last_name'],
            'fullName' => $user['fullName'],
            'Phone_number' => $user['Phone_number'],
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
            'user_id' => $user['user_id'],
            'created_at' => $user['created_at'],
            'createdBy' => $user['createdBy'],
            'updated_at' => $user['updated_at']
        ];
    }
    
    // Log the final results
    error_log("Partner Users by Creator API: Successfully fetched " . count($formattedUsers) . " partner users created by " . $creatorResult['fullName'] . " for Managing Director " . $loggedInUsername);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Partner users created by selected user fetched successfully',
        'data' => $formattedUsers,
        'total_users' => count($formattedUsers),
        'selected_creator' => [
            'id' => $creatorResult['id'],
            'username' => $creatorResult['username'],
            'full_name' => $creatorResult['fullName'],
            'designation' => $creatorResult['designation_name'],
            'department' => $creatorResult['department_name']
        ],
        'statistics' => [
            'total_users' => (int)$statistics['total_users'],
            'active_users' => (int)$statistics['active_users'],
            'sdsa_count' => (int)$statistics['sdsa_count'],
            'partner_count' => (int)$statistics['partner_count'],
            'agent_count' => (int)$statistics['agent_count']
        ],
        'managing_director_info' => [
            'id' => $managerResult['id'],
            'username' => $managerResult['username'],
            'full_name' => $managerResult['fullName'],
            'designation' => $managerResult['designation_name'],
            'department' => $managerResult['department_name']
        ],
        'api_info' => [
            'version' => '1.0',
            'timestamp' => date('Y-m-d H:i:s'),
            'requested_username' => $loggedInUsername,
            'selected_creator_id' => $selectedCreatorId
        ]
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 