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
    
    // First, let's check what the reportingTo parameter contains and find the user
    // The reportingTo parameter can be either user ID or username
    $checkUserQuery = "SELECT id, username, firstName, lastName, designation_id FROM tbl_user WHERE id = ? OR username = ?";
    $checkUserStmt = $pdo->prepare($checkUserQuery);
    $checkUserStmt->execute([$reportingTo, $reportingTo]);
    $loggedInUser = $checkUserStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$loggedInUser) {
        throw new Exception('Logged-in user not found with ID/username: ' . $reportingTo);
    }
    
    // Get user's designation
    $designationQuery = "SELECT designation_name FROM tbl_designation WHERE id = ?";
    $designationStmt = $pdo->prepare($designationQuery);
    $designationStmt->execute([$loggedInUser['designation_id']]);
    $designation = $designationStmt->fetch(PDO::FETCH_ASSOC);
    
    // Build the main query to find RBH users reporting to this user
    // IMPORTANT: In tbl_rbh_users, reportingTo column contains USERNAMES, not user IDs
    $query = "SELECT 
                rbh.id,
                rbh.username,
                rbh.alias_name,
                rbh.first_name,
                rbh.last_name,
                rbh.password,
                rbh.Phone_number,
                rbh.email_id,
                rbh.alternative_mobile_number,
                rbh.company_name,
                rbh.branch_state_name_id,
                rbh.branch_location_id,
                rbh.bank_id,
                rbh.account_type_id,
                rbh.office_address,
                rbh.residential_address,
                rbh.aadhaar_number,
                rbh.pan_number,
                rbh.account_number,
                rbh.ifsc_code,
                rbh.rank,
                rbh.status,
                rbh.reportingTo,
                rbh.pan_img,
                rbh.aadhaar_img,
                rbh.photo_img,
                rbh.bankproof_img,
                rbh.resume_file,
                rbh.data_icons,
                rbh.work_icons,
                rbh.user_id,
                rbh.createdBy,
                rbh.created_at,
                rbh.updated_at
              FROM tbl_rbh_users rbh
              WHERE (
                -- Find users who are reporting to the logged-in user by USERNAME
                rbh.reportingTo = ?
                OR
                -- Also check if createdBy matches (user was created by logged-in user)
                rbh.createdBy = ?
              )
              AND (
                rbh.status = 'Active' 
                OR rbh.status = 'active' 
                OR rbh.status = 1 
                OR rbh.status IS NULL 
                OR rbh.status = ''
              )
              AND rbh.first_name IS NOT NULL 
              AND rbh.first_name != ''
              AND rbh.username != ?  -- Exclude the logged-in user themselves
              ORDER BY rbh.first_name ASC, rbh.last_name ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute([
        $loggedInUser['username'],      // reportingTo matches logged-in user's USERNAME
        $loggedInUser['username'],      // createdBy matches logged-in user's USERNAME
        $loggedInUser['username']       // Exclude self
    ]);
    
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get debug information about the RBH users table structure
    $debugQuery = "SELECT 
                    COUNT(*) as total_rbh_users,
                    COUNT(CASE WHEN reportingTo IS NOT NULL AND reportingTo != '' THEN 1 END) as users_with_reporting_to,
                    COUNT(CASE WHEN createdBy IS NOT NULL AND createdBy != '' THEN 1 END) as users_with_created_by,
                    COUNT(CASE WHEN status = 'Active' OR status = 'active' OR status = 1 THEN 1 END) as active_users
                  FROM tbl_rbh_users";
    
    $debugStmt = $pdo->prepare($debugQuery);
    $debugStmt->execute();
    $debugInfo = $debugStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get sample reportingTo values to see what's in the column
    $sampleQuery = "SELECT DISTINCT reportingTo, COUNT(*) as count 
                    FROM tbl_rbh_users 
                    WHERE reportingTo IS NOT NULL AND reportingTo != '' 
                    GROUP BY reportingTo 
                    LIMIT 15";
    $sampleStmt = $pdo->prepare($sampleQuery);
    $sampleStmt->execute();
    $sampleReportingTo = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get detailed information about users reporting to the logged-in user
    $reportingRelationsQuery = "SELECT 
                                  rbh.id,
                                  rbh.username,
                                  rbh.first_name,
                                  rbh.last_name,
                                  rbh.reportingTo,
                                  rbh.status,
                                  rbh.company_name,
                                  rbh.rank
                                FROM tbl_rbh_users rbh 
                                WHERE rbh.reportingTo = ?";
    $reportingRelationsStmt = $pdo->prepare($reportingRelationsQuery);
    $reportingRelationsStmt->execute([$loggedInUser['username']]);
    $reportingRelations = $reportingRelationsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Also check if there are any users with numeric reportingTo values
    $numericReportingQuery = "SELECT 
                                COUNT(*) as numeric_reporting_count,
                                GROUP_CONCAT(DISTINCT reportingTo) as numeric_values
                              FROM tbl_rbh_users 
                              WHERE reportingTo REGEXP '^[0-9]+$' 
                              AND reportingTo IS NOT NULL 
                              AND reportingTo != ''";
    $numericReportingStmt = $pdo->prepare($numericReportingQuery);
    $numericReportingStmt->execute();
    $numericReportingInfo = $numericReportingStmt->fetch(PDO::FETCH_ASSOC);
    
    // Return success response with debug information
    echo json_encode([
        'success' => true,
        'status' => 'success', // Added root-level status field for Android compatibility
        'message' => 'RBH Active users fetched successfully from tbl_rbh_users',
        'users' => $users,
        'count' => count($users),
        'debug_info' => [
            'logged_in_user' => [
                'id' => $loggedInUser['id'],
                'username' => $loggedInUser['username'],
                'name' => $loggedInUser['firstName'] . ' ' . $loggedInUser['lastName'],
                'designation' => $designation['designation_name'] ?? 'Unknown'
            ],
            'search_parameter' => $reportingTo,
            'table_used' => 'tbl_rbh_users',
            'database_stats' => $debugInfo,
            'sample_reporting_to_values' => $sampleReportingTo,
            'reporting_relationships_found' => $reportingRelations,
            'numeric_reporting_analysis' => $numericReportingInfo,
            'query_approaches_used' => [
                'reporting_to_match' => 'rbh.reportingTo = logged_in_user_username',
                'created_by_match' => 'rbh.createdBy = logged_in_user_username'
            ],
            'key_insight' => 'In tbl_rbh_users, reportingTo column contains USERNAMES, not user IDs',
            'search_logic' => 'Finding users where reportingTo = current_logged_in_username'
        ]
    ]);
    
} catch (PDOException $e) {
    // Database error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => 'PDOException',
            'search_parameter' => $reportingTo ?? 'Not provided',
            'error_details' => $e->getMessage()
        ]
    ]);
    
} catch (Exception $e) {
    // General error
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => 'Exception',
            'search_parameter' => $reportingTo ?? 'Not provided'
        ]
    ]);
}
?> 