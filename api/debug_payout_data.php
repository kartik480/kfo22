<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Get the user ID from query parameter
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : '11';
    
    // Step 1: Get user's payout_icons
    $userQuery = "SELECT id, username, firstName, lastName, payout_icons FROM tbl_user WHERE id = :userId";
    $userStmt = $conn->prepare($userQuery);
    $userStmt->bindParam(':userId', $userId, PDO::PARAM_STR);
    $userStmt->execute();
    $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception("User not found with ID: " . $userId);
    }
    
    $payoutIcons = $userResult['payout_icons'];
    $payoutIconIds = json_decode($payoutIcons, true);
    
    // Step 2: Check what's in tbl_payout table
    $payoutQuery = "SELECT id, payout_type_id, payout_name FROM tbl_payout LIMIT 10";
    $payoutStmt = $conn->prepare($payoutQuery);
    $payoutStmt->execute();
    $payoutResults = $payoutStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Step 3: Check specific payout IDs from user
    $specificPayouts = [];
    if (is_array($payoutIconIds) && !empty($payoutIconIds)) {
        $placeholders = str_repeat('?,', count($payoutIconIds) - 1) . '?';
        $specificQuery = "SELECT id, payout_type_id, payout_name FROM tbl_payout WHERE id IN ($placeholders)";
        $specificStmt = $conn->prepare($specificQuery);
        $specificStmt->execute($payoutIconIds);
        $specificPayouts = $specificStmt->fetchAll(PDO::FETCH_ASSOC);
    }
    
    // Step 4: Check tbl_payout_type table
    $payoutTypeQuery = "SELECT id, payout_name FROM tbl_payout_type LIMIT 10";
    $payoutTypeStmt = $conn->prepare($payoutTypeQuery);
    $payoutTypeStmt->execute();
    $payoutTypeResults = $payoutTypeStmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Debug payout data',
        'user_data' => $userResult,
        'user_payout_icons_parsed' => $payoutIconIds,
        'tbl_payout_sample' => $payoutResults,
        'user_specific_payouts' => $specificPayouts,
        'tbl_payout_type_sample' => $payoutTypeResults
    ]);
    
} catch (Exception $e) {
    error_log("Debug payout data error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred: ' . $e->getMessage()
    ]);
}
?>
