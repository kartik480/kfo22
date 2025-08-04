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
    
    // For testing purposes, if no username provided, use a default one
    if (empty($loggedInUsername)) {
        $loggedInUsername = '10000'; // Default for testing
        error_log("Managing Director API: No username provided, using default: " . $loggedInUsername);
    }
    
    // Log the API call for debugging
    error_log("Managing Director Users Dropdown API called with username: " . $loggedInUsername);
    
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
    
    // Query to fetch all users for dropdown (Chief Business Officer, Regional Business Head, Director, etc.)
    $query = "SELECT DISTINCT 
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
              WHERE (d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director', 'Business Head', 'Managing Director')
                    OR u.id IN (SELECT DISTINCT createdBy FROM tbl_partner_users WHERE createdBy IS NOT NULL AND createdBy != ''))
              AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
              AND u.username IS NOT NULL 
              AND u.username != ''
              ORDER BY u.firstName ASC, u.lastName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'],
            'firstName' => $user['firstName'],
            'lastName' => $user['lastName'],
            'fullName' => $user['fullName'],
            'email_id' => $user['email_id'],
            'mobile' => $user['mobile'],
            'employee_no' => $user['employee_no'],
            'designation_name' => $user['designation_name'],
            'department_name' => $user['department_name'],
            'status' => $user['status']
        ];
    }
    
    // Log the final results
    error_log("Managing Director API: Successfully fetched " . count($formattedUsers) . " users for dropdown for Managing Director " . $loggedInUsername);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Users for dropdown fetched successfully for Managing Director',
        'data' => $formattedUsers,
        'total_users' => count($formattedUsers),
        'managing_director_info' => [
            'id' => $managerResult['id'],
            'username' => $managerResult['username'],
            'full_name' => $managerResult['fullName'],
            'designation' => $managerResult['designation_name'],
            'department' => $managerResult['department_name'],
            'email' => $managerResult['email_id'],
            'mobile' => $managerResult['mobile'],
            'employee_no' => $managerResult['employee_no']
        ],
        'api_info' => [
            'version' => '1.0',
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