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
    
    // Get createdBy parameter from request
    $createdBy = isset($_GET['createdBy']) ? $_GET['createdBy'] : null;
    
    if ($createdBy === null) {
        // If no createdBy parameter, fetch all partner users grouped by createdBy
        $sql = "SELECT 
                    createdBy,
                    COUNT(*) as user_count,
                    GROUP_CONCAT(
                        CONCAT(first_name, ' ', last_name, ' (', username, ')') 
                        ORDER BY first_name, last_name 
                        SEPARATOR ', '
                    ) as users_list
                FROM tbl_partner_users 
                WHERE createdBy IS NOT NULL 
                AND createdBy != ''
                AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                AND first_name IS NOT NULL 
                AND first_name != ''
                GROUP BY createdBy
                ORDER BY createdBy ASC";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute();
        $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        $response = [
            'status' => 'success',
            'message' => 'Partner users grouped by createdBy fetched successfully',
            'data' => $results,
            'total_creators' => count($results)
        ];
        
    } else {
        // If createdBy parameter is provided, fetch partner users for that specific creator
        $sql = "SELECT 
                    id, username, alias_name, first_name, last_name, password, 
                    Phone_number, email_id, alternative_mobile_number, company_name,
                    branch_state_name_id, branch_location_id, bank_id, account_type_id, 
                    office_address, residential_address, aadhaar_number, pan_number,
                    account_number, ifsc_code, rank, status, reportingTo, employee_no, 
                    department, designation, branchstate, branchloaction, bank_name,
                    account_type, partner_type_id, pan_img, aadhaar_img, photo_img, 
                    bankproof_img, created_at, createdBy, updated_at
                FROM tbl_partner_users 
                WHERE createdBy = ?
                AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
                AND first_name IS NOT NULL 
                AND first_name != ''
                ORDER BY first_name ASC, last_name ASC";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$createdBy]);
        $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        $response = [
            'status' => 'success',
            'message' => 'Partner users for createdBy = ' . $createdBy . ' fetched successfully',
            'createdBy' => $createdBy,
            'data' => $results,
            'total_users' => count($results)
        ];
    }
    
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