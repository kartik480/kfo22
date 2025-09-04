<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Get JSON input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validate input
if (!$data || !isset($data['rbh_username']) || empty($data['rbh_username'])) {
    echo json_encode([
        'success' => false,
        'message' => 'RBH username is required'
    ]);
    exit();
}

$rbhUsername = $data['rbh_username'];

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get partner users created by this RBH user using the createdBy column
    $stmt = $pdo->prepare("
        SELECT 
            pu.id,
            pu.username as partner_username,
            pu.first_name,
            pu.last_name,
            pu.status,
            pu.createdBy,
            pu.updated_at,
            pu.company_name,
            pu.Phone_number,
            pu.email_id,
            pu.office_address,
            pu.residential_address,
            pu.aadhaar_number,
            pu.pan_number,
            pu.account_number,
            pu.ifsc_code,
            pu.rank,
            pu.reportingTo,
            pu.employee_no,
            pu.department,
            pu.designation,
            pu.branchstate,
            pu.branchloaction,
            pu.bank_name,
            pu.account_type,
            pu.partner_type_id,
            pu.created_at
        FROM tbl_partner_users pu
        WHERE pu.createdBy = ?
        ORDER BY pu.id DESC
    ");
    $stmt->execute([$rbhUsername]);
    
    $partnerUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($partnerUsers)) {
        echo json_encode([
            'success' => true,
            'message' => 'No partner users found created by this RBH user',
            'data' => [],
            'rbh_username' => $rbhUsername
        ]);
        exit();
    }
    
    // Format the data for the response
    $formattedData = [];
    foreach ($partnerUsers as $partner) {
        $formattedData[] = [
            'id' => $partner['id'],
            'partner_username' => $partner['partner_username'],
            'partner_name' => trim($partner['first_name'] . ' ' . $partner['last_name']),
            'first_name' => $partner['first_name'],
            'last_name' => $partner['last_name'],
            'status' => $partner['status'] ?? 'Active',
            'created_by' => $partner['createdBy'],
            'company_name' => $partner['company_name'] ?? 'N/A',
            'phone_number' => $partner['Phone_number'] ?? 'N/A',
            'email_id' => $partner['email_id'] ?? 'N/A',
            'office_address' => $partner['office_address'] ?? 'N/A',
            'residential_address' => $partner['residential_address'] ?? 'N/A',
            'aadhaar_number' => $partner['aadhaar_number'] ?? 'N/A',
            'pan_number' => $partner['pan_number'] ?? 'N/A',
            'account_number' => $partner['account_number'] ?? 'N/A',
            'ifsc_code' => $partner['ifsc_code'] ?? 'N/A',
            'rank' => $partner['rank'] ?? 'N/A',
            'reportingTo' => $partner['reportingTo'] ?? 'N/A',
            'employee_no' => $partner['employee_no'] ?? 'N/A',
            'department' => $partner['department'] ?? 'N/A',
            'designation' => $partner['designation'] ?? 'N/A',
            'branchstate' => $partner['branchstate'] ?? 'N/A',
            'branchloaction' => $partner['branchloaction'] ?? 'N/A',
            'bank_name' => $partner['bank_name'] ?? 'N/A',
            'account_type' => $partner['account_type'] ?? 'N/A',
            'partner_type_id' => $partner['partner_type_id'] ?? 'N/A',
            'created_at' => $partner['created_at']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $formattedData,
        'message' => 'Partner users created by RBH user retrieved successfully',
        'count' => count($formattedData),
        'rbh_username' => $rbhUsername
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
