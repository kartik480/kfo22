<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Get parameters from request
    $username = isset($_GET['username']) ? $_GET['username'] : null;
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    
    if (!$username && !$userId) {
        throw new Exception("Username or user_id parameter is required");
    }
    
    // Build query based on provided parameter
    if ($username) {
        $sql = "
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.email_id,
                u.mobile,
                u.employee_no,
                u.designation_id,
                u.department_id,
                u.reportingTo,
                u.status,
                d.designation_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.username = ?
            AND d.designation_name = 'Marketing Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        ";
        $stmt = $conn->prepare($sql);
        $stmt->execute([$username]);
    } else {
        $sql = "
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.email_id,
                u.mobile,
                u.employee_no,
                u.designation_id,
                u.department_id,
                u.reportingTo,
                u.status,
                d.designation_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.id = ?
            AND d.designation_name = 'Marketing Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        ";
        $stmt = $conn->prepare($sql);
        $stmt->execute([$userId]);
    }
    
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($user) {
        // User is a Marketing Head
        $response = [
            'status' => 'success',
            'is_marketing_head' => true,
            'message' => 'User is a Marketing Head',
            'user_data' => $user
        ];
    } else {
        // User is not a Marketing Head
        $response = [
            'status' => 'success',
            'is_marketing_head' => false,
            'message' => 'User is not a Marketing Head',
            'user_data' => null
        ];
    }
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'is_marketing_head' => false,
        'message' => 'Server error: ' . $e->getMessage(),
        'user_data' => null
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
