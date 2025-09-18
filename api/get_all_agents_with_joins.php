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
    
    // First, get the RBH user's username from tbl_user and verify they are Regional Business Head
    $rbhQuery = "
        SELECT u.username, u.id, d.designation_name
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.id = :rbh_user_id AND d.designation_name = 'Regional Business Head'
    ";
    $rbhStmt = $conn->prepare($rbhQuery);
    $rbhStmt->bindParam(':rbh_user_id', $rbhUserId, PDO::PARAM_STR);
    $rbhStmt->execute();
    $rbhUser = $rbhStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$rbhUser) {
        echo json_encode([
            'success' => false,
            'message' => 'RBH user not found or user is not Regional Business Head'
        ]);
        exit();
    }
    
    $rbhUsername = $rbhUser['username'];
    
    // Query to fetch all agents created by this RBH user with all joins
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
            bs.branch_state_name,
            bl.branch_location,
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
            CONCAT(u.firstName, ' ', u.lastName) as created_by_name,
            d.designation_name as creator_designation
        FROM tbl_agent_data a
        LEFT JOIN tbl_user u ON a.createdBy = u.username
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_branch_state bs ON CAST(a.state AS CHAR) = CAST(bs.id AS CHAR)
        LEFT JOIN tbl_branch_location bl ON CAST(a.location AS CHAR) = CAST(bl.id AS CHAR)
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
            'username' => $rbhUsername,
            'designation' => $rbhUser['designation_name']
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
