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
    
    // Check if required tables exist
    $tables = ['tbl_agent_data', 'tbl_user'];
    foreach ($tables as $table) {
        $checkTable = $conn->prepare("SHOW TABLES LIKE '$table'");
        $checkTable->execute();
        $tableExists = $checkTable->fetch();
        
        if (!$tableExists) {
            echo json_encode([
                'success' => false,
                'message' => "$table table does not exist",
                'agents' => []
            ]);
            exit;
        }
    }
    
    // Get the logged-in user's username from request parameters
    $loggedInUsername = isset($_GET['username']) ? $_GET['username'] : null;
    
    if (!$loggedInUsername) {
        echo json_encode([
            'success' => false,
            'message' => 'Username parameter is required',
            'agents' => []
        ]);
        exit;
    }
    
    // Build the main query to fetch agents created by the logged-in user
    $sql = "SELECT 
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
                u.firstName as creator_first_name,
                u.lastName as creator_last_name,
                u.username as creator_username,
                CONCAT(u.firstName, ' ', u.lastName) as creator_full_name
            FROM tbl_agent_data a
            INNER JOIN tbl_user u ON a.createdBy = u.username
            WHERE a.createdBy = :logged_in_username
            ORDER BY a.created_at DESC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':logged_in_username', $loggedInUsername);
    $stmt->execute();
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total 
                 FROM tbl_agent_data a
                 WHERE a.createdBy = :logged_in_username";
    $countStmt = $conn->prepare($countSql);
    $countStmt->bindParam(':logged_in_username', $loggedInUsername);
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Get status counts
    $statusCountSql = "SELECT 
                        a.status,
                        COUNT(*) as count 
                       FROM tbl_agent_data a
                       WHERE a.createdBy = :logged_in_username
                       GROUP BY a.status";
    $statusStmt = $conn->prepare($statusCountSql);
    $statusStmt->bindParam(':logged_in_username', $loggedInUsername);
    $statusStmt->execute();
    $statusCounts = $statusStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get partner type counts
    $partnerTypeCountSql = "SELECT 
                             a.partnerType,
                             COUNT(*) as count 
                            FROM tbl_agent_data a
                            WHERE a.createdBy = :logged_in_username
                            GROUP BY a.partnerType";
    $partnerTypeStmt = $conn->prepare($partnerTypeCountSql);
    $partnerTypeStmt->bindParam(':logged_in_username', $loggedInUsername);
    $partnerTypeStmt->execute();
    $partnerTypeCounts = $partnerTypeStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Director agents fetched successfully',
        'data' => [
            'logged_in_user' => $loggedInUsername,
            'agents' => $agents,
            'statistics' => [
                'total_agents' => $totalCount,
                'status_counts' => $statusCounts,
                'partner_type_counts' => $partnerTypeCounts
            ]
        ],
        'count' => $totalCount
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'agents' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'agents' => []
    ]);
}
?>
