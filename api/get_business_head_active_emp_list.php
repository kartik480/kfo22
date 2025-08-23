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

require_once 'db_config.php';

try {
    $conn = getConnection();

    if (!$conn) {
        throw new Exception("Database connection failed");
    }

    $input = file_get_contents('php://input');
    $postData = $_POST;
    $getData = $_GET;

    $debugInput = [
        'request_method' => $_SERVER['REQUEST_METHOD'],
        'content_type' => $_SERVER['CONTENT_TYPE'] ?? 'Not set',
        'raw_input' => $input,
        'post_data' => $postData,
        'get_data' => $getData,
        'headers' => getallheaders()
    ];

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

    $usernameStmt = $conn->prepare($usernameQuery);
    $usernameStmt->execute($usernameParams);
    $usernameResult = $usernameStmt->fetch(PDO::FETCH_ASSOC);

    if (!$usernameResult) {
        throw new Exception("User not found in tbl_user");
    }

    $loggedInUsername = $usernameResult['username'];

    // Now fetch all users from tbl_bh_users where reportingTo matches the logged-in username
    $query = "SELECT id, username, alias_name, first_name, last_name, password, Phone_number, 
                     email_id, alternative_mobile_number, company_name, branch_state_name_id, 
                     branch_location_id, bank_id, account_type_id, office_address, residential_address, 
                     aadhaar_number, pan_number, account_number, ifsc_code, rank, status, reportingTo, 
                     pan_img, aadhaar_img, photo_img, bankproof_img, resume_file, data_icons, 
                     work_icons, user_id, createdBy, created_at, updated_at
              FROM tbl_bh_users 
              WHERE reportingTo = ?
              ORDER BY first_name ASC, last_name ASC";

    $stmt = $conn->prepare($query);
    $stmt->execute([$loggedInUsername]);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Get database statistics
    $totalBhUsersQuery = "SELECT COUNT(*) as total FROM tbl_bh_users";
    $totalBhUsersStmt = $conn->prepare($totalBhUsersQuery);
    $totalBhUsersStmt->execute();
    $totalBhUsers = $totalBhUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];

    $activeBhUsersQuery = "SELECT COUNT(*) as total FROM tbl_bh_users WHERE status = 'active'";
    $activeBhUsersStmt = $conn->prepare($activeBhUsersQuery);
    $activeBhUsersStmt->execute();
    $activeBhUsers = $activeBhUsersStmt->fetch(PDO::FETCH_ASSOC)['total'];

    $reportingToCountQuery = "SELECT COUNT(*) as total FROM tbl_bh_users WHERE reportingTo = ?";
    $reportingToCountStmt = $conn->prepare($reportingToCountQuery);
    $reportingToCountStmt->execute([$loggedInUsername]);
    $reportingToCount = $reportingToCountStmt->fetch(PDO::FETCH_ASSOC)['total'];

    echo json_encode([
        'status' => 'success',
        'message' => 'All employees fetched successfully for Business Head user',
        'users' => $users,
        'count' => count($users),
        'debug_info' => [
            'logged_in_user' => $loggedInUser,
            'logged_in_username' => $loggedInUsername,
            'query_executed' => $query,
            'database_stats' => [
                'total_bh_users' => $totalBhUsers,
                'active_bh_users' => $activeBhUsers,
                'users_reporting_to_logged_in' => $reportingToCount,
                'all_employees_found' => count($users)
            ],
            'input_debug' => $debugInput
        ]
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch active employees: ' . $e->getMessage(),
        'debug_info' => [
            'error_details' => $e->getMessage(),
            'trace' => $e->getTraceAsString(),
            'input_debug' => $debugInput ?? 'No input debug available'
        ]
    ]);
}
?> 