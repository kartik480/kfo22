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
    
    // Step 1: Get the user's payout_icons data from tbl_user
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
    
    $payout_icons_raw = $user_data['payout_icons'];
    
    // Check if payout_icons is empty or null
    if (empty($payout_icons_raw) || $payout_icons_raw == 'null') {
        echo json_encode([
            'status' => 'success',
            'message' => 'User has no payout icons assigned',
            'data' => [],
            'count' => 0,
            'user_id' => $user_id,
            'payout_icons_raw' => $payout_icons_raw
        ]);
        exit();
    }
    
    // Step 2: Parse the payout_icons JSON to get the IDs
    $payout_ids = json_decode($payout_icons_raw, true);
    
    if (!is_array($payout_ids) || empty($payout_ids)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Invalid payout_icons format',
            'data' => null,
            'user_id' => $user_id,
            'payout_icons_raw' => $payout_icons_raw
        ]);
        exit();
    }
    
    // Step 3: Fetch payout type details from tbl_payout_type using the IDs
    $payout_ids_str = implode(',', array_map('intval', $payout_ids));
    
    $payout_types_sql = "SELECT id, payout_name FROM tbl_payout_type WHERE id IN ($payout_ids_str) ORDER BY id";
    $payout_types_stmt = $pdo->prepare($payout_types_sql);
    $payout_types_stmt->execute();
    $payout_types = $payout_types_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Step 4: Return the results
    echo json_encode([
        'status' => 'success',
        'message' => 'User payout types retrieved successfully',
        'data' => $payout_types,
        'count' => count($payout_types),
        'user_id' => $user_id,
        'payout_icons_raw' => $payout_icons_raw,
        'payout_icons_parsed' => $payout_ids,
        'payout_icons_count' => count($payout_ids),
        'query_used' => "SELECT id, payout_name FROM tbl_payout_type WHERE id IN ($payout_ids_str) ORDER BY id"
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
