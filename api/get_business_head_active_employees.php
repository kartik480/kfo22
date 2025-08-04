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
    
    // Get the logged-in Business Head user's username from GET parameter
    $loggedInUsername = trim($_GET['username'] ?? '');
    
    if (empty($loggedInUsername)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Username parameter is required to identify the logged-in Business Head user',
            'data' => []
        ]);
        exit();
    }
    
    // Log the API call for debugging
    error_log("Business Head API called with username: " . $loggedInUsername);
    
    // First, verify that the user is actually a Business Head and get their ID
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
                    AND d.designation_name = 'Business Head'
                    AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')";
    
    $managerStmt = $pdo->prepare($managerQuery);
    $managerStmt->bindParam(':username', $loggedInUsername, PDO::PARAM_STR);
    $managerStmt->execute();
    $managerResult = $managerStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$managerResult) {
        error_log("Business Head API: User not found or not a Business Head - " . $loggedInUsername);
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found or is not a Business Head user',
            'data' => []
        ]);
        exit();
    }
    
    // Log successful Business Head verification
    error_log("Business Head API: Valid Business Head found - " . $loggedInUsername . " (ID: " . $managerResult['id'] . ")");
    
    $managerUsername = $managerResult['username'];
    
    // Query to fetch all users reporting to the Business Head from tbl_bh_users
    $query = "SELECT 
                bh.id,
                bh.username,
                bh.first_name as firstName,
                bh.last_name as lastName,
                bh.Phone_number as mobile,
                bh.email_id,
                bh.alias_name,
                bh.company_name,
                bh.office_address,
                bh.residential_address,
                bh.aadhaar_number,
                bh.pan_number,
                bh.account_number,
                bh.ifsc_code,
                bh.rank,
                bh.status,
                bh.reportingTo,
                bh.data_icons,
                bh.work_icons,
                bh.created_at,
                bh.updated_at,
                CONCAT(bh.first_name, ' ', bh.last_name) as fullName
              FROM tbl_bh_users bh
              WHERE bh.reportingTo = ?
              AND (bh.status = 'Active' OR bh.status = 1 OR bh.status IS NULL OR bh.status = '')
              AND bh.first_name IS NOT NULL 
              AND bh.first_name != ''
              ORDER BY bh.first_name ASC, bh.last_name ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute([$managerUsername]);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics for the reporting users from tbl_bh_users
    $statsQuery = "SELECT 
                    COUNT(DISTINCT bh.id) as total_users,
                    COUNT(DISTINCT CASE WHEN bh.status = 'Active' OR bh.status = 1 OR bh.status IS NULL OR bh.status = '' THEN bh.id END) as active_users,
                    COUNT(DISTINCT CASE WHEN bh.rank = 'SDSA' THEN bh.id END) as sdsa_count,
                    COUNT(DISTINCT CASE WHEN bh.rank = 'Partner' THEN bh.id END) as partner_count,
                    COUNT(DISTINCT CASE WHEN bh.rank = 'Agent' THEN bh.id END) as agent_count
                FROM tbl_bh_users bh
                WHERE bh.reportingTo = :managerUsername";
    
    $statsStmt = $pdo->prepare($statsQuery);
    $statsStmt->bindParam(':managerUsername', $managerUsername, PDO::PARAM_STR);
    $statsStmt->execute();
    $statistics = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format the response from tbl_bh_users
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'],
            'firstName' => $user['firstName'],
            'lastName' => $user['lastName'],
            'fullName' => $user['fullName'],
            'mobile' => $user['mobile'],
            'email_id' => $user['email_id'],
            'alias_name' => $user['alias_name'],
            'company_name' => $user['company_name'],
            'office_address' => $user['office_address'],
            'residential_address' => $user['residential_address'],
            'aadhaar_number' => $user['aadhaar_number'],
            'pan_number' => $user['pan_number'],
            'account_number' => $user['account_number'],
            'ifsc_code' => $user['ifsc_code'],
            'rank' => $user['rank'],
            'status' => $user['status'],
            'reportingTo' => $user['reportingTo'],
            'data_icons' => $user['data_icons'],
            'work_icons' => $user['work_icons'],
            'created_at' => $user['created_at'],
            'updated_at' => $user['updated_at']
        ];
    }
    
    // Log the final results
    error_log("Business Head API: Successfully fetched " . count($formattedUsers) . " employees for Business Head " . $loggedInUsername);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Active employees reporting to Business Head fetched successfully',
        'data' => $formattedUsers,
        'total_users' => count($formattedUsers),
        'manager_username' => $managerUsername,
        'business_head_info' => [
            'id' => $managerResult['id'],
            'username' => $managerResult['username'],
            'full_name' => $managerResult['fullName'],
            'designation' => $managerResult['designation_name'],
            'department' => $managerResult['department_name'],
            'email' => $managerResult['email_id'],
            'mobile' => $managerResult['mobile'],
            'employee_no' => $managerResult['employee_no']
        ],
        'statistics' => [
            'total_users' => (int)$statistics['total_users'],
            'active_users' => (int)$statistics['active_users'],
            'sdsa_count' => (int)$statistics['sdsa_count'],
            'partner_count' => (int)$statistics['partner_count'],
            'agent_count' => (int)$statistics['agent_count']
        ],
        'api_info' => [
            'version' => '2.0',
            'timestamp' => date('Y-m-d H:i:s'),
            'requested_username' => $loggedInUsername
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