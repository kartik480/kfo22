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
    
    // Get the RBH user ID from the request
    $rbhUserId = isset($_GET['rbh_user_id']) ? $_GET['rbh_user_id'] : null;
    
    if (!$rbhUserId) {
        $response = [
            'success' => false,
            'message' => 'RBH User ID is required',
            'users' => [],
            'count' => 0
        ];
        http_response_code(400);
        echo json_encode($response);
        exit();
    }
    
    // Query to get users who report to the specified RBH user
    $query = "
        SELECT 
            s.id,
            s.username,
            s.alias_name,
            s.first_name,
            s.last_name,
            s.Phone_number,
            s.email_id,
            s.alternative_mobile_number,
            s.company_name,
            s.branch_state_name_id,
            s.branch_location_id,
            s.bank_id,
            s.account_type_id,
            s.office_address,
            s.residential_address,
            s.aadhaar_number,
            s.pan_number,
            s.account_number,
            s.ifsc_code,
            s.rank,
            s.status,
            s.reportingTo,
            s.employee_no,
            s.department,
            s.designation,
            s.branchstate,
            s.branchloaction,
            s.bank_name,
            s.account_type,
            s.pan_img,
            s.aadhaar_img,
            s.photo_img,
            s.bankproof_img,
            s.user_id,
            s.createdBy,
            s.created_at,
            s.updated_at,
            CONCAT(s.first_name, ' ', s.last_name) as fullName,
            CONCAT(s.first_name, ' ', s.last_name, ' (', s.designation, ')') as displayName,
            CONCAT(s.first_name, ' ', s.last_name, ' - ', s.designation, ' (', s.department, ')') as detailedDisplayName
        FROM tbl_sdsa_users s
        WHERE s.reportingTo = :rbh_user_id
        ORDER BY s.rank ASC, s.first_name ASC, s.last_name ASC
    ";
    
    // Execute the query
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':rbh_user_id', $rbhUserId, PDO::PARAM_INT);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($users) {
        $response = [
            'success' => true,
            'message' => 'Reporting users fetched successfully',
            'users' => $users,
            'count' => count($users)
        ];
    } else {
        // If no users found, return empty response
        $response = [
            'success' => true,
            'message' => 'No users found reporting to this RBH',
            'users' => [],
            'count' => 0
        ];
    }
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Database error
    $response = [
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'users' => [],
        'count' => 0
    ];
    http_response_code(500);
    echo json_encode($response);
    
} catch (Exception $e) {
    // General error
    $response = [
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage(),
        'users' => [],
        'count' => 0
    ];
    http_response_code(500);
    echo json_encode($response);
}
?>
