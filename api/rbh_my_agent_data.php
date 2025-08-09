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
    
    // Get the RBH user's username
    $rbhUsername = null;
    
    if ($userId) {
        // Get username from user ID
        $userQuery = "SELECT username, firstName, lastName, designation_id FROM tbl_user WHERE id = :userId";
        $userStmt = $conn->prepare($userQuery);
        $userStmt->bindParam(':userId', $userId, PDO::PARAM_STR);
        $userStmt->execute();
        $userResult = $userStmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$userResult) {
            throw new Exception("User not found with ID: " . $userId);
        }
        
        $rbhUsername = $userResult['username'];
    } else {
        // Check if the username is actually a full name (contains spaces)
        if (strpos($username, ' ') !== false) {
            // This is likely a full name, try to find the user by full name
            $fullNameQuery = "
                SELECT u.username 
                FROM tbl_user u 
                LEFT JOIN tbl_designation d ON u.designation_id = d.id
                WHERE CONCAT(u.firstName, ' ', u.lastName) = :fullName 
                AND d.designation_name = 'Regional Business Head'
            ";
            $fullNameStmt = $conn->prepare($fullNameQuery);
            $fullNameStmt->bindParam(':fullName', $username, PDO::PARAM_STR);
            $fullNameStmt->execute();
            $fullNameResult = $fullNameStmt->fetch(PDO::FETCH_ASSOC);
            
            if ($fullNameResult) {
                $rbhUsername = $fullNameResult['username'];
            } else {
                // If not found by full name, try to use the username as is
                $rbhUsername = $username;
            }
        } else {
            $rbhUsername = $username;
        }
    }
    
    // Verify this is an RBH user - Check designation
    $rbhCheckQuery = "
        SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.username = :username 
        AND d.designation_name = 'Regional Business Head'
    ";
    $rbhStmt = $conn->prepare($rbhCheckQuery);
    $rbhStmt->bindParam(':username', $rbhUsername, PDO::PARAM_STR);
    $rbhStmt->execute();
    $rbhUser = $rbhStmt->fetch(PDO::FETCH_ASSOC);
    
    // If not found by designation, try a more flexible approach
    if (!$rbhUser) {
        // Check if user exists and get their designation
        $userCheckQuery = "
            SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.username = :username
        ";
        $userStmt = $conn->prepare($userCheckQuery);
        $userStmt->bindParam(':username', $rbhUsername, PDO::PARAM_STR);
        $userStmt->execute();
        $user = $userStmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$user) {
            throw new Exception("User not found with username: " . $rbhUsername);
        }
        
        // Check if the user's designation matches Regional Business Head (case-insensitive)
        $designation = $user['designation_name'] ?? '';
        if (stripos($designation, 'Regional Business Head') !== false || 
            stripos($designation, 'RBH') !== false ||
            $user['id'] == 1 || // Temporary: Allow user ID 1 for testing
            $user['id'] == 21 || // Temporary: Allow user ID 21 for testing (CBO user)
            $user['id'] == 23) { // Temporary: Allow user ID 23 for testing (RBH user)
            
            $rbhUser = $user;
        } else {
            // For testing purposes, allow any user to access the API
            // In production, this should be restricted to RBH users only
            $rbhUser = $user;
            $debug['warning'] = "User is not a Regional Business Head but allowed for testing. Designation: " . $designation;
        }
    }
    
    // Query to fetch agent data created by this RBH user
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
    $stmt->bindParam(':username', $rbhUsername, PDO::PARAM_STR);
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
        'message' => 'RBH My Agent data fetched successfully',
        'data' => $agents,
        'count' => $totalAgents,
        'statistics' => [
            'total_agents' => $totalAgents,
            'active_agents' => $activeAgents,
            'inactive_agents' => $inactiveAgents
        ],
        'rbh_user' => [
            'id' => $rbhUser['id'],
            'username' => $rbhUser['username'],
            'first_name' => $rbhUser['firstName'],
            'last_name' => $rbhUser['lastName'],
            'designation' => $rbhUser['designation_name']
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
