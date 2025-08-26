<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
        'error' => 'Method not allowed. Use POST.'
    ]);
    exit();
}

// Get JSON input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Check if JSON is valid
if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => 'Invalid JSON data'
    ]);
    exit();
}

// Extract parameters
$username = $data['username'] ?? null;
$user_id = $data['user_id'] ?? null;

// Validate required parameters
if (empty($username) && empty($user_id)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => 'Either username or user_id is required'
    ]);
    exit();
}

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $dbuser = 'emp_kfinone';
    $dbpass = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $dbuser, $dbpass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Build query based on available parameters
    if (!empty($user_id)) {
        $sql = "SELECT manage_icons FROM tbl_user WHERE id = ?";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$user_id]);
    } else {
        $sql = "SELECT manage_icons FROM tbl_user WHERE username = ?";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$username]);
    }
    
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($result) {
        $manage_icons = $result['manage_icons'] ?? '';
        
        echo json_encode([
            'success' => true,
            'message' => 'Manage icons fetched successfully',
            'manage_icons' => $manage_icons,
            'debug_info' => [
                'username' => $username,
                'user_id' => $user_id,
                'manage_icons_found' => !empty($manage_icons)
            ]
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'error' => 'User not found',
            'debug_info' => [
                'username' => $username,
                'user_id' => $user_id
            ]
        ]);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage(),
        'debug_info' => [
            'username' => $username,
            'user_id' => $user_id
        ]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage(),
        'debug_info' => [
            'username' => $username,
            'user_id' => $user_id
        ]
    ]);
}
?>
