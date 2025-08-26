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

// Error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $conn = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get parameters
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    $username = isset($_GET['username']) ? $_GET['username'] : null;
    
    // Debug information
    $debug = [
        'user_id' => $userId,
        'username' => $username,
        'all_params' => $_GET,
        'connection_type' => 'PDO'
    ];
    
    // Validate input
    if (!$userId && !$username) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Missing required parameter',
            'error' => 'Either user_id or username must be provided',
            'debug' => $debug
        ]);
        exit();
    }
    
    // First, verify the user is a Business Head
    $verifyQuery = "SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name 
                    FROM tbl_user u 
                    JOIN tbl_designation d ON u.designation_id = d.id 
                    WHERE ";
    
    if ($userId) {
        $verifyQuery .= "u.id = :param";
    } else {
        $verifyQuery .= "u.username = :param";
    }
    
    // Execute verification query
    $verifyStmt = $conn->prepare($verifyQuery);
    $paramValue = $userId ?: $username;
    $verifyStmt->bindParam(':param', $paramValue, PDO::PARAM_STR);
    $verifyStmt->execute();
    $verifyResult = $verifyStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$verifyResult) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'User not found',
            'error' => 'User with ' . ($userId ? 'ID' : 'username') . ' not found',
            'debug' => $debug
        ]);
        exit();
    }
    
    $userData = $verifyResult;
    
    // Check if user is a Business Head
    if ($userData['designation_name'] !== 'Business Head') {
        http_response_code(403);
        echo json_encode([
            'success' => false,
            'message' => 'Access denied',
            'error' => 'User is not a Business Head. Current designation: ' . $userData['designation_name'],
            'debug' => $debug
        ]);
        exit();
    }
    
    // Now fetch all agents created by this Business Head with actual names from lookup tables
    $agentQuery = "SELECT 
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
                        pt.partner_type as partner_type_name,
                        bs.branch_state_name as state_name,
                        bl.branch_location as location_name
                    FROM tbl_agent_data a
                    LEFT JOIN tbl_partner_type pt ON a.partnerType = pt.id
                    LEFT JOIN tbl_branch_state bs ON a.state = bs.id
                    LEFT JOIN tbl_branch_location bl ON a.location = bl.id
                    WHERE a.createdBy = :username
                    ORDER BY a.created_at DESC";
    
    // Execute agent query
    $agentStmt = $conn->prepare($agentQuery);
    $agentStmt->bindParam(':username', $userData['username'], PDO::PARAM_STR);
    $agentStmt->execute();
    $agents = $agentStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Count statistics
    $totalAgents = count($agents);
    $activeAgents = 0;
    $inactiveAgents = 0;
    
    foreach ($agents as $row) {
        if ($row['status'] === 'Active' || $row['status'] === '1') {
            $activeAgents++;
        } else {
            $inactiveAgents++;
        }
    }
    
    // Prepare statistics
    $stats = [
        'total_agents' => $totalAgents,
        'active_agents' => $activeAgents,
        'inactive_agents' => $inactiveAgents
    ];
    
    // Return success response
    echo json_encode([
        'success' => true,
        'message' => 'Agents fetched successfully with enhanced data',
        'data' => $agents,
        'stats' => $stats,
        'creator_info' => [
            'id' => $userData['id'],
            'username' => $userData['username'],
            'firstName' => $userData['firstName'],
            'lastName' => $userData['lastName'],
            'designation' => $userData['designation_name']
        ],
        'debug' => $debug
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Internal server error',
        'error' => $e->getMessage(),
        'debug' => $debug ?? [],
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
} finally {
    if (isset($conn)) {
        $conn = null;
    }
}
?>
