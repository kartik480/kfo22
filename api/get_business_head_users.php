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
    
    // Get statistics for Business Head users
    $stmt = $pdo->prepare("
        SELECT 
            COUNT(CASE WHEN u.status = 'active' THEN 1 END) as total_business_head_users,
            COUNT(CASE WHEN u.status = 'active' THEN 1 END) as active_business_head_users,
            COUNT(CASE WHEN u.status = 'active' AND u.reportingTo IS NOT NULL THEN 1 END) as total_team_members,
            COUNT(CASE WHEN u.status = 'active' AND u.reportingTo IS NOT NULL THEN 1 END) as active_team_members,
            COUNT(CASE WHEN u.status = 'active' AND u.reportingTo IS NOT NULL THEN 1 END) as total_sdsa_users,
            COUNT(CASE WHEN u.status = 'active' AND u.reportingTo IS NOT NULL THEN 1 END) as total_partner_users,
            COUNT(CASE WHEN u.status = 'active' AND u.reportingTo IS NOT NULL THEN 1 END) as total_agent_users
        FROM tbl_user u 
        JOIN tbl_designation d ON u.designation_id = d.id 
        WHERE LOWER(d.designation_name) LIKE '%business head%'
    ");
    $stmt->execute();
    $statistics = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // Get list of Business Head users
    $stmt = $pdo->prepare("
        SELECT 
            u.id,
            u.username,
            u.firstName,
            u.lastName,
            u.email_id,
            u.mobile,
            u.status,
            u.created_at,
            d.designation_name
        FROM tbl_user u 
        JOIN tbl_designation d ON u.designation_id = d.id 
        WHERE LOWER(d.designation_name) LIKE '%business head%'
        ORDER BY u.firstName, u.lastName
    ");
    $stmt->execute();
    $businessHeadUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return success response with data
    echo json_encode([
        'status' => 'success',
        'message' => 'Business Head users data retrieved successfully',
        'statistics' => $statistics,
        'data' => $businessHeadUsers
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error occurred',
        'debug' => $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred',
        'debug' => $e->getMessage()
    ]);
}
?> 