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
    // Check if ids parameter is provided
    if (!isset($_GET['ids']) || empty($_GET['ids'])) {
        throw new Exception("Icon IDs are required");
    }
    
    $iconIds = $_GET['ids'];
    
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Convert comma-separated string to array and clean it
    $idArray = array_map('trim', explode(',', $iconIds));
    $idArray = array_filter($idArray, 'is_numeric'); // Only keep numeric IDs
    
    if (empty($idArray)) {
        throw new Exception("No valid icon IDs provided");
    }
    
    // Create placeholders for IN clause
    $placeholders = str_repeat('?,', count($idArray) - 1) . '?';
    
    // Fetch icon details from tbl_work_icon
    $stmt = $pdo->prepare("SELECT id, icon_name, icon_url, icon_image, icon_description FROM tbl_work_icon WHERE id IN ($placeholders) ORDER BY icon_name ASC");
    $stmt->execute($idArray);
    $icons = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Prepare response
    $response = [
        'success' => true,
        'total_icons' => count($icons),
        'requested_ids' => $idArray,
        'icons' => $icons
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