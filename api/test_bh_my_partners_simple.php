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

    // Debug: Log the username we're looking for
    error_log("Looking for partners created by username: $loggedInUsername");

    // Check if tbl_partner_users table exists
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
        
        // Check if createdBy column exists
        if (!in_array('createdBy', $columns)) {
            throw new Exception("Column 'createdBy' not found in tbl_partner_users. Available columns: " . implode(', ', $columns));
        }
        
        // Simple query to test
        $testQuery = "SELECT COUNT(*) as total FROM tbl_partner_users WHERE createdBy = ?";
        $testStmt = $pdo->prepare($testQuery);
        $testStmt->execute([$loggedInUsername]);
        $totalCount = $testStmt->fetch(PDO::FETCH_ASSOC)['total'];
        
        error_log("Total partners found for username '$loggedInUsername': $totalCount");
        
        // Get a few sample records
        $sampleQuery = "SELECT id, username, first_name, last_name, Phone_number, email_id, status, createdBy 
                       FROM tbl_partner_users 
                       WHERE createdBy = ? 
                       LIMIT 5";
        $sampleStmt = $pdo->prepare($sampleQuery);
        $sampleStmt->execute([$loggedInUsername]);
        $samplePartners = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
        
        $response = [
            'status' => 'success',
            'message' => 'Test successful - found partners',
            'debug_info' => [
                'logged_in_username' => $loggedInUsername,
                'total_partners' => $totalCount,
                'available_columns' => $columns,
                'sample_partners' => $samplePartners
            ]
        ];
        
    } else {
        throw new Exception("Table tbl_partner_users does not exist");
    }

    echo json_encode($response);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Test failed: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => get_class($e),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ]);
}
?>
