<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Get RBH user ID parameter
    $rbhUserId = isset($_GET['rbh_user_id']) ? $_GET['rbh_user_id'] : '';
    
    if (empty($rbhUserId)) {
        echo json_encode([
            'success' => false,
            'message' => 'RBH user ID is required'
        ]);
        exit();
    }
    
    // First, get the RBH user's username from tbl_user
    $rbhQuery = "SELECT username FROM tbl_user WHERE id = :rbh_user_id";
    $rbhStmt = $conn->prepare($rbhQuery);
    $rbhStmt->bindParam(':rbh_user_id', $rbhUserId, PDO::PARAM_STR);
    $rbhStmt->execute();
    $rbhUser = $rbhStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$rbhUser) {
        echo json_encode([
            'success' => false,
            'message' => 'RBH user not found'
        ]);
        exit();
    }
    
    $rbhUsername = $rbhUser['username'];
    
    // Query to fetch agents created by this RBH user
    $sql = "
        SELECT 
            a.id,
            a.full_name,
            a.Phone_number,
            a.email_id,
            a.createdBy,
            a.status,
            a.created_at,
            u.id as user_id,
            u.username,
            u.firstName,
            u.lastName,
            CONCAT(u.firstName, ' ', u.lastName) as created_by_name
        FROM tbl_agent_data a
        LEFT JOIN tbl_user u ON a.createdBy = u.username
        WHERE a.createdBy = :rbh_username
        ORDER BY a.created_at DESC
    ";
    
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':rbh_username', $rbhUsername, PDO::PARAM_STR);
    $stmt->execute();
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'success' => true,
        'message' => 'Agents fetched successfully for RBH user',
        'agents' => $agents,
        'count' => count($agents),
        'rbh_user' => [
            'id' => $rbhUserId,
            'username' => $rbhUsername
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage(),
        'agents' => [],
        'count' => 0
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
