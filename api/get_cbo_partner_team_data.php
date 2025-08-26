<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        'success' => false,
        'message' => 'Only POST method is allowed'
    ]);
    exit();
}

// Get JSON input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validate input
if (!$data || !isset($data['user_id']) || empty($data['user_id']) || 
    !isset($data['selected_user_name']) || empty($data['selected_user_name'])) {
    echo json_encode([
        'success' => false,
        'message' => 'User ID and selected user name are required'
    ]);
    exit();
}

$userId = $data['user_id'];
$selectedUserName = $data['selected_user_name'];

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, get the selected RBH user's username from the name
    $stmt1 = $pdo->prepare("
        SELECT username FROM tbl_user 
        WHERE username = ? 
        AND status = 'active'
    ");
    $stmt1->execute([$selectedUserName]);
    
    $selectedUser = $stmt1->fetch(PDO::FETCH_ASSOC);
    
    if (!$selectedUser) {
        echo json_encode([
            'success' => false,
            'message' => 'Selected RBH user not found'
        ]);
        exit();
    }
    
    $selectedUserUsername = $selectedUser['username'];
    
    // Now get partner users created by this RBH user using the createdBy column
    // Focus on tbl_partner_users table as requested
    $stmt2 = $pdo->prepare("
        SELECT 
            pu.id,
            pu.username as partner_username,
            pu.status,
            pu.createdBy,
            pu.updated_at,
            pu.remarks,
            pu.partner_type,
            pu.company_name,
            pu.phone_number,
            pu.email_id,
            pu.address,
            pu.state,
            pu.location,
            pu.pincode,
            pu.bank_name,
            pu.account_number,
            pu.ifsc_code,
            pu.pan_number,
            pu.aadhaar_number
        FROM tbl_partner_users pu
        WHERE pu.createdBy = ?
        ORDER BY pu.id DESC
    ");
    $stmt2->execute([$selectedUserUsername]);
    
    $partnerUsers = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    
    // If no partner users found, check if table exists and provide debug info
    if (empty($partnerUsers)) {
        // Check if tbl_partner_users table exists
        $tableCheckQuery = "SHOW TABLES LIKE 'tbl_partner_users'";
        $stmt = $pdo->prepare($tableCheckQuery);
        $stmt->execute();
        $tableExists = $stmt->fetch();
        
        if (!$tableExists) {
            echo json_encode([
                'success' => false,
                'message' => 'tbl_partner_users table does not exist',
                'debug' => [
                    'selected_rbh_user' => $selectedUserUsername,
                    'table_exists' => false
                ]
            ]);
            exit();
        }
        
        // Table exists but no data found
        echo json_encode([
            'success' => false,
            'message' => 'No partner users found created by this RBH user',
            'debug' => [
                'selected_rbh_user' => $selectedUserUsername,
                'table_exists' => true,
                'query_executed' => "SELECT * FROM tbl_partner_users WHERE createdBy = '$selectedUserUsername'",
                'focus_column' => 'createdBy'
            ]
        ]);
        exit();
    }
    
    // Format the data for the response
    $formattedData = [];
    foreach ($partnerUsers as $partner) {
        $formattedData[] = [
            'id' => $partner['id'],
            'partner_name' => $partner['partner_username'],
            'partner_type' => $partner['partner_type'] ?? 'N/A',
            'company_name' => $partner['company_name'] ?? 'N/A',
            'status' => $partner['status'] ?? 'Active',
            'created_by' => $partner['createdBy'],
            'phone_number' => $partner['phone_number'] ?? 'N/A',
            'email_id' => $partner['email_id'] ?? 'N/A',
            'address' => $partner['address'] ?? 'N/A',
            'state' => $partner['state'] ?? 'N/A',
            'location' => $partner['location'] ?? 'N/A',
            'pincode' => $partner['pincode'] ?? 'N/A',
            'bank_name' => $partner['bank_name'] ?? 'N/A',
            'account_number' => $partner['account_number'] ?? 'N/A',
            'ifsc_code' => $partner['ifsc_code'] ?? 'N/A',
            'pan_number' => $partner['pan_number'] ?? 'N/A',
            'aadhaar_number' => $partner['aadhaar_number'] ?? 'N/A',
            'remarks' => $partner['remarks'] ?? 'N/A',
            'partner_details' => "Partner created by RBH user: " . $partner['createdBy']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $formattedData,
        'message' => 'Partner users created by RBH user retrieved successfully',
        'count' => count($formattedData),
        'selected_rbh_user' => $selectedUserUsername,
        'query_info' => [
            'table_used' => 'tbl_partner_users',
            'filter_column' => 'createdBy',
            'filter_value' => $selectedUserUsername,
            'focus' => 'createdBy column as requested'
        ]
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred: ' . $e->getMessage()
    ]);
}
?>
