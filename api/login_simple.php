<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Include database configuration
require_once 'db_config.php';

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array('success' => false, 'message' => 'Method not allowed'));
    exit;
}

// Get JSON input
$input = json_decode(file_get_contents('php://input'), true);

if (!$input) {
    http_response_code(400);
    echo json_encode(array('success' => false, 'message' => 'Invalid JSON input'));
    exit;
}

$username = isset($input['username']) ? $input['username'] : '';
$password = isset($input['password']) ? $input['password'] : '';

// Validate input
if (empty($username) || empty($password)) {
    http_response_code(400);
    echo json_encode(array('success' => false, 'message' => 'Username and password are required'));
    exit;
}

try {
    $conn = getConnection();
    
    // Prepare statement to prevent SQL injection
    $stmt = $conn->prepare("SELECT username, password FROM tbl_user WHERE username = ?");
    $stmt->execute(array($username));
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($user) {
        // Simple string comparison
        if ($password === $user['password']) {
            // Login successful
            echo json_encode(array(
                'success' => true,
                'message' => 'Login successful',
                'user' => array(
                    'username' => $user['username']
                )
            ));
        } else {
            // Invalid password
            http_response_code(401);
            echo json_encode(array('success' => false, 'message' => 'Invalid username or password'));
        }
    } else {
        // User not found
        http_response_code(401);
        echo json_encode(array('success' => false, 'message' => 'Invalid username or password'));
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Login error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array('success' => false, 'message' => 'Internal server error: ' . $e->getMessage()));
}
?> 