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
    
    // Get parameters
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    $username = isset($_GET['username']) ? $_GET['username'] : null;
    
    // Debug information
    $debug = [
        'user_id' => $userId,
        'username' => $username,
        'all_params' => $_GET
    ];
    
    if (!$userId && !$username) {
        throw new Exception("User ID or username is required");
    }
    
    // Get the Business Head user's username
    $bhUsername = null;
    
    if ($userId) {
        // Get username from user ID
        $userQuery = "SELECT username, firstName, lastName, designation_id FROM tbl_user WHERE id = :userId";
        $userStmt = $conn->prepare($userQuery);
        $userStmt->bindParam(':userId', $userId, PDO::PARAM_STR);
        $userStmt->execute();
        $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$userResult) {
            throw new Exception("User not found with ID: " + $userId);
        }
        
        $bhUsername = $userResult['username'];
        
        // Verify the user is a Business Head
        if ($userResult['designation_id']) {
            $designationQuery = "SELECT designation_name FROM tbl_designation WHERE id = :designation_id";
            $designationStmt = $conn->prepare($designationQuery);
            $designationStmt->bindParam(':designation_id', $userResult['designation_id'], PDO::PARAM_STR);
            $designationStmt->execute();
            $designationResult = $designationStmt->fetch(PDO::FETCH_ASSOC);
            
            if (!$designationResult || $designationResult['designation_name'] !== 'Business Head') {
                throw new Exception("User is not a Business Head. Current designation: " . ($designationResult['designation_name'] ?? 'Unknown'));
            }
        }
    } else {
        $bhUsername = $username;
        
        // Verify the user is a Business Head by username
        $userQuery = "SELECT u.id, u.firstName, u.lastName, u.designation_id, d.designation_name 
                      FROM tbl_user u 
                      LEFT JOIN tbl_designation d ON u.designation_id = d.id 
                      WHERE u.username = :username";
        $userStmt = $conn->prepare($userQuery);
        $userStmt->bindParam(':username', $bhUsername, PDO::PARAM_STR);
        $userStmt->execute();
        $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$userResult) {
            throw new Exception("User not found with username: " . $bhUsername);
        }
        
        if ($userResult['designation_name'] !== 'Business Head') {
            throw new Exception("User is not a Business Head. Current designation: " . $userResult['designation_name']);
        }
    }
    
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
    $stmt->bindParam(':username', $bhUsername, PDO::PARAM_STR);
    $stmt->execute();
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Count statistics
    $totalAgents = count($agents);
    $activeAgents = 0;
    $inactiveAgents = 0;
    
    foreach ($agents as $agent) {
        if ($agent['status'] === 'Active' || $agent['status'] === '1' || $agent['status'] === null || $agent['status'] === '') {
            $activeAgents++;
        } else {
            $inactiveAgents++;
        }
    }
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Business Head partner users fetched successfully',
        'business_head_info' => [
            'username' => $bhUsername,
            'full_name' => $userResult['firstName'] . ' ' . $userResult['lastName'],
            'designation' => 'Business Head'
        ],
        'data' => $agents,
        'statistics' => [
            'total_partners' => $totalAgents,
            'active_partners' => $activeAgents,
            'inactive_partners' => $inactiveAgents
        ],
        'counts' => [
            'total_count' => $totalAgents
        ],
        'debug' => $debug
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'debug' => $debug ?? []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
