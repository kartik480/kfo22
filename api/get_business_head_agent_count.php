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
    
    // Get username from request
    $input = json_decode(file_get_contents('php://input'), true);
    $username = isset($input['username']) ? $input['username'] : '';
    
    if (empty($username)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Username is required'
        ]);
        exit();
    }
    
    // First, get the user ID and designation for the Business Head
    $stmt = $pdo->prepare("
        SELECT u.id, u.designation_id, d.designation_name 
        FROM tbl_user u 
        JOIN tbl_designation d ON u.designation_id = d.id 
        WHERE u.username = ? AND u.status = 'active'
    ");
    $stmt->execute([$username]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found or inactive'
        ]);
        exit();
    }
    
    // Check if user is a Business Head
    if (strpos(strtolower($user['designation_name']), 'business head') === false) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User is not a Business Head'
        ]);
        exit();
    }
    
    // Get all agent users who report to this Business Head
    // Agents are users with designation containing 'agent' or 'sales' or similar
    $stmt = $pdo->prepare("
        SELECT COUNT(*) as total_agents
        FROM tbl_user u 
        JOIN tbl_designation d ON u.designation_id = d.id 
        WHERE u.reportingTo = ? 
        AND u.status = 'active'
        AND (
            LOWER(d.designation_name) LIKE '%agent%' 
            OR LOWER(d.designation_name) LIKE '%sales%'
            OR LOWER(d.designation_name) LIKE '%field%'
            OR LOWER(d.designation_name) LIKE '%executive%'
            OR LOWER(d.designation_name) LIKE '%representative%'
        )
    ");
    $stmt->execute([$user['id']]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    $totalAgents = $result['total_agents'];
    
    // Return success response with agent count
    echo json_encode([
        'status' => 'success',
        'message' => 'Agent count retrieved successfully',
        'data' => [
            'total_agents' => $totalAgents,
            'business_head_id' => $user['id'],
            'business_head_username' => $username
        ]
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
