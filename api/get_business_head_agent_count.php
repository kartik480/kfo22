<?php
/**
 * Business Head Agent Count API
 * Returns the total count of agent users who report to a specific Business Head
 * 
 * @author KfinOne System
 * @version 1.1
 * @date 2025-08-25
 */

// Set error reporting for development (remove in production)
error_reporting(E_ALL);
ini_set('display_errors', 0);
ini_set('log_errors', 1);

// Set headers for API
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$config = [
    'host' => 'p3plzcpnl508816.prod.phx3.secureserver.net',
    'dbname' => 'emp_kfinone',
    'username' => 'emp_kfinone',
    'password' => '*F*im1!Y0D25',
    'charset' => 'utf8mb4'
];

// Response function for consistent output
function sendResponse($status, $message, $data = null, $httpCode = 200) {
    http_response_code($httpCode);
    echo json_encode([
        'status' => $status,
        'message' => $message,
        'data' => $data,
        'timestamp' => date('Y-m-d H:i:s'),
        'api_version' => '1.1'
    ], JSON_UNESCAPED_UNICODE);
    exit();
}

// Log function for debugging
function logMessage($level, $message, $context = []) {
    $logEntry = [
        'timestamp' => date('Y-m-d H:i:s'),
        'level' => $level,
        'message' => $message,
        'context' => $context
    ];
    error_log(json_encode($logEntry));
}

try {
    // Validate request method
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        sendResponse('error', 'Only POST method is allowed', null, 405);
    }
    
    // Get and validate input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (json_last_error() !== JSON_ERROR_NONE) {
        sendResponse('error', 'Invalid JSON format', null, 400);
    }
    
    $username = trim($input['username'] ?? '');
    
    if (empty($username)) {
        sendResponse('error', 'Username is required', null, 400);
    }
    
    // Validate username format (basic validation)
    if (!preg_match('/^[a-zA-Z0-9_]{3,50}$/', $username)) {
        sendResponse('error', 'Invalid username format', null, 400);
    }
    
    logMessage('info', 'Processing request for username', ['username' => $username]);
    
    // Create PDO connection with better error handling
    $dsn = "mysql:host={$config['host']};dbname={$config['dbname']};charset={$config['charset']}";
    $pdo = new PDO($dsn, $config['username'], $config['password'], [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
        PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES {$config['charset']}"
    ]);
    
    // Verify user exists and is a Business Head
    $stmt = $pdo->prepare("
        SELECT 
            u.id, 
            u.designation_id, 
            u.username,
            u.firstName,
            u.lastName,
            d.designation_name,
            u.status
        FROM tbl_user u 
        INNER JOIN tbl_designation d ON u.designation_id = d.id 
        WHERE u.username = ? AND u.status = 'active'
        LIMIT 1
    ");
    
    $stmt->execute([$username]);
    $user = $stmt->fetch();
    
    if (!$user) {
        logMessage('warning', 'User not found or inactive', ['username' => $username]);
        sendResponse('error', 'User not found or inactive', null, 404);
    }
    
    // Check if user is a Business Head
    $designation = strtolower($user['designation_name']);
    if (strpos($designation, 'business head') === false && 
        strpos($designation, 'bh') === false &&
        strpos($designation, 'business') === false) {
        logMessage('warning', 'User is not a Business Head', [
            'username' => $username,
            'designation' => $user['designation_name']
        ]);
        sendResponse('error', 'User is not authorized as Business Head', null, 403);
    }
    
    logMessage('info', 'Business Head validated', [
        'user_id' => $user['id'],
        'username' => $username,
        'designation' => $user['designation_name']
    ]);
    
    // Get agent count with optimized query
    $stmt = $pdo->prepare("
        SELECT COUNT(*) as total_agents
        FROM tbl_user u 
        INNER JOIN tbl_designation d ON u.designation_id = d.id 
        WHERE u.reportingTo = ? 
        AND u.status = 'active'
        AND (
            LOWER(d.designation_name) LIKE '%agent%' 
            OR LOWER(d.designation_name) LIKE '%sales%'
            OR LOWER(d.designation_name) LIKE '%field%'
            OR LOWER(d.designation_name) LIKE '%executive%'
            OR LOWER(d.designation_name) LIKE '%representative%'
            OR LOWER(d.designation_name) LIKE '%officer%'
            OR LOWER(d.designation_name) LIKE '%associate%'
        )
    ");
    
    $stmt->execute([$user['id']]);
    $result = $stmt->fetch();
    
    $totalAgents = (int)$result['total_agents'];
    
    logMessage('info', 'Agent count retrieved successfully', [
        'business_head_id' => $user['id'],
        'total_agents' => $totalAgents
    ]);
    
    // Return success response
    sendResponse('success', 'Agent count retrieved successfully', [
        'total_agents' => $totalAgents,
        'business_head' => [
            'id' => $user['id'],
            'username' => $user['username'],
            'name' => $user['firstName'] . ' ' . $user['lastName'],
            'designation' => $user['designation_name']
        ],
        'query_info' => [
            'reporting_to_id' => $user['id'],
            'status_filter' => 'active',
            'designation_patterns' => ['agent', 'sales', 'field', 'executive', 'representative', 'officer', 'associate']
        ]
    ]);
    
} catch (PDOException $e) {
    logMessage('error', 'Database connection error', [
        'error' => $e->getMessage(),
        'code' => $e->getCode()
    ]);
    sendResponse('error', 'Database connection failed', null, 500);
    
} catch (Exception $e) {
    logMessage('error', 'Unexpected error occurred', [
        'error' => $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
    sendResponse('error', 'An unexpected error occurred', null, 500);
}
?>
