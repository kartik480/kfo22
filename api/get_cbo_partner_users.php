<?php
header('Content-Type: application/json');
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
    echo json_encode([
        'success' => false,
        'message' => 'Only POST method is allowed'
    ]);
    exit();
}

// Get JSON input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validate input
if (!$data || !isset($data['user_id']) || empty($data['user_id'])) {
    echo json_encode([
        'success' => false,
        'message' => 'User ID is required'
    ]);
    exit();
}

$userId = $data['user_id'];

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Query to get users for CBO Partner Team Management
    // This will get users that the CBO user can manage
    $stmt = $pdo->prepare("
        SELECT DISTINCT u.id, CONCAT(u.first_name, ' ', u.last_name) as name, u.username, u.status
        FROM tbl_user u
        INNER JOIN tbl_user_roles ur ON u.id = ur.user_id
        WHERE ur.role_id IN (SELECT role_id FROM tbl_user_roles WHERE user_id = ?)
        AND u.status = 'active'
        ORDER BY u.first_name, u.last_name
    ");
    $stmt->execute([$userId]);
    
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($results) {
        echo json_encode([
            'success' => true,
            'data' => $results,
            'message' => 'Users retrieved successfully',
            'count' => count($results)
        ]);
    } else {
        // If no specific role-based users found, get all active users
        $stmt2 = $pdo->prepare("
            SELECT id, CONCAT(first_name, ' ', last_name) as name, username, status
            FROM tbl_user 
            WHERE status = 'active'
            ORDER BY first_name, last_name
        ");
        $stmt2->execute();
        
        $allResults = $stmt2->fetchAll(PDO::FETCH_ASSOC);
        
        if ($allResults) {
            echo json_encode([
                'success' => true,
                'data' => $allResults,
                'message' => 'All active users retrieved successfully',
                'count' => count($allResults)
            ]);
        } else {
            echo json_encode([
                'success' => true,
                'data' => [],
                'message' => 'No users found',
                'count' => 0
            ]);
        }
    }
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred'
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred'
    ]);
}
?>
