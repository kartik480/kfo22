<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
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
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Query to fetch only Regional Business Head users
    $query = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.email,
                u.phone,
                u.status,
                d.designation_name,
                dept.department_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
              FROM tbl_user u
              LEFT JOIN tbl_designation d ON u.designation_id = d.id
              LEFT JOIN tbl_department dept ON u.department_id = dept.id
              WHERE d.designation_name = 'Regional Business Head'
              AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
              AND u.firstName IS NOT NULL AND u.firstName != ''
              ORDER BY u.firstName ASC, u.lastName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    
    $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Calculate statistics
    $totalRBH = count($rbhUsers);
    $activeRBH = 0;
    $inactiveRBH = 0;
    
    foreach ($rbhUsers as $user) {
        if ($user['status'] === 'Active' || $user['status'] == 1) {
            $activeRBH++;
        } else {
            $inactiveRBH++;
        }
    }
    
    // Return success response
    echo json_encode([
        'success' => true,
        'message' => 'Regional Business Head users fetched successfully',
        'users' => $rbhUsers,
        'count' => $totalRBH,
        'statistics' => [
            'total_rbh' => $totalRBH,
            'active_rbh' => $activeRBH,
            'inactive_rbh' => $inactiveRBH
        ]
    ]);
    
} catch (PDOException $e) {
    // Database error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
    
} catch (Exception $e) {
    // General error
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 