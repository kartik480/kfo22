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
    
    // Simple query to get ALL SDSA users without any JOINs
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
            updated_at
        FROM tbl_sdsa_users
        ORDER BY rank ASC, first_name ASC, last_name ASC
    ";
    
    // Execute the query
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($users) {
        // Add computed fields
        foreach ($users as &$user) {
            $user['fullName'] = $user['first_name'] . ' ' . $user['last_name'];
            $user['displayName'] = $user['first_name'] . ' ' . $user['last_name'] . ' (' . $user['designation'] . ')';
            $user['detailedDisplayName'] = $user['first_name'] . ' ' . $user['last_name'] . ' - ' . $user['designation'] . ' (' . $user['department'] . ')';
        }
        
        $response = [
            'success' => true,
            'message' => 'All SDSA users fetched successfully',
            'users' => $users,
            'count' => count($users)
        ];
    } else {
        // If no users found, return empty response
        $response = [
            'success' => true,
            'message' => 'No SDSA users found in the system',
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
