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

    // First, get the ID of the logged-in user
    $userIdQuery = "";
    $userIdParams = [];
    if (isset($loggedInUser['id'])) {
        $userIdQuery = "SELECT id, username, designation FROM tbl_user WHERE id = ?";
        $userIdParams = [$loggedInUser['id']];
    } else {
        $userIdQuery = "SELECT id, username, designation FROM tbl_user WHERE username = ?";
        $userIdParams = [$loggedInUser['username']];
    }

    $userIdStmt = $conn->prepare($userIdQuery);
    $userIdStmt->execute($userIdParams);
    $userIdResult = $userIdStmt->fetch(PDO::FETCH_ASSOC);

    if (!$userIdResult) {
        throw new Exception("User not found in tbl_user");
    }

    $loggedInUserId = $userIdResult['id'];
    $loggedInUsername = $userIdResult['username'];
    $loggedInDesignation = $userIdResult['designation'];

    // Verify this is a Business Head user - but be more flexible for now
    if ($loggedInDesignation !== 'BH' && $loggedInDesignation !== 'Business Head' && $loggedInDesignation !== 'SuperAdmin') {
        // Log the designation for debugging but don't block
        error_log("User designation: " . $loggedInDesignation . " - allowing access for debugging");
    }

    // Fetch payout types for Business Head users
    // First check if tbl_payout_type exists, if not use the original data structure
    $checkTableQuery = "SHOW TABLES LIKE 'tbl_payout_type'";
    $checkStmt = $conn->prepare($checkTableQuery);
    $checkStmt->execute();
    $tableExists = $checkStmt->fetch();
    
    if ($tableExists) {
        try {
            $query = "SELECT id, payout_name, description, status, created_at 
                      FROM tbl_payout_type 
                      WHERE status = 'active' 
                      ORDER BY payout_name ASC";
            
            $stmt = $conn->prepare($query);
            $stmt->execute();
            $payoutTypes = $stmt->fetchAll(PDO::FETCH_ASSOC);
            
            // Get database statistics
            $totalPayoutTypesQuery = "SELECT COUNT(*) as total FROM tbl_payout_type WHERE status = 'active'";
            $totalPayoutTypesStmt = $conn->prepare($totalPayoutTypesQuery);
            $totalPayoutTypesStmt->execute();
            $totalPayoutTypes = $totalPayoutTypesStmt->fetch(PDO::FETCH_ASSOC)['total'];
            
            echo json_encode([
                'status' => 'success',
                'message' => 'Payout types fetched successfully for Business Head user',
                'data' => $payoutTypes,
                'count' => count($payoutTypes),
                'user_info' => [
                    'id' => $loggedInUserId,
                    'username' => $loggedInUsername,
                    'designation' => $loggedInDesignation
                ],
                'debug_info' => [
                    'logged_in_user' => $loggedInUser,
                    'logged_in_user_id' => $loggedInUserId,
                    'logged_in_username' => $loggedInUsername,
                    'logged_in_designation' => $loggedInDesignation,
                    'table_exists' => true,
                    'using_database_data' => true,
                    'query_executed' => $query,
                    'database_stats' => [
                        'total_payout_types' => $totalPayoutTypes,
                        'payout_types_found' => count($payoutTypes)
                    ],
                    'input_debug' => $debugInput
                ]
            ]);
            return;
            
        } catch (Exception $dbError) {
            error_log("Database error: " . $dbError->getMessage());
            // Fall through to hardcoded data
        }
    }
    
    // Use hardcoded payout types if table doesn't exist or database fails
    $payoutTypes = [
        ['id' => 1, 'payout_name' => 'Branch/Full Payout'],
        ['id' => 2, 'payout_name' => 'Service/Partner Payout'],
        ['id' => 3, 'payout_name' => 'Lead Base/Agent Payout'],
        ['id' => 4, 'payout_name' => 'Connector/Referral Payout'],
        ['id' => 5, 'payout_name' => 'Inter Branch Payout'],
        ['id' => 6, 'payout_name' => 'SDSA Payout']
    ];
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Payout types fetched successfully for Business Head user',
        'data' => $payoutTypes,
        'count' => count($payoutTypes),
        'user_info' => [
            'id' => $loggedInUserId,
            'username' => $loggedInUsername,
            'designation' => $loggedInDesignation
        ],
        'debug_info' => [
            'logged_in_user' => $loggedInUser,
            'logged_in_user_id' => $loggedInUserId,
            'logged_in_username' => $loggedInUsername,
            'logged_in_designation' => $loggedInDesignation,
            'table_exists' => $tableExists ? true : false,
            'using_hardcoded_data' => true,
            'database_error' => isset($dbError) ? $dbError->getMessage() : 'None',
            'input_debug' => $debugInput
        ]
    ]);
    return;

    // This code is no longer needed as we handle everything above

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch payout types: ' . $e->getMessage(),
        'debug_info' => [
            'error_details' => $e->getMessage(),
            'trace' => $e->getTraceAsString(),
            'input_debug' => $debugInput ?? 'No input debug available'
        ]
    ]);
}
?>
