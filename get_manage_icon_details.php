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
$icon_ids = $data['icon_ids'] ?? null;

// Validate required parameters
if (empty($icon_ids)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => 'icon_ids parameter is required'
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
    
    // Split icon IDs and clean them
    $icon_id_array = array_map('trim', explode(',', $icon_ids));
    $icon_id_array = array_filter($icon_id_array); // Remove empty values
    
    if (empty($icon_id_array)) {
        echo json_encode([
            'success' => false,
            'error' => 'No valid icon IDs provided'
        ]);
        exit();
    }
    
    // Create placeholders for IN clause
    $placeholders = str_repeat('?,', count($icon_id_array) - 1) . '?';
    
    // Query to fetch icon details
    $sql = "SELECT id, icon_name, icon_url, icon_image, icon_description, status 
            FROM tbl_manage_icon 
            WHERE id IN ($placeholders) 
            ORDER BY icon_name ASC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($icon_id_array);
    
    $icons = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($icons) {
        echo json_encode([
            'success' => true,
            'message' => 'Icon details fetched successfully',
            'icons' => $icons,
            'count' => count($icons),
            'debug_info' => [
                'icon_ids_requested' => $icon_ids,
                'icon_ids_processed' => $icon_id_array,
                'icons_found' => count($icons)
            ]
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'error' => 'No icons found with the provided IDs',
            'debug_info' => [
                'icon_ids_requested' => $icon_ids,
                'icon_ids_processed' => $icon_id_array
            ]
        ]);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage(),
        'debug_info' => [
            'icon_ids_requested' => $icon_ids
        ]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage(),
        'debug_info' => [
            'icon_ids_requested' => $icon_ids
        ]
    ]);
}
?>
