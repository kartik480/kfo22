<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    // Try to use the global connection first, if it exists and works
    $conn = null;
    $createdNewConnection = false;
    
    if (isset($GLOBALS['conn']) && $GLOBALS['conn']) {
        try {
            $testQuery = $GLOBALS['conn']->query("SELECT 1 as test");
            if ($testQuery && $testQuery->fetch_assoc()['test'] == 1) {
                $conn = $GLOBALS['conn'];
            }
        } catch (Exception $e) {
            // Global connection failed, will create new one
        }
    }
    
    // If global connection doesn't work, create a new one
    if (!$conn) {
        $conn = new mysqli($db_host, $db_username, $db_password, $db_name);
        $createdNewConnection = true;
        
        // Check connection
        if ($conn->connect_error) {
            throw new Exception("Connection failed: " . $conn->connect_error);
        }
        
        // Set charset to utf8
        $conn->set_charset("utf8");
    }
    
    // Query to fetch partner users created by users with specific designations
    // Chief Business Officer: ID 5, Regional Business Head: ID 12, Director: ID 6
    $sql = "SELECT 
                pu.id,
                pu.username,
                pu.alias_name,
                pu.first_name,
                pu.last_name,
                pu.password,
                pu.Phone_number,
                pu.email_id,
                pu.alternative_mobile_number,
                pu.company_name,
                pu.branch_state_name_id,
                pu.branch_location_id,
                pu.bank_id,
                pu.account_type_id,
                pu.office_address,
                pu.residential_address,
                pu.aadhaar_number,
                pu.pan_number,
                pu.account_number,
                pu.ifsc_code,
                pu.rank,
                pu.status,
                pu.reportingTo,
                pu.employee_no,
                pu.department,
                pu.designation,
                pu.branchstate,
                pu.branchloaction,
                pu.bank_name,
                pu.account_type,
                pu.partner_type_id,
                pu.pan_img,
                pu.aadhaar_img,
                pu.photo_img,
                pu.bankproof_img,
                pu.user_id,
                pu.created_at,
                pu.createdBy,
                pu.updated_at,
                CONCAT(creator.firstName, ' ', creator.lastName) AS creator_name,
                creator.designation_id AS creator_designation_id,
                d.designation_name AS creator_designation_name
            FROM tbl_partner_users pu
            LEFT JOIN tbl_user creator ON pu.createdBy = creator.id
            LEFT JOIN tbl_designation d ON creator.designation_id = d.id
            WHERE creator.designation_id IN (5, 12, 6)
            ORDER BY pu.id DESC";
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = [
            'id' => $row['id'],
            'username' => $row['username'],
            'alias_name' => $row['alias_name'],
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'password' => $row['password'],
            'Phone_number' => $row['Phone_number'],
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
            'creator_designation_id' => $row['creator_designation_id'],
            'creator_designation_name' => $row['creator_designation_name']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $users,
        'count' => count($users),
        'message' => 'Partner users created by Chief Business Officer, Regional Business Head, and Director users fetched successfully'
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} finally {
    // Only close the connection if we created a new one
    if (isset($conn) && $createdNewConnection) {
        $conn->close();
    }
}
?> 