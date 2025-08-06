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
    $userId = $_GET['user_id'] ?? '';
    
    if (empty($userId)) {
        throw new Exception('User ID parameter is required');
    }
    
    // Query to get user information with designation
    $query = "
        SELECT 
            u.id, 
            u.username, 
            u.firstName, 
            u.lastName, 
            u.status,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.id = ?
    ";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(1, $userId, PDO::PARAM_STR);
    $stmt->execute();
    
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        throw new Exception('User not found with ID: ' . $userId);
    }
    
    // Check if user is a CBO
    $isCBO = ($user['designation_name'] === 'Chief Business Officer');
    
    // Return success response
    echo json_encode([
        'success' => true,
        'message' => 'User information retrieved successfully',
        'user' => $user,
        'is_cbo' => $isCBO,
        'can_access_sdsa' => $isCBO
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