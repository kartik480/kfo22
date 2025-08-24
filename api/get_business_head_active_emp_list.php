<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $input = file_get_contents('php://input');
    $postData = $_POST;
    $getData = $_GET;

    $loggedInUser = null;
    if (!empty($postData)) {
        if (isset($postData['user_id']) && !empty($postData['user_id'])) {
            $loggedInUser = ['id' => $postData['user_id']];
        } elseif (isset($postData['username']) && !empty($postData['username'])) {
            $loggedInUser = ['username' => $postData['username']];
        }
    }
    if (!$loggedInUser && !empty($getData)) {
        if (isset($getData['user_id']) && !empty($getData['user_id'])) {
            $loggedInUser = ['id' => $getData['user_id']];
        } elseif (isset($getData['username']) && !empty($getData['username'])) {
            $loggedInUser = ['username' => $getData['username']];
        }
    }
    if (!$loggedInUser && !empty($input)) {
        $jsonInput = json_decode($input, true);
        if ($jsonInput && json_last_error() === JSON_ERROR_NONE) {
            if (isset($jsonInput['user_id']) && !empty($jsonInput['user_id'])) {
                $loggedInUser = ['id' => $jsonInput['user_id']];
            } elseif (isset($jsonInput['username']) && !empty($jsonInput['username'])) {
                $loggedInUser = ['username' => $jsonInput['username']];
            }
        }
    }

    if (!$loggedInUser) {
        throw new Exception("No user information provided. Please send user_id or username.");
    }

    // First, get the username of the logged-in user
    $usernameQuery = "";
    $usernameParams = [];
    if (isset($loggedInUser['id'])) {
        $usernameQuery = "SELECT username FROM tbl_user WHERE id = ?";
        $usernameParams = [$loggedInUser['id']];
    } else {
        $usernameQuery = "SELECT username FROM tbl_user WHERE username = ?";
        $usernameParams = [$loggedInUser['username']];
    }

    $usernameStmt = $pdo->prepare($usernameQuery);
    $usernameStmt->execute($usernameParams);
    $usernameResult = $usernameStmt->fetch(PDO::FETCH_ASSOC);

    if (!$usernameResult) {
        throw new Exception("User not found in tbl_user");
    }

    $loggedInUsername = $usernameResult['username'];

    // Get manager details
    $managerQuery = "SELECT id, username, firstName, lastName, email_id, mobile, status 
                     FROM tbl_user 
                     WHERE username = ?";
    $managerStmt = $pdo->prepare($managerQuery);
    $managerStmt->execute([$loggedInUsername]);
    $managerDetails = $managerStmt->fetch(PDO::FETCH_ASSOC);

    // Check what tables exist and their structure
    $tableCheckQuery = "SHOW TABLES LIKE 'tbl_bh_users'";
    $tableCheckStmt = $pdo->prepare($tableCheckQuery);
    $tableCheckStmt->execute();
    $bhTableExists = $tableCheckStmt->fetch(PDO::FETCH_ASSOC);

    if ($bhTableExists) {
        // Get the actual column names from tbl_bh_users
        $columnsQuery = "SHOW COLUMNS FROM tbl_bh_users";
        $columnsStmt = $pdo->prepare($columnsQuery);
        $columnsStmt->execute();
        $columns = $columnsStmt->fetchAll(PDO::FETCH_COLUMN);
        
        // Build dynamic query based on available columns
        $selectColumns = [];
        
        // Map expected columns to available columns with better fallbacks
        $columnMapping = [
            'id' => 'id',
            'username' => 'username',
            'first_name' => 'first_name',
            'last_name' => 'last_name',
            'Phone_number' => 'Phone_number',
            'email_id' => 'email_id',
            'company_name' => 'company_name',
            'status' => 'status',
            'reportingTo' => 'reportingTo',
            'alias_name' => 'alias_name',
            'alternative_mobile_number' => 'alternative_mobile_number',
            'branch_state_name_id' => 'branch_state_name_id',
            'branch_location_id' => 'branch_location_id',
            'bank_id' => 'bank_id',
            'account_type_id' => 'account_type_id',
            'office_address' => 'office_address',
            'residential_address' => 'residential_address',
            'aadhaar_number' => 'aadhaar_number',
            'pan_number' => 'pan_number',
            'account_number' => 'account_number',
            'ifsc_code' => 'ifsc_code',
            'rank' => 'rank',
            'pan_img' => 'pan_img',
            'aadhaar_img' => 'aadhaar_img',
            'photo_img' => 'photo_img',
            'bankproof_img' => 'bankproof_img',
            'resume_file' => 'resume_file',
            'data_icons' => 'data_icons',
            'work_icons' => 'work_icons',
            'user_id' => 'user_id',
            'createdBy' => 'createdBy',
            'created_at' => 'created_at',
            'updated_at' => 'updated_at'
        ];
        
        foreach ($columnMapping as $expected => $actual) {
            if (in_array($actual, $columns)) {
                $selectColumns[] = $actual . " as " . $expected;
            }
        }
        
        if (empty($selectColumns)) {
            throw new Exception("No usable columns found in tbl_bh_users");
        }
        
        $query = "SELECT " . implode(', ', $selectColumns) . " 
                  FROM tbl_bh_users 
                  WHERE reportingTo = ? AND (status = 'active' OR status = 'Active' OR status = 1)
                  ORDER BY " . (in_array('firstName', $columns) ? 'firstName' : 'username') . " ASC";
        
        $stmt = $pdo->prepare($query);
        $stmt->execute([$loggedInUsername]);
        $teamMembers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Get database statistics
        $totalBhUsersQuery = "SELECT COUNT(*) as total FROM tbl_bh_users WHERE reportingTo = ?";
        $totalBhUsersStmt = $pdo->prepare($totalBhUsersQuery);
        $totalBhUsersStmt->execute([$loggedInUsername]);
        $totalBhUsers = $totalBhUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];

        $activeBhUsersQuery = "SELECT COUNT(*) as total FROM tbl_bh_users WHERE reportingTo = ? AND (status = 'active' OR status = 'Active' OR status = 1)";
        $activeBhUsersStmt = $pdo->prepare($activeBhUsersQuery);
        $activeBhUsersStmt->execute([$loggedInUsername]);
        $activeBhUsers = $activeBhUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];
        
    } else {
        // Use tbl_user table with Business Head designation - get more complete information
        $query = "SELECT u.id, u.username, '' as alias_name, u.firstName as first_name, u.lastName as last_name, 
                         u.mobile as Phone_number, u.email_id, '' as alternative_mobile_number, u.company_name, 
                         '' as branch_state_name_id, '' as branch_location_id, u.bank_id, u.account_type_id, 
                         u.office_address, u.residential_address, u.aadhaar_number, u.pan_number, 
                         u.account_number, u.ifsc_code, d.designation_name as rank, u.status, u.reportingTo,
                         '' as pan_img, '' as aadhaar_img, '' as photo_img, '' as bankproof_img, 
                         '' as resume_file, '' as data_icons, '' as work_icons, '' as user_id, '' as createdBy,
                         u.created_at, u.updated_at
                  FROM tbl_user u
                  INNER JOIN tbl_designation d ON u.designation_id = d.id
                  WHERE u.reportingTo = ? 
                  AND d.designation_name IN ('SDSA', 'Partner', 'Agent')
                  AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                  ORDER BY u.firstName ASC, u.lastName ASC";

        $stmt = $pdo->prepare($query);
        $stmt->execute([$loggedInUsername]);
        $teamMembers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Get database statistics
        $totalUsersQuery = "SELECT COUNT(*) as total FROM tbl_user u
                           INNER JOIN tbl_designation d ON u.designation_id = d.id
                           WHERE u.reportingTo = ? AND d.designation_name IN ('SDSA', 'Partner', 'Agent')";
        $totalUsersStmt = $pdo->prepare($totalUsersQuery);
        $totalUsersStmt->execute([$loggedInUsername]);
        $totalBhUsers = $totalUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];

        $activeUsersQuery = "SELECT COUNT(*) as total FROM tbl_user u
                            INNER JOIN tbl_designation d ON u.designation_id = d.id
                            WHERE u.reportingTo = ? 
                            AND d.designation_name IN ('SDSA', 'Partner', 'Agent')
                            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')";
        $activeUsersStmt = $pdo->prepare($activeUsersQuery);
        $activeUsersStmt->execute([$loggedInUsername]);
        $activeBhUsers = $activeUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];
    }

    // Prepare response matching CBO format
    $response = [
        'status' => 'success',
        'message' => 'Business Head Active Employee List fetched successfully',
        'data' => [
            'manager' => $managerDetails,
            'team_members' => $teamMembers,
            'statistics' => [
                'total_team_members' => $totalBhUsers,
                'active_members' => $activeBhUsers
            ]
        ],
        'counts' => [
            'total_team_members' => $totalBhUsers,
            'active_members' => $activeBhUsers
        ]
    ];

    echo json_encode($response);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch active employees: ' . $e->getMessage()
    ]);
}
?> 