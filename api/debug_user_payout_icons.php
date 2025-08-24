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
            'debug_info' => []
        ]);
        exit();
    }
    
    $debug_info = [];
    
    // 1. Check if user exists and get basic info
    $user_sql = "SELECT id, payout_icons FROM tbl_user WHERE id = :user_id";
    $user_stmt = $pdo->prepare($user_sql);
    $user_stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
    $user_stmt->execute();
    $user_data = $user_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user_data) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User not found',
            'data' => [],
            'debug_info' => [
                'user_id_requested' => $user_id,
                'user_exists' => false
            ]
        ]);
        exit();
    }
    
    $debug_info['user_exists'] = true;
    $debug_info['user_id'] = $user_data['id'];
    $debug_info['payout_icons_raw'] = $user_data['payout_icons'];
    
    // 2. Check if user has payout_icons assigned
    if (empty($user_data['payout_icons']) || $user_data['payout_icons'] == '[]' || $user_data['payout_icons'] == 'null') {
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout types assigned to this user',
            'data' => [],
            'debug_info' => $debug_info
        ]);
        exit();
    }
    
    // 3. Parse the payout_icons JSON array
    $payout_icons_json = $user_data['payout_icons'];
    $payout_ids = json_decode($payout_icons_json, true);
    
    $debug_info['payout_icons_parsed'] = $payout_ids;
    $debug_info['payout_icons_count'] = is_array($payout_ids) ? count($payout_ids) : 0;
    
    if (!$payout_ids || !is_array($payout_ids) || empty($payout_ids)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No valid payout types found in user data',
            'data' => [],
            'debug_info' => $debug_info
        ]);
        exit();
    }
    
    // 4. Check each payout type ID individually
    $payout_type_details = [];
    $active_count = 0;
    $inactive_count = 0;
    $not_found_count = 0;
    
    foreach ($payout_ids as $payout_id) {
        $payout_check_sql = "SELECT id, payout_name, status FROM tbl_payout_type WHERE id = :payout_id";
        $payout_check_stmt = $pdo->prepare($payout_check_sql);
        $payout_check_stmt->bindParam(':payout_id', $payout_id, PDO::PARAM_INT);
        $payout_check_stmt->execute();
        $payout_result = $payout_check_stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($payout_result) {
            $payout_type_details[] = $payout_result;
            if ($payout_result['status'] == 'Active') {
                $active_count++;
            } else {
                $inactive_count++;
            }
        } else {
            $payout_type_details[] = [
                'id' => $payout_id,
                'payout_name' => 'NOT_FOUND',
                'status' => 'NOT_FOUND'
            ];
            $not_found_count++;
        }
    }
    
    $debug_info['payout_type_details'] = $payout_type_details;
    $debug_info['active_count'] = $active_count;
    $debug_info['inactive_count'] = $inactive_count;
    $debug_info['not_found_count'] = $not_found_count;
    
    // 5. Get all payout types in the system for comparison
    $all_payout_types_sql = "SELECT id, payout_name, status FROM tbl_payout_type ORDER BY id";
    $all_payout_types_stmt = $pdo->prepare($all_payout_types_sql);
    $all_payout_types_stmt->execute();
    $all_payout_types = $all_payout_types_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $debug_info['all_payout_types_in_system'] = $all_payout_types;
    $debug_info['total_payout_types_in_system'] = count($all_payout_types);
    
    // 6. Return results
    if ($active_count > 0) {
        $active_payout_types = array_filter($payout_type_details, function($pt) {
            return $pt['status'] == 'Active';
        });
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Payout types analysis complete',
            'data' => array_values($active_payout_types),
            'count' => $active_count,
            'user_id' => $user_id,
            'debug_info' => $debug_info
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No active payout types found for the assigned IDs',
            'data' => [],
            'count' => 0,
            'user_id' => $user_id,
            'debug_info' => $debug_info
        ]);
    }
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage(),
        'data' => [],
        'debug_info' => []
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred: ' . $e->getMessage(),
        'data' => [],
        'debug_info' => []
    ]);
}
?>
