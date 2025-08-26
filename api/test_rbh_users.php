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

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get request method
    $method = $_SERVER['REQUEST_METHOD'];
    
    if ($method === 'GET') {
        // Simple GET request - show database info
        echo json_encode([
            'success' => true,
            'message' => 'Test RBH Users API - GET request',
            'method' => $method,
            'timestamp' => date('Y-m-d H:i:s'),
            'database' => [
                'host' => $host,
                'database' => $dbname,
                'connected' => true
            ]
        ]);
        exit;
    }
    
    // Handle POST request
    if ($method === 'POST') {
        // Get JSON input
        $input = file_get_contents('php://input');
        $data = json_decode($input, true);
        
        if (!$data) {
            echo json_encode([
                'success' => false,
                'message' => 'No JSON data received',
                'method' => $method,
                'input_received' => $input
            ]);
            exit;
        }
        
        $userId = $data['user_id'] ?? 'not_provided';
        
        // Show what we received
        echo json_encode([
            'success' => true,
            'message' => 'Test RBH Users API - POST request received',
            'method' => $method,
            'timestamp' => date('Y-m-d H:i:s'),
            'data_received' => $data,
            'user_id' => $userId,
            'database' => [
                'host' => $host,
                'database' => $dbname,
                'connected' => true
            ]
        ]);
        exit;
    }
    
    // Unsupported method
    echo json_encode([
        'success' => false,
        'message' => 'Unsupported HTTP method: ' . $method,
        'supported_methods' => ['GET', 'POST']
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'method' => $_SERVER['REQUEST_METHOD'] ?? 'unknown'
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'method' => $_SERVER['REQUEST_METHOD'] ?? 'unknown'
    ]);
}
?>
