<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
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
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    $manageIcons = isset($input['manage_icons']) ? $input['manage_icons'] : '';
    
    if (empty($manageIcons)) {
        echo json_encode([
            'success' => false,
            'message' => 'No manage_icons provided'
        ]);
        exit;
    }
    
    // Split the manage_icons string into individual IDs
    $iconIds = explode(',', $manageIcons);
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    
    // Prepare and execute query to fetch icon names
    $query = "SELECT id, icon_name FROM tbl_manage_icon WHERE id IN ($placeholders)";
    $stmt = $pdo->prepare($query);
    $stmt->execute($iconIds);
    
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($results) {
        echo json_encode([
            'success' => true,
            'data' => $results
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'No icons found for the provided IDs'
        ]);
    }
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 