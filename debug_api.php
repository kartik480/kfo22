<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
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
    
    // Debug: Check if user ID 8 exists and what manage_icons contains
    $query = "SELECT u.id, u.manage_icons FROM tbl_user u WHERE u.id = 8";
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $userResult = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Debug: Check what's in tbl_manage_icon
    $query2 = "SELECT id, icon_name FROM tbl_manage_icon LIMIT 10";
    $stmt2 = $pdo->prepare($query2);
    $stmt2->execute();
    $iconResult = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'debug_info' => [
            'user_data' => $userResult,
            'icon_data' => $iconResult,
            'message' => 'Debug information for troubleshooting'
        ]
    ]);
    
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