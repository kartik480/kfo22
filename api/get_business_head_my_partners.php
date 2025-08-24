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

    // Get Business Head manager details
    $managerQuery = "SELECT id, username, firstName, lastName, email_id, mobile, status 
                     FROM tbl_user 
                     WHERE username = ?";
    $managerStmt = $pdo->prepare($managerQuery);
    $managerStmt->execute([$loggedInUsername]);
    $managerDetails = $managerStmt->fetch(PDO::FETCH_ASSOC);

    // Check if tbl_partner_users table exists and get its structure
    $tableCheckQuery = "SHOW TABLES LIKE 'tbl_partner_users'";
    $tableCheckStmt = $pdo->prepare($tableCheckQuery);
    $tableCheckStmt->execute();
    $partnerTableExists = $tableCheckStmt->fetch(PDO::FETCH_ASSOC);

    if ($partnerTableExists) {
        // Get the actual column structure of tbl_partner_users
        $columnsQuery = "SHOW COLUMNS FROM tbl_partner_users";
        $columnsStmt = $pdo->prepare($columnsQuery);
        $columnsStmt->execute();
        $columns = $columnsStmt->fetchAll(PDO::FETCH_COLUMN);
        
        // Debug: Log available columns
        error_log("Available columns in tbl_partner_users: " . implode(', ', $columns));
        
        // Build dynamic query based on available columns
        $selectFields = [];
        $availableFields = [
            'id', 'username', 'alias_name', 'first_name', 'last_name', 'password',
            'Phone_number', 'email_id', 'alternative_mobile_number', 'company_name',
            'branch_state_name_id', 'branch_location_id', 'bank_id', 'account_type_id',
            'office_address', 'residential_address', 'aadhaar_number', 'pan_number',
            'account_number', 'ifsc_code', 'rank', 'status', 'reportingTo', 'employee_no',
            'department', 'designation', 'branchstate', 'branchloaction', 'bank_name',
            'account_type', 'partner_type_id', 'pan_img', 'aadhaar_img', 'photo_img',
            'bankproof_img', 'created_at', 'createdBy', 'updated_at'
        ];
        
        foreach ($availableFields as $field) {
            if (in_array($field, $columns)) {
                $selectFields[] = $field;
            } else {
                $selectFields[] = "'' as $field";
            }
        }
        
        $selectClause = implode(', ', $selectFields);
        
        // Debug: Log the final query and parameters
        error_log("Final query: SELECT $selectClause FROM tbl_partner_users WHERE createdBy = '$loggedInUsername'");
        
        // Use tbl_partner_users table with createdBy column
        $query = "SELECT $selectClause
                  FROM tbl_partner_users 
                  WHERE createdBy = ? 
                  ORDER BY first_name ASC, last_name ASC";

        $stmt = $pdo->prepare($query);
        $stmt->execute([$loggedInUsername]);
        $partnerUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Debug: Log results
        error_log("Found " . count($partnerUsers) . " partners for user: $loggedInUsername");

        // Get database statistics
        $totalPartnersQuery = "SELECT COUNT(*) as total FROM tbl_partner_users WHERE createdBy = ?";
        $totalPartnersStmt = $pdo->prepare($totalPartnersQuery);
        $totalPartnersStmt->execute([$loggedInUsername]);
        $totalPartners = $totalPartnersStmt->fetch(PDO::FETCH_ASSOC)['total'];

        $activePartnersQuery = "SELECT COUNT(*) as total FROM tbl_partner_users WHERE createdBy = ? AND (status = 'active' OR status = 'Active' OR status = 1)";
        $activePartnersStmt = $pdo->prepare($activePartnersQuery);
        $activePartnersStmt->execute([$loggedInUsername]);
        $activePartners = $activePartnersStmt->fetch(PDO::FETCH_ASSOC)['total'];
        
    } else {
        // Fallback to tbl_user table if tbl_partner_users doesn't exist
        $query = "SELECT id, username, '' as alias_name, firstName as first_name, lastName as last_name, 
                         password, mobile as Phone_number, email_id, '' as alternative_mobile_number, 
                         company_name, branch_state_name_id, branch_location_id, bank_id, account_type_id, 
                         office_address, residential_address, aadhaar_number, pan_number, account_number, 
                         ifsc_code, rank, status, reportingTo, employee_no, '' as department, '' as designation, 
                         '' as branchstate, '' as branchloaction, '' as bank_name, '' as account_type, 
                         '' as partner_type_id, '' as pan_img, '' as aadhaar_img, '' as photo_img, 
                         '' as bankproof_img, created_at, '' as createdBy, updated_at
                  FROM tbl_user 
                  WHERE createdBy = ? 
                  ORDER BY firstName ASC, lastName ASC";

        $stmt = $pdo->prepare($query);
        $stmt->execute([$loggedInUsername]);
        $partnerUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Get database statistics
        $totalPartnersQuery = "SELECT COUNT(*) as total FROM tbl_user WHERE createdBy = ?";
        $totalPartnersStmt = $pdo->prepare($totalPartnersQuery);
        $totalPartnersStmt->execute([$loggedInUsername]);
        $totalPartners = $totalPartnersStmt->fetch(PDO::FETCH_ASSOC)['total'];

        $activePartnersQuery = "SELECT COUNT(*) as total FROM tbl_user WHERE createdBy = ? AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')";
        $activePartnersStmt = $pdo->prepare($activePartnersQuery);
        $activePartnersStmt->execute([$loggedInUsername]);
        $activePartners = $activePartnersStmt->fetch(PDO::FETCH_ASSOC)['total'];
    }

    // Prepare response
    $response = [
        'status' => 'success',
        'message' => 'Business Head My Partners List fetched successfully',
        'data' => [
            'manager' => $managerDetails,
            'partner_users' => $partnerUsers,
            'statistics' => [
                'total_partners' => $totalPartners,
                'active_partners' => $activePartners
            ]
        ],
        'counts' => [
            'total_partners' => $totalPartners,
            'active_partners' => $activePartners
        ]
    ];

    echo json_encode($response);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch Business Head My Partners: ' . $e->getMessage()
    ]);
}
?>
