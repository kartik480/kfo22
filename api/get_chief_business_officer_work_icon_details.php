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
if (!$data || !isset($data['icon_ids']) || !is_array($data['icon_ids']) || empty($data['icon_ids'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Icon IDs array is required'
    ]);
    exit();
}

$iconIds = $data['icon_ids'];

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Create placeholders for IN clause
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    
    // Query to get work icon details from tbl_work_icon
    $stmt = $pdo->prepare("SELECT id, icon_name, icon_url, icon_image, icon_description FROM tbl_work_icon WHERE id IN ($placeholders) ORDER BY icon_name");
    $stmt->execute($iconIds);
    
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($results) {
        echo json_encode([
            'success' => true,
            'data' => $results,
            'message' => 'Work icon details retrieved successfully',
            'count' => count($results)
        ]);
    } else {
        echo json_encode([
            'success' => true,
            'data' => [],
            'message' => 'No work icons found for the provided IDs',
            'count' => 0
        ]);
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
