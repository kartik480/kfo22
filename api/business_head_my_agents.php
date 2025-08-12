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
    // Database connection - try multiple approaches
    $conn = null;
    
    // Method 1: Try to include db_config.php if it exists
    if (file_exists('db_config.php')) {
        require_once 'db_config.php';
        if (function_exists('getConnection')) {
            $conn = getConnection();
        }
    }
    
    // Method 2: Direct connection if db_config.php doesn't exist
    if (!$conn) {
        // Updated with actual KfinOne production database credentials
        $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
        $dbname = 'emp_kfinone';
        $username = 'emp_kfinone';
        $password = '*F*im1!Y0D25';
        
        try {
            // Try PDO first
            $conn = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            // If PDO fails, try MySQLi
            $conn = new mysqli($host, $username, $password, $dbname);
            if ($conn->connect_error) {
                throw new Exception("Connection failed: " . $conn->connect_error);
            }
        }
    }
    
    if (!$conn) {
        throw new Exception("Could not establish database connection");
    }
    
    // Get parameters
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    $username = isset($_GET['username']) ? $_GET['username'] : null;
    
    // Debug information
    $debug = [
        'user_id' => $userId,
        'username' => $username,
        'all_params' => $_GET,
        'connection_type' => get_class($conn)
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
    try {
        if ($conn instanceof PDO) {
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
        } else {
            // MySQLi
            $verifyStmt = $conn->prepare($verifyQuery);
            $paramValue = $userId ?: $username;
            $verifyStmt->bind_param("s", $paramValue);
            $verifyStmt->execute();
            $verifyResult = $verifyStmt->get_result();
            
            if ($verifyResult->num_rows === 0) {
                http_response_code(404);
                echo json_encode([
                    'success' => false,
                    'message' => 'User not found',
                    'error' => 'User with ' . ($userId ? 'ID' : 'username') . ' not found',
                    'debug' => $debug
                ]);
                exit();
            }
            
            $userData = $verifyResult->fetch_assoc();
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Database query error',
            'error' => 'Verification query failed: ' . $e->getMessage(),
            'debug' => $debug
        ]);
        exit();
    }
    
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
    
    // Validate that username exists
    if (!isset($userData['username']) || empty($userData['username'])) {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Invalid user data',
            'error' => 'Username not found in user data',
            'debug' => $debug
        ]);
        exit();
    }
    
    // Now fetch all agents created by this Business Head from tbl_agent_data
    $agentQuery = "SELECT 
                        id,
                        full_name,
                        company_name,
                        Phone_number,
                        alternative_Phone_number,
                        email_id,
                        partnerType,
                        state,
                        location,
                        address,
                        visiting_card,
                        created_user,
                        createdBy,
                        status,
                        created_at,
                        updated_at
                    FROM tbl_agent_data
                    WHERE createdBy = :username
                    ORDER BY created_at DESC";
    
    // Execute agent query
    try {
        if ($conn instanceof PDO) {
            $agentStmt = $conn->prepare($agentQuery);
            $agentStmt->bindParam(':username', $userData['username'], PDO::PARAM_STR);
            $agentStmt->execute();
            $agents = $agentStmt->fetchAll(PDO::FETCH_ASSOC);
        } else {
            // MySQLi
            $agentStmt = $conn->prepare($agentQuery);
            $agentStmt->bind_param("s", $userData['username']);
            $agentStmt->execute();
            $agentResult = $agentStmt->get_result();
            $agents = [];
            while ($row = $agentResult->fetch_assoc()) {
                $agents[] = $row;
            }
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Database query error',
            'error' => 'Agent query failed: ' . $e->getMessage(),
            'debug' => $debug
        ]);
        exit();
    }
    
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
        'message' => 'Agents fetched successfully',
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
        if ($conn instanceof PDO) {
            $conn = null;
        } else {
            $conn->close();
        }
    }
}
?>
