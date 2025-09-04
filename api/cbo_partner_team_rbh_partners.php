<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Query to fetch all partner users created by Regional Business Head users
    $query = "
        SELECT 
            pu.*,
            CONCAT(creator.firstName, ' ', creator.lastName) AS creator_name,
            creator.username AS creator_username,
            d.designation_name AS creator_designation
        FROM tbl_partner_users pu
        LEFT JOIN tbl_user creator ON pu.createdBy = creator.username
        LEFT JOIN tbl_designation d ON creator.designation_id = d.id
        WHERE d.designation_name = 'Regional Business Head'
        ORDER BY pu.id DESC
    ";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($result)) {
        echo json_encode([
            'success' => true,
            'message' => 'No partner users found created by Regional Business Head users',
            'data' => []
        ]);
        exit;
    }
    
    // Format the response
    $formattedData = [];
    foreach ($result as $row) {
        $formattedData[] = [
            'id' => $row['id'],
            'username' => $row['username'],
            'alias_name' => $row['alias_name'],
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'full_name' => trim($row['first_name'] . ' ' . $row['last_name']),
            'phone_number' => $row['Phone_number'],
            'email_id' => $row['email_id'],
            'alternative_mobile_number' => $row['alternative_mobile_number'],
            'company_name' => $row['company_name'],
            'branch_state_name_id' => $row['branch_state_name_id'],
            'branch_location_id' => $row['branch_location_id'],
            'bank_id' => $row['bank_id'],
            'account_type_id' => $row['account_type_id'],
            'office_address' => $row['office_address'],
            'residential_address' => $row['residential_address'],
            'aadhaar_number' => $row['aadhaar_number'],
            'pan_number' => $row['pan_number'],
            'account_number' => $row['account_number'],
            'ifsc_code' => $row['ifsc_code'],
            'rank' => $row['rank'],
            'status' => $row['status'],
            'reportingTo' => $row['reportingTo'],
            'employee_no' => $row['employee_no'],
            'department' => $row['department'],
            'designation' => $row['designation'],
            'branchstate' => $row['branchstate'],
            'branchloaction' => $row['branchloaction'],
            'bank_name' => $row['bank_name'],
            'account_type' => $row['account_type'],
            'partner_type_id' => $row['partner_type_id'],
            'pan_img' => $row['pan_img'],
            'aadhaar_img' => $row['aadhaar_img'],
            'photo_img' => $row['photo_img'],
            'bankproof_img' => $row['bankproof_img'],
            'user_id' => $row['user_id'],
            'created_at' => $row['created_at'],
            'createdBy' => $row['createdBy'],
            'updated_at' => $row['updated_at'],
            'creator_name' => $row['creator_name'],
            'creator_username' => $row['creator_username'],
            'creator_designation' => $row['creator_designation']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Partner users created by Regional Business Head users fetched successfully',
        'data' => $formattedData,
        'count' => count($formattedData)
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>
