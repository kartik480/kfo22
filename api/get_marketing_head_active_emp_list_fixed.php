<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response = array();
    
    $input = json_decode(file_get_contents('php://input'), true);
    $user_id = isset($input['user_id']) ? $input['user_id'] : '';
    $username = isset($input['username']) ? $input['username'] : '';
    
    // Fetch all active employees from tbl_user (Fixed version)
    try {
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.mobile,
                u.email_id,
                u.password,
                u.birth_date,
                u.employee_no,
                u.department_id,
                u.designation_id,
                u.branch_state_name_id,
                u.branch_location_id,
                u.present_address,
                u.permanent_address,
                u.status,
                u.rank,
                u.avatar,
                u.acc_holder_name,
                u.bank_name,
                u.account_type,
                u.branch_name,
                u.account_number,
                u.ifsc_code,
                u.bank_passbook,
                u.passport_document,
                u.aadhar_document,
                u.aadhar_back_document,
                u.pancard_document,
                u.reportingTo,
                u.official_phone,
                u.official_email,
                u.work_state,
                u.work_location,
                u.residential_address,
                u.office_address,
                u.pan_number,
                u.aadhaar_number,
                u.manage_icons,
                u.data_icons,
                u.ref_name_1,
                u.ref_relation_1,
                u.ref_mobile_1,
                u.ref_address_1,
                u.ref_name_2,
                u.ref_relation_2,
                u.ref_mobile_2,
                u.ref_address_2,
                u.createdBy,
                u.created_at,
                u.updated_at,
                d.designation_name,
                dept.department_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', COALESCE(d.designation_name, 'N/A'), ')') as displayName
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_department dept ON u.department_id = dept.id
            WHERE (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName, u.lastName
        ");
        $stmt->execute();
        $activeEmployees = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        $response['active_employees'] = $activeEmployees;
        $response['total_count'] = count($activeEmployees);
        
    } catch (Exception $e) {
        $response['active_employees'] = array();
        $response['total_count'] = 0;
        $response['error'] = $e->getMessage();
    }
    
    $response['status'] = 'success';
    $response['message'] = 'Active employees fetched successfully';
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = array(
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'active_employees' => array(),
        'total_count' => 0
    );
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
