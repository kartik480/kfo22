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
    $tables = ['tbl_portfolio', 'tbl_user', 'tbl_designation'];
    foreach ($tables as $table) {
        $checkTable = $conn->prepare("SHOW TABLES LIKE '$table'");
        $checkTable->execute();
        $tableExists = $checkTable->fetch();
        
        if (!$tableExists) {
            echo json_encode([
                'success' => false,
                'message' => "$table table does not exist",
                'portfolios' => []
            ]);
            exit;
        }
    }
    
    // First, get the designation ID for "Regional Business Head"
    $designationSql = "SELECT id FROM tbl_designation WHERE designation_name = 'Regional Business Head'";
    $designationStmt = $conn->prepare($designationSql);
    $designationStmt->execute();
    $designation = $designationStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$designation) {
        echo json_encode([
            'success' => false,
            'message' => 'Regional Business Head designation not found',
            'portfolios' => []
        ]);
        exit;
    }
    
    $rbhDesignationId = $designation['id'];
    
    // Get the logged-in user's username from request parameters (optional)
    $loggedInUsername = isset($_GET['username']) ? $_GET['username'] : null;
    
    // Build the main query to fetch portfolios created by RBH users
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
                u.firstName as creator_first_name,
                u.lastName as creator_last_name,
                u.designation_id as creator_designation_id,
                d.designation_name as creator_designation_name,
                CONCAT(u.firstName, ' ', u.lastName) as creator_full_name
            FROM tbl_portfolio p
            INNER JOIN tbl_user u ON p.createdBy = u.username
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.designation_id = :rbh_designation_id
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            ORDER BY p.created_at DESC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':rbh_designation_id', $rbhDesignationId);
    $stmt->execute();
    $portfolios = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total 
                 FROM tbl_portfolio p
                 INNER JOIN tbl_user u ON p.createdBy = u.username
                 WHERE u.designation_id = :rbh_designation_id
                 AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')";
    $countStmt = $conn->prepare($countSql);
    $countStmt->bindParam(':rbh_designation_id', $rbhDesignationId);
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Get status counts
    $statusCountSql = "SELECT 
                        p.status,
                        COUNT(*) as count 
                       FROM tbl_portfolio p
                       INNER JOIN tbl_user u ON p.createdBy = u.username
                       WHERE u.designation_id = :rbh_designation_id
                       AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                       GROUP BY p.status";
    $statusStmt = $conn->prepare($statusCountSql);
    $statusStmt->bindParam(':rbh_designation_id', $rbhDesignationId);
    $statusStmt->execute();
    $statusCounts = $statusStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get unique RBH creators
    $creatorsSql = "SELECT DISTINCT 
                        u.username,
                        u.firstName,
                        u.lastName,
                        u.designation_id,
                        d.designation_name,
                        CONCAT(u.firstName, ' ', u.lastName) as full_name,
                        COUNT(p.id) as portfolio_count
                    FROM tbl_user u
                    INNER JOIN tbl_designation d ON u.designation_id = d.id
                    LEFT JOIN tbl_portfolio p ON u.username = p.createdBy
                    WHERE u.designation_id = :rbh_designation_id
                    AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                    GROUP BY u.id, u.username, u.firstName, u.lastName, u.designation_id, d.designation_name
                    ORDER BY u.firstName ASC, u.lastName ASC";
    $creatorsStmt = $conn->prepare($creatorsSql);
    $creatorsStmt->bindParam(':rbh_designation_id', $rbhDesignationId);
    $creatorsStmt->execute();
    $creators = $creatorsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Portfolios created by Regional Business Head users fetched successfully',
        'data' => [
            'designation_info' => [
                'id' => $rbhDesignationId,
                'name' => 'Regional Business Head'
            ],
            'portfolios' => $portfolios,
            'creators' => $creators,
            'statistics' => [
                'total_portfolios' => $totalCount,
                'status_counts' => $statusCounts,
                'total_rbh_creators' => count($creators)
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
