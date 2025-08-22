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
    
    // First, let's check what columns exist in tbl_sdsa_emp_users
    $checkTableQuery = "DESCRIBE tbl_sdsa_emp_users";
    $checkTableStmt = $pdo->prepare($checkTableQuery);
    $checkTableStmt->execute();
    $tableColumns = $checkTableStmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Build the main query to find SDSA employee users reporting to this user
    // IMPORTANT: In tbl_sdsa_emp_users, reportingTo column contains USER IDs, not usernames
    $query = "SELECT * FROM tbl_sdsa_emp_users sdsa_emp
              WHERE (
                -- Find users who are reporting to the logged-in user by USER ID
                sdsa_emp.reportingTo = ?
                OR
                -- Also check if createdBy matches (user was created by logged-in user)
                sdsa_emp.createdBy = ?
              )
              AND (
                sdsa_emp.status = 'Active' 
                OR sdsa_emp.status = 'active' 
                OR sdsa_emp.status = 1 
                OR sdsa_emp.status IS NULL 
                OR sdsa_emp.status = ''
              )
              AND sdsa_emp.first_name IS NOT NULL 
              AND sdsa_emp.first_name != ''
              AND sdsa_emp.id != ?  -- Exclude the logged-in user themselves if they exist in sdsa_emp_users
              ORDER BY sdsa_emp.first_name ASC, sdsa_emp.last_name ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute([
        $loggedInUser['id'],             // reportingTo matches logged-in user's ID
        $loggedInUser['id'],             // createdBy matches logged-in user's ID
        $loggedInUser['id']              // Exclude self
    ]);
    
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get debug information about the SDSA employee users table structure
    $debugQuery = "SELECT 
                    COUNT(*) as total_sdsa_emp_users,
                    COUNT(CASE WHEN reportingTo IS NOT NULL AND reportingTo != '' THEN 1 END) as users_with_reporting_to,
                    COUNT(CASE WHEN createdBy IS NOT NULL AND createdBy != '' THEN 1 END) as users_with_created_by,
                    COUNT(CASE WHEN status = 'Active' OR status = 'active' OR status = 1 THEN 1 END) as active_users
                  FROM tbl_sdsa_emp_users";
    
    $debugStmt = $pdo->prepare($debugQuery);
    $debugStmt->execute();
    $debugInfo = $debugStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get sample reportingTo values to see what's in the column
    $sampleQuery = "SELECT DISTINCT reportingTo, COUNT(*) as count 
                    FROM tbl_sdsa_emp_users 
                    WHERE reportingTo IS NOT NULL AND reportingTo != '' 
                    GROUP BY reportingTo 
                    LIMIT 15";
    $sampleStmt = $pdo->prepare($sampleQuery);
    $sampleStmt->execute();
    $sampleReportingTo = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get detailed information about users reporting to the logged-in user
    $reportingRelationsQuery = "SELECT 
                                  sdsa_emp.id,
                                  sdsa_emp.username,
                                  sdsa_emp.first_name,
                                  sdsa_emp.last_name,
                                  sdsa_emp.reportingTo,
                                  sdsa_emp.status,
                                  sdsa_emp.company_name,
                                  sdsa_emp.rank,
                                  sdsa_emp.department,
                                  sdsa_emp.designation
                                FROM tbl_sdsa_emp_users sdsa_emp 
                                WHERE sdsa_emp.reportingTo = ?";
    $reportingRelationsStmt = $pdo->prepare($reportingRelationsQuery);
    $reportingRelationsStmt->execute([$loggedInUser['id']]);
    $reportingRelations = $reportingRelationsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Also check if there are any users with non-numeric reportingTo values
    $nonNumericReportingQuery = "SELECT 
                                   COUNT(*) as non_numeric_reporting_count,
                                   GROUP_CONCAT(DISTINCT reportingTo) as non_numeric_values
                                 FROM tbl_sdsa_emp_users 
                                 WHERE (reportingTo NOT REGEXP '^[0-9]+$' OR reportingTo IS NULL OR reportingTo = '')
                                 AND reportingTo IS NOT NULL 
                                 AND reportingTo != ''";
    $nonNumericReportingStmt = $pdo->prepare($nonNumericReportingQuery);
    $nonNumericReportingStmt->execute();
    $nonNumericReportingInfo = $nonNumericReportingStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get count of numeric reportingTo values
    $numericReportingQuery = "SELECT 
                                COUNT(*) as numeric_reporting_count,
                                GROUP_CONCAT(DISTINCT reportingTo) as numeric_values
                              FROM tbl_sdsa_emp_users 
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
        'message' => 'SDSA employee users reporting to logged-in user fetched successfully from tbl_sdsa_emp_users',
        'users' => $users,
        'count' => count($users),
        'table_structure' => [
            'table_name' => 'tbl_sdsa_emp_users',
            'available_columns' => $tableColumns,
            'total_columns' => count($tableColumns)
        ],
        'debug_info' => [
            'logged_in_user' => [
                'id' => $loggedInUser['id'],
                'username' => $loggedInUser['username'],
                'name' => $loggedInUser['firstName'] . ' ' . $loggedInUser['lastName'],
                'designation' => $designation['designation_name'] ?? 'Unknown'
            ],
            'search_parameter' => $reportingTo,
            'table_used' => 'tbl_sdsa_emp_users',
            'database_stats' => $debugInfo,
            'sample_reporting_to_values' => $sampleReportingTo,
            'reporting_relationships_found' => $reportingRelations,
            'numeric_reporting_analysis' => $numericReportingInfo,
            'non_numeric_reporting_analysis' => $nonNumericReportingInfo,
            'query_approaches_used' => [
                'reporting_to_match' => 'sdsa_emp.reportingTo = logged_in_user_id',
                'created_by_match' => 'sdsa_emp.createdBy = logged_in_user_id'
            ],
            'key_insight' => 'In tbl_sdsa_emp_users, reportingTo column contains USER IDs, not usernames',
            'search_logic' => 'Finding users where reportingTo = current_logged_in_user_id',
            'table_difference' => 'tbl_sdsa_emp_users uses user IDs in reportingTo, tbl_rbh_users uses usernames',
            'note' => 'This API uses tbl_sdsa_emp_users table instead of tbl_sdsa_users'
        ]
    ]);
    
} catch (PDOException $e) {
    // Database error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'status' => 'error',
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
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'debug_info' => [
            'error_type' => 'Exception',
            'search_parameter' => $reportingTo ?? 'Not provided'
        ]
    ]);
}
?>
