<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
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
    
    // Get request data - support both GET and POST
    $user_id = '';
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $input = json_decode(file_get_contents('php://input'), true);
        $user_id = isset($input['user_id']) ? $input['user_id'] : '';
    } else {
        // For GET requests, user_id is optional
        $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '';
    }
    
    // Note: user_id is optional for this endpoint - we fetch all RBH users
    
    // Query to fetch only Regional Business Head users
    $query = "
        SELECT 
            u.id,
            u.username,
            u.firstName,
            u.lastName,
            u.designation_id,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName,
            CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE d.designation_name = 'Regional Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        AND u.firstName IS NOT NULL AND u.firstName != ''
        ORDER BY u.firstName ASC, u.lastName ASC
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get statistics
    $statsQuery = "
        SELECT 
            COUNT(*) as total_rbh_users,
            SUM(CASE WHEN u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '' THEN 1 ELSE 0 END) as active_rbh_users,
            SUM(CASE WHEN u.status = 'Inactive' OR u.status = 0 THEN 1 ELSE 0 END) as inactive_rbh_users
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE d.designation_name = 'Regional Business Head'
        AND u.firstName IS NOT NULL AND u.firstName != ''
    ";
    
    $statsStmt = $pdo->prepare($statsQuery);
    $statsStmt->execute();
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format response - match Android code expectations
    $response = [
        'success' => true,
        'message' => 'Regional Business Head users fetched successfully',
        'users' => $users,  // Changed from 'data' to 'users' to match Android code
        'statistics' => [
            'total_rbh_users' => (int)$stats['total_rbh_users'],
            'active_rbh_users' => (int)$stats['active_rbh_users'],
            'inactive_rbh_users' => (int)$stats['inactive_rbh_users']
        ]
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Log error
    error_log("Database error in get_rbh_users_for_dropdown.php: " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred',
        'error' => $e->getMessage()
    ]);
} catch (Exception $e) {
    // Log error
    error_log("General error in get_rbh_users_for_dropdown.php: " . $e->getMessage());
    
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred while fetching RBH users',
        'error' => $e->getMessage()
    ]);
}
?>