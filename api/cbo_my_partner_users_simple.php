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

try {
    // Database configuration
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';

    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get parameters
    $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    $username = isset($_GET['username']) ? $_GET['username'] : null;
    $createdBy = isset($_GET['createdBy']) ? $_GET['createdBy'] : null;
    
    // Debug info
    $debug = [
        'user_id' => $user_id,
        'username' => $username,
        'createdBy' => $createdBy,
        'all_params' => $_GET
    ];
    
    // If createdBy is provided, use it directly
    if ($createdBy) {
        $cboUsername = $createdBy;
    } else if ($user_id || $username) {
        // Get CBO user info
        $sql = "SELECT username FROM tbl_user WHERE designation_id = (SELECT id FROM tbl_designation WHERE designation_name = 'Chief Business Officer')";
        
        if ($user_id) {
            $sql .= " AND id = :user_id";
            $params = [':user_id' => $user_id];
        } else {
            $sql .= " AND username = :username";
            $params = [':username' => $username];
        }
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$user) {
            throw new Exception('CBO user not found');
        }
        
        $cboUsername = $user['username'];
    } else {
        throw new Exception('Please provide user_id, username, or createdBy parameter');
    }
    
    // Get partner users
    $sql = "SELECT id, username, first_name, last_name, email_id, Phone_number, company_name, status, created_at, createdBy 
            FROM tbl_partner_users 
            WHERE createdBy = :username 
            AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
            ORDER BY first_name ASC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([':username' => $cboUsername]);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Count statistics
    $total = count($users);
    $active = 0;
    $inactive = 0;
    
    foreach ($users as $user) {
        if ($user['status'] === 'Active' || $user['status'] === '1') {
            $active++;
        } else {
            $inactive++;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Partner users fetched successfully',
        'cbo_username' => $cboUsername,
        'data' => $users,
        'statistics' => [
            'total_users' => $total,
            'active_users' => $active,
            'inactive_users' => $inactive
        ],
        'debug' => $debug
    ], JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'debug' => isset($debug) ? $debug : null
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'debug' => isset($debug) ? $debug : null
    ]);
}
?> 