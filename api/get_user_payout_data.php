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
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    
    if (!$userId) {
        throw new Exception("User ID is required");
    }
    
    // First, get the user's payout_icons
    $userQuery = "SELECT payout_icons FROM tbl_user WHERE id = :userId";
    $userStmt = $conn->prepare($userQuery);
    $userStmt->bindParam(':userId', $userId, PDO::PARAM_STR);
    $userStmt->execute();
    $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception("User not found with ID: " . $userId);
    }
    
    $payoutIcons = $userResult['payout_icons'];
    
    if (empty($payoutIcons) || $payoutIcons === 'null' || $payoutIcons === '') {
        // Return empty array if no payout icons
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout icons found for this user',
            'data' => []
        ]);
        exit();
    }
    
    // Parse the payout_icons (it's a JSON array string like "[\"1\", \"2\"]")
    $payoutIconIds = json_decode($payoutIcons, true);
    
    if (!is_array($payoutIconIds) || empty($payoutIconIds)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No valid payout icons found',
            'data' => []
        ]);
        exit();
    }
    
    // Create placeholders for the IN clause
    $placeholders = str_repeat('?,', count($payoutIconIds) - 1) . '?';
    
    // Query to fetch payout data with joins to get payout type names
    $sql = "
        SELECT 
            p.id,
            p.user_id,
            p.payout_type_id,
            p.loan_type_id,
            p.vendor_bank_id,
            p.category_id,
            p.payout,
            p.status,
            p.createdBy,
            p.created_user,
            p.created_at,
            p.updated_at,
            pt.payout_name,
            pt.id as payout_type_table_id
        FROM tbl_payout p
        LEFT JOIN tbl_payout_type pt ON p.payout_type_id = pt.id
        WHERE p.id IN ($placeholders)
        ORDER BY p.created_at DESC
    ";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute($payoutIconIds);
    
    $payouts = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return the payout data
    echo json_encode([
        'status' => 'success',
        'message' => 'Payout data fetched successfully',
        'data' => $payouts,
        'count' => count($payouts)
    ]);
    
} catch (Exception $e) {
    error_log("Get user payout data error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'An error occurred while fetching payout data: ' . $e->getMessage()
    ]);
}
?>
