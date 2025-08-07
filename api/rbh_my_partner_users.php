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
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get the logged-in user's username from request parameters
    $loggedInUsername = isset($_GET['username']) ? $_GET['username'] : null;
    
    if (!$loggedInUsername) {
        throw new Exception('Username parameter is required');
    }
    
    // Verify that the logged-in user is a Regional Business Head
    $userVerificationSql = "SELECT 
                                u.id, u.username, u.firstName, u.lastName, u.email_id, u.mobile, 
                                u.employee_no, u.designation_id, d.designation_name,
                                CONCAT(u.firstName, ' ', u.lastName) as fullName
                            FROM tbl_user u
                            INNER JOIN tbl_designation d ON u.designation_id = d.id
                            WHERE d.designation_name = 'Regional Business Head'
                            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                            AND u.username = :username";
    
    $userStmt = $pdo->prepare($userVerificationSql);
    $userStmt->bindParam(':username', $loggedInUsername, PDO::PARAM_STR);
    $userStmt->execute();
    $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception('User not found or not authorized as Regional Business Head');
    }
    
    // Now fetch partner users created by this Regional Business Head
    $partnerSql = "SELECT 
                        id, username, alias_name, first_name, last_name, password,
                        Phone_number, email_id, alternative_mobile_number, company_name,
                        branch_state_name_id, branch_location_id, bank_id, account_type_id,
                        office_address, residential_address, aadhaar_number, pan_number,
                        account_number, ifsc_code, rank, status, reportingTo, employee_no,
                        department, designation, branchstate, branchloaction, bank_name,
                        account_type, partner_type_id, pan_img, aadhaar_img, photo_img,
                        bankproof_img, created_at, createdBy, updated_at,
                        CONCAT(first_name, ' ', last_name) as full_name
                    FROM tbl_partner_users 
                    WHERE createdBy = :creator_username
                    AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                    AND first_name IS NOT NULL 
                    AND first_name != ''
                    ORDER BY first_name ASC, last_name ASC";
    
    $partnerStmt = $pdo->prepare($partnerSql);
    $partnerStmt->bindParam(':creator_username', $loggedInUsername, PDO::PARAM_STR);
    $partnerStmt->execute();
    
    $partnerUsers = $partnerStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Calculate statistics
    $totalCount = count($partnerUsers);
    $activeCount = 0;
    $inactiveCount = 0;
    
    foreach ($partnerUsers as $user) {
        if ($user['status'] == 'Active' || $user['status'] == 1) {
            $activeCount++;
        } else {
            $inactiveCount++;
        }
    }
    
    // Return success response
    $response = [
        'status' => 'success',
        'message' => 'Partner users for Regional Business Head fetched successfully',
        'logged_in_user' => [
            'id' => $userResult['id'],
            'username' => $userResult['username'],
            'full_name' => $userResult['fullName'],
            'designation' => $userResult['designation_name']
        ],
        'statistics' => [
            'total_partners' => $totalCount,
            'active_partners' => $activeCount,
            'inactive_partners' => $inactiveCount
        ],
        'data' => $partnerUsers
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'General error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine()
        ]
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 