<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get all SDSA users who report to ID 11
    $query = "SELECT 
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
                user_id,
                createdBy,
                created_at,
                updated_at
              FROM tbl_sdsa_users 
              WHERE reportingTo = 11 
              AND (status = 'Active' OR status IS NULL OR status = '' OR status != 'Inactive')
              ORDER BY first_name ASC, last_name ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the data for the list
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'],
            'alias_name' => $user['alias_name'],
            'first_name' => $user['first_name'],
            'last_name' => $user['last_name'],
            'full_name' => trim($user['first_name'] . ' ' . $user['last_name']),
            'phone_number' => $user['Phone_number'],
            'email_id' => $user['email_id'],
            'alternative_mobile_number' => $user['alternative_mobile_number'],
            'company_name' => $user['company_name'],
            'employee_no' => $user['employee_no'],
            'department' => $user['department'],
            'designation' => $user['designation'],
            'branchstate' => $user['branchstate'],
            'branchloaction' => $user['branchloaction'],
            'bank_name' => $user['bank_name'],
            'account_type' => $user['account_type'],
            'status' => $user['status'],
            'reportingTo' => $user['reportingTo'],
            'created_at' => $user['created_at'],
            'updated_at' => $user['updated_at']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'SDSA users reporting to ID 11 fetched successfully',
        'data' => $formattedUsers,
        'total_users' => count($formattedUsers)
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 