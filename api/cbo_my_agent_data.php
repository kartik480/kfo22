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
        'resolved_username' => $cboUsername,
        'all_params' => $_GET
    ];
    
    if (!$userId && !$username) {
        throw new Exception("User ID or username is required");
    }
    
    // Get the CBO user's username
    $cboUsername = null;
    
    if ($userId) {
        // Get username from user ID
        $userQuery = "SELECT username FROM tbl_user WHERE id = :userId";
        $userStmt = $conn->prepare($userQuery);
        $userStmt->bindParam(':userId', $userId, PDO::PARAM_STR);
        $userStmt->execute();
        $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$userResult) {
            throw new Exception("User not found with ID: " . $userId);
        }
        
        $cboUsername = $userResult['username'];
    } else {
        // Check if the username is actually a full name (contains spaces)
        if (strpos($username, ' ') !== false) {
            // This is likely a full name, try to find the user by full name
            $fullNameQuery = "
                SELECT u.username 
                FROM tbl_user u 
                LEFT JOIN tbl_designation d ON u.designation_id = d.id
                WHERE CONCAT(u.firstName, ' ', u.lastName) = :fullName 
                AND d.designation_name = 'Chief Business Officer'
            ";
            $fullNameStmt = $conn->prepare($fullNameQuery);
            $fullNameStmt->bindParam(':fullName', $username, PDO::PARAM_STR);
            $fullNameStmt->execute();
            $fullNameResult = $fullNameStmt->fetch(PDO::FETCH_ASSOC);
            
            if ($fullNameResult) {
                $cboUsername = $fullNameResult['username'];
            } else {
                // If not found by full name, try to use the username as is
                $cboUsername = $username;
            }
        } else {
            $cboUsername = $username;
        }
    }
    
    // Verify this is a CBO user
    $cboCheckQuery = "
        SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.username = :username 
        AND d.designation_name = 'Chief Business Officer'
    ";
    $cboStmt = $conn->prepare($cboCheckQuery);
    $cboStmt->bindParam(':username', $cboUsername, PDO::PARAM_STR);
    $cboStmt->execute();
    $cboUser = $cboStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$cboUser) {
        throw new Exception("User is not a Chief Business Officer or not found");
    }
    
    // Query to fetch agent data created by this CBO user
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
    $stmt->bindParam(':username', $cboUsername, PDO::PARAM_STR);
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
        'message' => 'CBO My Agent data fetched successfully',
        'data' => $agents,
        'count' => $totalAgents,
        'statistics' => [
            'total_agents' => $totalAgents,
            'active_agents' => $activeAgents,
            'inactive_agents' => $inactiveAgents
        ],
        'cbo_user' => [
            'id' => $cboUser['id'],
            'username' => $cboUser['username'],
            'first_name' => $cboUser['firstName'],
            'last_name' => $cboUser['lastName'],
            'designation' => $cboUser['designation_name']
        ],
        'debug' => $debug
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0,
        'debug' => $debug ?? []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
