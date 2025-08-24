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

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    // Get search parameters
    $reportingTo = isset($_GET['reportingTo']) ? trim($_GET['reportingTo']) : '';
    $searchQuery = isset($_GET['search']) ? trim($_GET['search']) : '';
    $status = isset($_GET['status']) ? trim($_GET['status']) : '';
    $designation = isset($_GET['designation']) ? trim($_GET['designation']) : '';
    $department = isset($_GET['department']) ? trim($_GET['department']) : '';
    $location = isset($_GET['location']) ? trim($_GET['location']) : '';
    
    if (empty($reportingTo)) {
        throw new Exception('reportingTo parameter is required');
    }
    
    // Build search query
    $whereConditions = ["u.reportingTo = ?"];
    $searchParams = [$reportingTo];
    
    // Add search query condition
    if (!empty($searchQuery)) {
        $whereConditions[] = "(u.firstName LIKE ? OR u.lastName LIKE ? OR u.username LIKE ? OR u.employee_no LIKE ? OR u.mobile LIKE ? OR u.email_id LIKE ?)";
        $searchPattern = "%$searchQuery%";
        $searchParams = array_merge($searchParams, [$searchPattern, $searchPattern, $searchPattern, $searchPattern, $searchPattern, $searchPattern]);
    }
    
    // Add status filter
    if (!empty($status)) {
        $whereConditions[] = "u.status = ?";
        $searchParams[] = $status;
    }
    
    // Add designation filter
    if (!empty($designation)) {
        $whereConditions[] = "d.designation_name LIKE ?";
        $searchParams[] = "%$designation%";
    }
    
    // Add department filter
    if (!empty($department)) {
        $whereConditions[] = "dept.department_name LIKE ?";
        $searchParams[] = "%$department%";
    }
    
    // Add location filter
    if (!empty($location)) {
        $whereConditions[] = "(bs.state_name LIKE ? OR bl.location_name LIKE ?)";
        $searchParams[] = "%$location%";
        $searchParams[] = "%$location%";
    }
    
    // Exclude RBH users from results
    $whereConditions[] = "d.designation_name != 'Regional Business Head'";
    
    $whereClause = implode(' AND ', $whereConditions);
    
    // Main search query
    $searchSql = "
        SELECT 
            u.id,
            u.username,
            u.firstName,
            u.lastName,
            u.mobile,
            u.email_id,
            u.employee_no,
            u.status,
            u.reportingTo,
            u.created_at,
            u.updated_at,
            d.designation_name,
            dept.department_name,
            bs.state_name as branch_state_name,
            bl.location_name as branch_location_name,
            CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, '')) as full_name
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        LEFT JOIN tbl_branch_states bs ON u.branch_state_id = bs.id
        LEFT JOIN tbl_branch_locations bl ON u.branch_location_id = bl.id
        WHERE $whereClause
        ORDER BY u.firstName, u.lastName
        LIMIT 100
    ";
    
    $searchStmt = $conn->prepare($searchSql);
    $searchStmt->execute($searchParams);
    $searchResults = $searchStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get count for pagination
    $countSql = "
        SELECT COUNT(*) as total_count
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        LEFT JOIN tbl_branch_states bs ON u.branch_state_id = bs.id
        LEFT JOIN tbl_branch_locations bl ON u.branch_location_id = bl.id
        WHERE $whereClause
    ";
    
    $countStmt = $conn->prepare($countSql);
    $countStmt->execute($searchParams);
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'message' => 'RBH user search completed successfully',
        'data' => [
            'search_results' => $searchResults,
            'search_parameters' => [
                'search_query' => $searchQuery,
                'status' => $status,
                'designation' => $designation,
                'department' => $department,
                'location' => $location
            ],
            'pagination' => [
                'total_count' => $countResult['total_count'],
                'results_returned' => count($searchResults),
                'limit' => 100
            ]
        ]
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("RBH Search Users Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
