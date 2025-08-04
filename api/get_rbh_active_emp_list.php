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
    
    // Get the logged-in user's ID from the request
    $loggedInUserId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    
    if (!$loggedInUserId) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User ID is required'
        ]);
        exit();
    }
    
    // First, verify that the logged-in user is a Regional Business Head
    $userCheckQuery = "SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name 
                      FROM tbl_user u 
                      LEFT JOIN tbl_designation d ON u.designation_id = d.id 
                      WHERE u.id = ? AND u.status = 'active'";
    
    $userCheckStmt = $pdo->prepare($userCheckQuery);
    $userCheckStmt->execute([$loggedInUserId]);
    $loggedInUser = $userCheckStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found or inactive'
        ]);
        exit();
    }
    
    // Check if the user is a Regional Business Head
    if ($loggedInUser['designation_name'] !== 'Regional Business Head') {
        echo json_encode([
            'status' => 'error',
            'message' => 'Access denied. Only Regional Business Head can access this feature.'
        ]);
        exit();
    }
    
    // Get all active employees created by this Regional Business Head
    $query = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.mobile,
                u.email_id,
                u.employee_no,
                u.dob,
                u.joining_date,
                u.status,
                u.reportingTo,
                u.official_phone,
                u.official_email,
                u.work_state,
                u.work_location,
                u.alias_name,
                u.residential_address,
                u.office_address,
                u.pan_number,
                u.aadhaar_number,
                u.alternative_mobile_number,
                u.company_name,
                u.last_working_date,
                u.leaving_reason,
                u.re_joining_date,
                u.created_at,
                u.updated_at,
                d.designation_name,
                dept.department_name,
                bs.state_name as branch_state_name,
                bl.location_name as branch_location_name
              FROM tbl_user u
              LEFT JOIN tbl_designation d ON u.designation_id = d.id
              LEFT JOIN tbl_department dept ON u.department_id = dept.id
              LEFT JOIN tbl_branch_state bs ON u.branch_state_name_id = bs.id
              LEFT JOIN tbl_branch_location bl ON u.branch_location_id = bl.id
              WHERE u.createdBy = ? 
              AND u.status = 'active'
              ORDER BY u.firstName ASC, u.lastName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute([$loggedInUserId]);
    $employees = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_user WHERE createdBy = ? AND status = 'active'";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute([$loggedInUserId]);
    $totalCount = $countStmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Active employees fetched successfully',
        'logged_in_user' => [
            'id' => $loggedInUser['id'],
            'username' => $loggedInUser['username'],
            'firstName' => $loggedInUser['firstName'],
            'lastName' => $loggedInUser['lastName'],
            'designation' => $loggedInUser['designation_name']
        ],
        'total_employees' => $totalCount,
        'employees' => $employees
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 