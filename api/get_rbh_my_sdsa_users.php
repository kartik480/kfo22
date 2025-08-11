<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
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
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get GET parameters
    $reportingTo = $_GET['reportingTo'] ?? '';
    $status = $_GET['status'] ?? 'active';
    
    if (empty($reportingTo)) {
        throw new Exception('ReportingTo parameter is required');
    }
    
    // TEMPORARY: Skip RBH verification for testing
    // First, verify the logged-in user exists (but don't check designation)
    $userVerificationSql = "
        SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.id = ?
    ";
    
    $userStmt = $pdo->prepare($userVerificationSql);
    $userStmt->bindParam(1, $reportingTo, PDO::PARAM_STR);
    $userStmt->execute();
    $loggedInUser = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        throw new Exception('User not found with ID: ' . $reportingTo);
    }
    
    // TEMPORARY: Skip RBH verification - just check if user exists
    // $isRBH = ($loggedInUser['designation_name'] === 'Regional Business Head');
    // if (!$isRBH) {
    //     throw new Exception('Access denied. Only Regional Business Head users can access this feature.');
    // }
    
    // Query to fetch SDSA users reporting to this RBH user by joining tbl_user on reportingTo IDs
    $query = "SELECT 
                su.id,
                su.username,
                su.alias_name,
                su.first_name,
                su.last_name,
                su.Phone_number,
                su.email_id,
                su.alternative_mobile_number,
                su.company_name,
                su.branch_state_name_id,
                su.branch_location_id,
                su.bank_id,
                su.account_type_id,
                su.office_address,
                su.residential_address,
                su.aadhaar_number,
                su.pan_number,
                su.account_number,
                su.ifsc_code,
                su.rank,
                su.status,
                su.reportingTo,
                su.employee_no,
                su.department,
                su.designation,
                su.branchstate,
                su.branchloaction,
                su.bank_name,
                su.account_type,
                su.user_id,
                su.createdBy,
                su.created_at,
                su.updated_at,
                CONCAT(su.first_name, ' ', su.last_name) as fullName,
                CONCAT(su.first_name, ' ', su.last_name, ' (', su.designation, ')') as displayName,
                u.id as manager_id,
                CONCAT(u.firstName, ' ', u.lastName) as manager_name
              FROM tbl_sdsa_users su
              INNER JOIN tbl_user u ON FIND_IN_SET(u.id, su.reportingTo)
              WHERE u.id = :reportingTo
              AND (su.status = 'Active' OR su.status = 1 OR su.status IS NULL OR su.status = '')
              AND su.first_name IS NOT NULL AND su.first_name != ''
              ORDER BY su.first_name ASC, su.last_name ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':reportingTo', $reportingTo, PDO::PARAM_STR);
    $stmt->execute();
    
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Calculate statistics
    $totalUsers = count($users);
    $uniqueDesignations = array_unique(array_filter(array_column($users, 'designation')));
    $uniqueDepartments = array_unique(array_filter(array_column($users, 'department')));
    $uniqueBanks = array_unique(array_filter(array_column($users, 'bank_name')));
    
    // Group by designation
    $designationBreakdown = [];
    foreach ($users as $user) {
        $designation = $user['designation'] ?: 'Not Assigned';
        if (!isset($designationBreakdown[$designation])) {
            $designationBreakdown[$designation] = 0;
        }
        $designationBreakdown[$designation]++;
    }
    
    // Group by department
    $departmentBreakdown = [];
    foreach ($users as $user) {
        $department = $user['department'] ?: 'Not Assigned';
        if (!isset($departmentBreakdown[$department])) {
            $departmentBreakdown[$department] = 0;
        }
        $departmentBreakdown[$department]++;
    }
    
    // Group by bank
    $bankBreakdown = [];
    foreach ($users as $user) {
        $bank = $user['bank_name'] ?: 'Not Assigned';
        if (!isset($bankBreakdown[$bank])) {
            $bankBreakdown[$bank] = 0;
        }
        $bankBreakdown[$bank]++;
    }
    
    // Return success response
    echo json_encode([
        'success' => true,
        'message' => 'SDSA users fetched successfully (TEST MODE - RBH verification disabled)',
        'logged_in_user' => [
            'id' => $loggedInUser['id'],
            'username' => $loggedInUser['username'],
            'full_name' => $loggedInUser['firstName'] . ' ' . $loggedInUser['lastName'],
            'designation' => $loggedInUser['designation_name'] ?? 'Not Assigned'
        ],
        'users' => $users,
        'count' => $totalUsers,
        'statistics' => [
            'total_users' => $totalUsers,
            'unique_designations' => count($uniqueDesignations),
            'unique_departments' => count($uniqueDepartments),
            'unique_banks' => count($uniqueBanks)
        ],
        'breakdowns' => [
            'by_designation' => $designationBreakdown,
            'by_department' => $departmentBreakdown,
            'by_bank' => $bankBreakdown
        ]
    ]);
    
} catch (PDOException $e) {
    // Database error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
    
} catch (Exception $e) {
    // General error
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 