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
    
    // First, get the username for the given user ID
    $userQuery = "SELECT username FROM tbl_user WHERE id = :userId";
    $userStmt = $conn->prepare($userQuery);
    $userStmt->bindParam(':userId', $userId, PDO::PARAM_STR);
    $userStmt->execute();
    $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$userResult) {
        throw new Exception("User not found with ID: " . $userId);
    }
    
    $username = $userResult['username'];
    
    // Query to fetch agent data filtered by createdBy username
    $sql = "
        SELECT 
            a.id,
            a.full_name,
            a.company_name,
            a.Phone_number,
            a.alternative_Phone_number,
            a.email_id,
            a.partnerType,
            a.state,
            a.location,
            a.address,
            a.visiting_card,
            a.created_user,
            a.createdBy,
            a.status,
            a.created_at,
            a.updated_at,
            u.id as user_id,
            u.username,
            u.firstName,
            u.lastName,
            CONCAT(u.firstName, ' ', u.lastName) as created_by_name
        FROM tbl_agent_data a
        LEFT JOIN tbl_user u ON a.createdBy = u.username
        WHERE a.createdBy = :username
        ORDER BY a.created_at DESC
    ";
    
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':username', $username, PDO::PARAM_STR);
    $stmt->execute();
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Agent data fetched successfully',
        'data' => $agents,
        'count' => count($agents),
        'filtered_by_user_id' => $userId,
        'filtered_by_username' => $username
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 