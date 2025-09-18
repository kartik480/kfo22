<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get RBH user ID from request
    $rbhUserId = '';
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $input = json_decode(file_get_contents('php://input'), true);
        $rbhUserId = isset($input['rbh_user_id']) ? $input['rbh_user_id'] : '';
    } else {
        $rbhUserId = isset($_GET['rbh_user_id']) ? $_GET['rbh_user_id'] : '';
    }
    
    if (empty($rbhUserId)) {
        echo json_encode([
            'success' => false,
            'message' => 'RBH User ID is required'
        ]);
        exit();
    }
    
    // Query to fetch users reporting to the selected RBH
    // Query tbl_sdsa_users directly where reportingTo column contains the selected user's ID
    $query = "
        SELECT 
            id,
            username,
            alias_name,
            first_name,
            last_name,
            Phone_number,
            email_id,
            alternative_mobile_number,
            company_name,
            branch_state_name_id,
            branch_location_id,
            bank_id,
            account_type_id,
            office_address,
            residential_address,
            aadhaar_number,
            pan_number,
            account_number,
            ifsc_code,
            rank,
            status,
            reportingTo,
            employee_no,
            department,
            designation,
            branchstate,
            branchloaction,
            bank_name,
            account_type,
            pan_img,
            aadhaar_img,
            photo_img,
            bankproof_img,
            user_id,
            createdBy,
            created_at,
            updated_at,
            CONCAT(first_name, ' ', last_name) as fullName,
            CONCAT(first_name, ' ', last_name, ' (', designation, ')') as displayName
        FROM tbl_sdsa_users 
        WHERE reportingTo = :rbh_user_id
        AND first_name IS NOT NULL AND first_name != ''
        ORDER BY first_name ASC, last_name ASC
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':rbh_user_id', $rbhUserId, PDO::PARAM_STR);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics
    $statsQuery = "
        SELECT 
            COUNT(*) as total_reporting_users,
            SUM(CASE WHEN status = 'Active' OR status = 1 OR status IS NULL OR status = '' THEN 1 ELSE 0 END) as active_reporting_users,
            SUM(CASE WHEN status = 'Inactive' OR status = 0 THEN 1 ELSE 0 END) as inactive_reporting_users
        FROM tbl_sdsa_users 
        WHERE reportingTo = :rbh_user_id
        AND first_name IS NOT NULL AND first_name != ''
    ";
    
    $statsStmt = $pdo->prepare($statsQuery);
    $statsStmt->bindParam(':rbh_user_id', $rbhUserId, PDO::PARAM_STR);
    $statsStmt->execute();
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'success' => true,
        'message' => 'Users reporting to selected RBH fetched successfully',
        'users' => $users,
        'statistics' => [
            'total_reporting_users' => (int)$stats['total_reporting_users'],
            'active_reporting_users' => (int)$stats['active_reporting_users'],
            'inactive_reporting_users' => (int)$stats['inactive_reporting_users']
        ],
        'rbh_user_id' => $rbhUserId,
        'debug' => [
            'query_used' => $query,
            'users_found' => count($users),
            'note' => 'Querying tbl_sdsa_users.reportingTo = ' . $rbhUserId . ' and joining with tbl_user'
        ]
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Log error
    error_log("Database error in get_users_reporting_to_rbh.php: " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred',
        'error' => $e->getMessage()
    ]);
} catch (Exception $e) {
    // Log error
    error_log("General error in get_users_reporting_to_rbh.php: " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred while fetching reporting users',
        'error' => $e->getMessage()
    ]);
}
?>
