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
    // Check if user_id parameter is provided
    if (!isset($_GET['user_id']) || empty($_GET['user_id'])) {
        throw new Exception("User ID is required");
    }
    
    $userId = $_GET['user_id'];
    
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Fetch user data including manage_icons
    $stmt = $pdo->prepare("SELECT id, username, firstName, lastName, manage_icons FROM tbl_user WHERE id = ?");
    $stmt->execute([$userId]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        throw new Exception("User not found with ID: " . $userId);
    }
    
    // Prepare response
    $response = [
        'success' => true,
        'user' => [
            'id' => $user['id'],
            'username' => $user['username'],
            'first_name' => $user['firstName'],
            'last_name' => $user['lastName'],
            'manage_icons' => $user['manage_icons']
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    $response = [
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ];
    echo json_encode($response, JSON_PRETTY_PRINT);
} catch (Exception $e) {
    $response = [
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage()
    ];
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 