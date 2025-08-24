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
            'data' => null,
            'user_id' => null
        ]);
        exit();
    }
    
    // Get the user's payout_icons data directly from tbl_user
    $user_sql = "SELECT id, payout_icons FROM tbl_user WHERE id = :user_id";
    $user_stmt = $pdo->prepare($user_sql);
    $user_stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
    $user_stmt->execute();
    $user_data = $user_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user_data) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found',
            'data' => null,
            'user_id' => $user_id
        ]);
        exit();
    }
    
    // Return the raw payout_icons data
    $payout_icons_raw = $user_data['payout_icons'];
    
    // Try to parse as JSON to show the structure
    $payout_icons_parsed = null;
    $payout_icons_count = 0;
    
    if (!empty($payout_icons_raw) && $payout_icons_raw != 'null') {
        $payout_icons_parsed = json_decode($payout_icons_raw, true);
        if (is_array($payout_icons_parsed)) {
            $payout_icons_count = count($payout_icons_parsed);
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'User payout icons retrieved successfully',
        'data' => [
            'user_id' => $user_data['id'],
            'payout_icons_raw' => $payout_icons_raw,
            'payout_icons_parsed' => $payout_icons_parsed,
            'payout_icons_count' => $payout_icons_count,
            'is_json_valid' => json_last_error() === JSON_ERROR_NONE
        ],
        'user_id' => $user_id
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'data' => null,
        'user_id' => $user_id ?? null
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred: ' . $e->getMessage(),
        'data' => null,
        'user_id' => $user_id ?? null
    ]);
}
?>
