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
    
    // First, let's get CBO and RBH users
    $cboRbhSql = "SELECT 
                    u.id, u.username, u.firstName, u.lastName, u.email_id, u.mobile, u.employee_no,
                    d.designation_name, dept.department_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                LEFT JOIN tbl_department dept ON u.department_id = dept.id
                WHERE (LOWER(d.designation_name) LIKE '%chief%' 
                       AND LOWER(d.designation_name) LIKE '%business%' 
                       AND LOWER(d.designation_name) LIKE '%officer%'
                       OR LOWER(d.designation_name) LIKE '%cbo%'
                       OR (LOWER(d.designation_name) LIKE '%regional%' 
                           AND LOWER(d.designation_name) LIKE '%business%' 
                           AND LOWER(d.designation_name) LIKE '%head%')
                       OR LOWER(d.designation_name) LIKE '%rbh%')
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND u.firstName IS NOT NULL 
                AND u.firstName != ''
                ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    
    $cboRbhStmt = $pdo->prepare($cboRbhSql);
    $cboRbhStmt->execute();
    $cboRbhUsers = $cboRbhStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Create a set of CBO/RBH user IDs for quick lookup
    $cboRbhUserIds = array();
    foreach ($cboRbhUsers as $user) {
        $cboRbhUserIds[] = $user['id'];
    }
    
    // If no CBO/RBH users found, return empty result
    if (empty($cboRbhUserIds)) {
        $response = [
            'status' => 'success',
            'message' => 'No CBO/RBH users found',
            'debug_info' => [
                'cbo_rbh_users_count' => 0,
                'filtered_partner_users_count' => 0
            ],
            'data' => []
        ];
        echo json_encode($response, JSON_PRETTY_PRINT);
        exit();
    }
    
    // Now get partner users created by these CBO/RBH users
    // Convert the IDs to strings for comparison with createdBy column
    $cboRbhUserIdsStr = array();
    foreach ($cboRbhUserIds as $id) {
        $cboRbhUserIdsStr[] = (string)$id;
    }
    
    $placeholders = str_repeat('?,', count($cboRbhUserIdsStr) - 1) . '?';
    $partnerSql = "SELECT 
                    pu.id, pu.username, pu.alias_name, pu.first_name, pu.last_name, pu.password,
                    pu.Phone_number, pu.email_id, pu.alternative_mobile_number, pu.company_name,
                    pu.branch_state_name_id, pu.branch_location_id, pu.bank_id, pu.account_type_id,
                    pu.office_address, pu.residential_address, pu.aadhaar_number, pu.pan_number,
                    pu.account_number, pu.ifsc_code, pu.rank, pu.status, pu.reportingTo, pu.employee_no,
                    pu.department, pu.designation, pu.branchstate, pu.branchloaction, pu.bank_name,
                    pu.account_type, pu.partner_type_id, pu.pan_img, pu.aadhaar_img, pu.photo_img,
                    pu.bankproof_img, pu.user_id, pu.created_at, pu.createdBy, pu.updated_at,
                    creator.username as creator_username,
                    creator.firstName as creator_first_name,
                    creator.lastName as creator_last_name,
                    creator_designation.designation_name as creator_designation
                FROM tbl_partner_users pu
                LEFT JOIN tbl_user creator ON pu.createdBy = creator.id
                LEFT JOIN tbl_designation creator_designation ON creator.designation_id = creator_designation.id
                WHERE pu.createdBy IN ($placeholders)
                AND (pu.status = 'Active' OR pu.status = 1 OR pu.status IS NULL OR pu.status = '')
                AND pu.first_name IS NOT NULL
                AND pu.first_name != ''
                ORDER BY pu.first_name ASC, pu.last_name ASC";
    
    $partnerStmt = $pdo->prepare($partnerSql);
    $partnerStmt->execute($cboRbhUserIdsStr);
    $partnerUsers = $partnerStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Group the data by designation and creator
    $groupedData = array();
    
    foreach ($partnerUsers as $partnerUser) {
        $creatorDesignation = $partnerUser['creator_designation'] ?: 'Unknown Designation';
        $creatorUsername = $partnerUser['creator_username'] ?: 'Unknown User';
        
        if (!isset($groupedData[$creatorDesignation])) {
            $groupedData[$creatorDesignation] = array();
        }
        
        if (!isset($groupedData[$creatorDesignation][$creatorUsername])) {
            $groupedData[$creatorDesignation][$creatorUsername] = array(
                'creator_info' => array(
                    'id' => $partnerUser['createdBy'],
                    'username' => $partnerUser['creator_username'],
                    'first_name' => $partnerUser['creator_first_name'],
                    'last_name' => $partnerUser['creator_last_name'],
                    'designation' => $partnerUser['creator_designation']
                ),
                'partner_users' => array()
            );
        }
        
        $groupedData[$creatorDesignation][$creatorUsername]['partner_users'][] = $partnerUser;
    }
    
    $response = [
        'status' => 'success',
        'message' => 'Partner users created by CBO/RBH users fetched successfully',
        'debug_info' => [
            'cbo_rbh_users_count' => count($cboRbhUsers),
            'cbo_rbh_user_ids' => $cboRbhUserIds,
            'cbo_rbh_user_ids_string' => $cboRbhUserIdsStr,
            'filtered_partner_users_count' => count($partnerUsers),
            'grouped_data_keys' => array_keys($groupedData)
        ],
        'data' => $groupedData
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug' => [
            'error_details' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'sql_state' => $e->getCode()
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