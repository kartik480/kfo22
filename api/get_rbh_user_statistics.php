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
    
    // Get the reportingTo value from GET parameter
    $reportingTo = isset($_GET['reportingTo']) ? trim($_GET['reportingTo']) : '';
    
    if (empty($reportingTo)) {
        throw new Exception('reportingTo parameter is required');
    }
    
    // Get comprehensive statistics
    $statsSql = "
        SELECT 
            COUNT(*) as total_users,
            COUNT(CASE WHEN u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '' THEN 1 END) as active_users,
            COUNT(CASE WHEN u.status = 'Inactive' THEN 1 END) as inactive_users,
            COUNT(CASE WHEN u.status = 'Suspended' THEN 1 END) as suspended_users,
            COUNT(CASE WHEN u.status = 'Terminated' THEN 1 END) as terminated_users,
            COUNT(CASE WHEN u.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as new_users_30_days,
            COUNT(CASE WHEN u.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 END) as new_users_7_days,
            COUNT(CASE WHEN u.updated_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 END) as updated_users_7_days
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ? 
        AND d.designation_name != 'Regional Business Head'
    ";
    
    $statsStmt = $conn->prepare($statsSql);
    $statsStmt->execute([$reportingTo]);
    $basicStats = $statsStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get designation-wise statistics
    $designationStatsSql = "
        SELECT 
            d.designation_name,
            COUNT(*) as user_count
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ? 
        AND d.designation_name != 'Regional Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        GROUP BY d.designation_name
        ORDER BY user_count DESC
        LIMIT 10
    ";
    
    $designationStatsStmt = $conn->prepare($designationStatsSql);
    $designationStatsStmt->execute([$reportingTo]);
    $designationStats = $designationStatsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get department-wise statistics
    $departmentStatsSql = "
        SELECT 
            COALESCE(dept.department_name, 'Not Assigned') as department_name,
            COUNT(*) as user_count
        FROM tbl_user u
        LEFT JOIN tbl_department dept ON u.department_id = dept.id
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ? 
        AND d.designation_name != 'Regional Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        GROUP BY dept.department_name
        ORDER BY user_count DESC
        LIMIT 10
    ";
    
    $departmentStatsStmt = $conn->prepare($departmentStatsSql);
    $departmentStatsStmt->execute([$reportingTo]);
    $departmentStats = $departmentStatsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get location-wise statistics
    $locationStatsSql = "
        SELECT 
            COALESCE(bs.state_name, 'Not Assigned') as state_name,
            COUNT(*) as user_count
        FROM tbl_user u
        LEFT JOIN tbl_branch_states bs ON u.branch_state_id = bs.id
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ? 
        AND d.designation_name != 'Regional Business Head'
        AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        GROUP BY bs.state_name
        ORDER BY user_count DESC
        LIMIT 10
    ";
    
    $locationStatsStmt = $conn->prepare($locationStatsSql);
    $locationStatsStmt->execute([$reportingTo]);
    $locationStats = $locationStatsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get monthly growth data (last 12 months)
    $monthlyGrowthSql = "
        SELECT 
            DATE_FORMAT(u.created_at, '%Y-%m') as month,
            COUNT(*) as new_users
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.reportingTo = ? 
        AND d.designation_name != 'Regional Business Head'
        AND u.created_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
        GROUP BY DATE_FORMAT(u.created_at, '%Y-%m')
        ORDER BY month ASC
    ";
    
    $monthlyGrowthStmt = $conn->prepare($monthlyGrowthSql);
    $monthlyGrowthStmt->execute([$reportingTo]);
    $monthlyGrowth = $monthlyGrowthStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Calculate growth percentage
    $currentMonth = date('Y-m');
    $previousMonth = date('Y-m', strtotime('-1 month'));
    
    $currentMonthCount = 0;
    $previousMonthCount = 0;
    
    foreach ($monthlyGrowth as $monthData) {
        if ($monthData['month'] === $currentMonth) {
            $currentMonthCount = $monthData['new_users'];
        } elseif ($monthData['month'] === $previousMonth) {
            $previousMonthCount = $monthData['new_users'];
        }
    }
    
    $growthPercentage = 0;
    if ($previousMonthCount > 0) {
        $growthPercentage = round((($currentMonthCount - $previousMonthCount) / $previousMonthCount) * 100, 2);
    }
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'RBH User Statistics fetched successfully',
        'data' => [
            'basic_statistics' => [
                'total_users' => (int)$basicStats['total_users'],
                'active_users' => (int)$basicStats['active_users'],
                'inactive_users' => (int)$basicStats['inactive_users'],
                'suspended_users' => (int)$basicStats['suspended_users'],
                'terminated_users' => (int)$basicStats['terminated_users'],
                'new_users_30_days' => (int)$basicStats['new_users_30_days'],
                'new_users_7_days' => (int)$basicStats['new_users_7_days'],
                'updated_users_7_days' => (int)$basicStats['updated_users_7_days']
            ],
            'designation_statistics' => $designationStats,
            'department_statistics' => $departmentStats,
            'location_statistics' => $locationStats,
            'monthly_growth' => $monthlyGrowth,
            'growth_metrics' => [
                'current_month_users' => $currentMonthCount,
                'previous_month_users' => $previousMonthCount,
                'growth_percentage' => $growthPercentage,
                'growth_trend' => $growthPercentage >= 0 ? 'positive' : 'negative'
            ]
        ],
        'generated_at' => date('Y-m-d H:i:s')
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("RBH User Statistics Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
