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
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get user ID from request
    $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '';
    
    if (empty($user_id)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User ID is required',
            'data' => [],
            'count' => 0
        ]);
        exit();
    }
    
    // First, get the logged-in user's payout_icons from tbl_user
    $user_sql = "SELECT payout_icons FROM tbl_user WHERE id = :user_id";
    $user_stmt = $pdo->prepare($user_sql);
    $user_stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
    $user_stmt->execute();
    $user_data = $user_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user_data) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found',
            'data' => [],
            'count' => 0
        ]);
        exit();
    }
    
    // Check if user has payout_icons assigned
    if (empty($user_data['payout_icons']) || $user_data['payout_icons'] == '[]' || $user_data['payout_icons'] == 'null') {
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout types assigned to this user',
            'data' => [],
            'count' => 0,
            'user_id' => $user_id
        ]);
        exit();
    }
    
    // Parse the payout_icons JSON array to get payout type IDs
    $payout_icons_json = $user_data['payout_icons'];
    $payout_ids = json_decode($payout_icons_json, true);
    
    if (!$payout_ids || !is_array($payout_ids) || empty($payout_ids)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No valid payout types found in user data',
            'data' => [],
            'count' => 0,
            'user_id' => $user_id,
            'payout_icons_raw' => $payout_icons_json
        ]);
        exit();
    }
    
    // Convert array to comma-separated string for SQL IN clause
    $payout_ids_str = implode(',', array_map('intval', $payout_ids));
    
    // Get payout type details from tbl_payout_type
    $payout_types_sql = "SELECT id, payout_name FROM tbl_payout_type WHERE id IN ($payout_ids_str) AND status = 'Active' ORDER BY id";
    $payout_types_stmt = $pdo->prepare($payout_types_sql);
    $payout_types_stmt->execute();
    $payout_types = $payout_types_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($payout_types) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Payout types fetched successfully for user',
            'data' => $payout_types,
            'count' => count($payout_types),
            'user_id' => $user_id,
            'payout_icons_count' => count($payout_ids)
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No active payout types found for the assigned IDs',
            'data' => [],
            'count' => 0,
            'user_id' => $user_id,
            'payout_icons_count' => count($payout_ids)
        ]);
    }
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ]);
}
?>
