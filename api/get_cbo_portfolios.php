<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create connection using PDO
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get the logged-in user's username from request parameters
    $loggedInUsername = isset($_GET['username']) ? $_GET['username'] : null;
    
    if (!$loggedInUsername) {
        echo json_encode([
            'success' => false,
            'message' => 'Username parameter is required'
        ]);
        exit;
    }
    
    // First, check if tbl_portfolio table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_portfolio'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        echo json_encode([
            'success' => false,
            'message' => 'tbl_portfolio table does not exist',
            'portfolios' => []
        ]);
        exit;
    }
    
    // Check if tbl_user table exists
    $checkUserTable = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
    $checkUserTable->execute();
    $userTableExists = $checkUserTable->fetch();
    
    if (!$userTableExists) {
        echo json_encode([
            'success' => false,
            'message' => 'tbl_user table does not exist',
            'portfolios' => []
        ]);
        exit;
    }
    
    // Get the user ID from tbl_user table using the username
    $userSql = "SELECT id, username, firstName, lastName FROM tbl_user WHERE username = :username";
    $userStmt = $conn->prepare($userSql);
    $userStmt->bindParam(':username', $loggedInUsername);
    $userStmt->execute();
    $user = $userStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        echo json_encode([
            'success' => false,
            'message' => 'User not found with username: ' . $loggedInUsername,
            'portfolios' => []
        ]);
        exit;
    }
    
    // Fetch portfolios created by the logged-in user
    // Join with tbl_user to get the creator's username
    $sql = "SELECT 
                p.id,
                p.customer_name,
                p.company_name,
                p.Phone_number,
                p.alternative_Phone_number,
                p.email_id,
                p.state,
                p.location,
                p.sub_location,
                p.pin_code,
                p.customer_type,
                p.industry_type,
                p.business_type,
                p.birth_date,
                p.address,
                p.createdBy,
                p.status,
                p.created_at,
                p.updated_at,
                u.username as creator_username,
                CONCAT(u.firstName, ' ', u.lastName) as creator_full_name
            FROM tbl_portfolio p
            LEFT JOIN tbl_user u ON p.createdBy = u.username
            WHERE p.createdBy = :username
            ORDER BY p.created_at DESC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':username', $loggedInUsername);
    $stmt->execute();
    $portfolios = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total FROM tbl_portfolio WHERE createdBy = :username";
    $countStmt = $conn->prepare($countSql);
    $countStmt->bindParam(':username', $loggedInUsername);
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Get status counts
    $statusCountSql = "SELECT 
                        status,
                        COUNT(*) as count 
                       FROM tbl_portfolio 
                       WHERE createdBy = :username 
                       GROUP BY status";
    $statusStmt = $conn->prepare($statusCountSql);
    $statusStmt->bindParam(':username', $loggedInUsername);
    $statusStmt->execute();
    $statusCounts = $statusStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Portfolios fetched successfully',
        'data' => [
            'user_info' => [
                'id' => $user['id'],
                'username' => $user['username'],
                'full_name' => $user['firstName'] . ' ' . $user['lastName']
            ],
            'portfolios' => $portfolios,
            'statistics' => [
                'total_portfolios' => $totalCount,
                'status_counts' => $statusCounts
            ]
        ],
        'count' => $totalCount
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'portfolios' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'portfolios' => []
    ]);
}
?>
