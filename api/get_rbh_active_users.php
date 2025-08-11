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
    
    // Get GET parameters
    $reportingTo = $_GET['reportingTo'] ?? '';
    $status = $_GET['status'] ?? 'active';
    
    if (empty($reportingTo)) {
        throw new Exception('ReportingTo parameter is required');
    }
    
    // Query to fetch users reporting to the specified RBH user
    $query = "SELECT 
                u.firstName as first_name,
                u.lastName as last_name,
                u.username,
                u.mobile as Phone_number,
                u.email_id,
                u.status,
                d.designation_name,
                dept.department_name
              FROM tbl_user u
              INNER JOIN tbl_designation d ON u.designation_id = d.id
              LEFT JOIN tbl_department dept ON u.department_id = dept.id
              WHERE FIND_IN_SET(:reportingTo, u.reportingTo)
              AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
              AND u.firstName IS NOT NULL AND u.firstName != ''
              ORDER BY u.firstName ASC, u.lastName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':reportingTo', $reportingTo, PDO::PARAM_STR);
    $stmt->execute();
    
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return success response
    echo json_encode([
        'success' => true,
        'message' => 'RBH Active users fetched successfully',
        'users' => $users,
        'count' => count($users)
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