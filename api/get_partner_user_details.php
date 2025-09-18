<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
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
    
    // Get request data - support both GET and POST
    $partner_user_id = '';
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $input = json_decode(file_get_contents('php://input'), true);
        $partner_user_id = isset($input['partner_user_id']) ? $input['partner_user_id'] : '';
    } else {
        $partner_user_id = isset($_GET['partner_user_id']) ? $_GET['partner_user_id'] : '';
    }
    
    if (empty($partner_user_id)) {
        echo json_encode([
            'success' => false,
            'message' => 'Partner user ID is required'
        ]);
        exit();
    }
    
    // Query to fetch complete partner user details from tbl_partner_users
    $query = "
        SELECT 
            id,
            username,
            alias_name,
            first_name,
            last_name,
            password,
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
            partner_type_id,
            pan_img,
            aadhaar_img,
            photo_img,
            bankproof_img,
            created_at,
            createdBy,
            updated_at
        FROM tbl_partner_users
        WHERE id = :partner_user_id
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':partner_user_id', $partner_user_id, PDO::PARAM_STR);
    $stmt->execute();
    $partnerUser = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($partnerUser) {
        // Format response
        $response = [
            'success' => true,
            'message' => 'Partner user details fetched successfully',
            'partner_user' => $partnerUser
        ];
        
        echo json_encode($response);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Partner user not found'
        ]);
    }
    
} catch (PDOException $e) {
    // Log error
    error_log("Database error in get_partner_user_details.php: " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred',
        'error' => $e->getMessage()
    ]);
} catch (Exception $e) {
    // Log error
    error_log("General error in get_partner_user_details.php: " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred while fetching partner user details',
        'error' => $e->getMessage()
    ]);
}
?>
