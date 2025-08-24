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
    
    // Get export parameters
    $reportingTo = isset($_GET['reportingTo']) ? trim($_GET['reportingTo']) : '';
    $exportFormat = isset($_GET['format']) ? strtolower(trim($_GET['format'])) : 'csv';
    $status = isset($_GET['status']) ? trim($_GET['status']) : '';
    
    if (empty($reportingTo)) {
        throw new Exception('reportingTo parameter is required');
    }
    
    if (!in_array($exportFormat, ['csv', 'json'])) {
        throw new Exception('Invalid export format. Supported: csv, json');
    }
    
    // Build query conditions
    $whereConditions = ["u.reportingTo = ?"];
    $queryParams = [$reportingTo];
    
    if (!empty($status)) {
        $whereConditions[] = "u.status = ?";
        $queryParams[] = $status;
    }
    
    // Exclude RBH users from results
    $whereConditions[] = "d.designation_name != 'Regional Business Head'";
    
    $whereClause = implode(' AND ', $whereConditions);
    
    // Fetch user data
    $exportSql = "
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
    ";
    
    $exportStmt = $conn->prepare($exportSql);
    $exportStmt->execute($queryParams);
    $userData = $exportStmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($userData)) {
        throw new Exception('No users found for export');
    }
    
    // Generate export based on format
    if ($exportFormat === 'csv') {
        // Generate CSV content
        $csvContent = generateCSV($userData);
        
        $response = [
            'status' => 'success',
            'message' => 'CSV export generated successfully',
            'data' => [
                'format' => 'csv',
                'total_records' => count($userData),
                'csv_content' => $csvContent,
                'filename' => 'rbh_users_' . date('Y-m-d_H-i-s') . '.csv'
            ]
        ];
        
    } else {
        // JSON format
        $response = [
            'status' => 'success',
            'message' => 'JSON export generated successfully',
            'data' => [
                'format' => 'json',
                'total_records' => count($userData),
                'export_data' => $userData,
                'filename' => 'rbh_users_' . date('Y-m-d_H-i-s') . '.json'
            ]
        ];
    }
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    error_log("RBH Export User List Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}

/**
 * Generate CSV content from user data
 */
function generateCSV($userData) {
    if (empty($userData)) {
        return '';
    }
    
    // CSV headers
    $headers = [
        'ID', 'Username', 'First Name', 'Last Name', 'Full Name',
        'Mobile', 'Email', 'Employee No', 'Status', 'Reporting To',
        'Designation', 'Department', 'Branch State', 'Branch Location',
        'Created At', 'Updated At'
    ];
    
    $csvContent = implode(',', $headers) . "\n";
    
    // CSV data rows
    foreach ($userData as $user) {
        $row = [
            $user['id'] ?? '',
            $user['username'] ?? '',
            $user['firstName'] ?? '',
            $user['lastName'] ?? '',
            $user['full_name'] ?? '',
            $user['mobile'] ?? '',
            $user['email_id'] ?? '',
            $user['employee_no'] ?? '',
            $user['status'] ?? '',
            $user['reportingTo'] ?? '',
            $user['designation_name'] ?? '',
            $user['department_name'] ?? '',
            $user['branch_state_name'] ?? '',
            $user['branch_location_name'] ?? '',
            $user['created_at'] ?? '',
            $user['updated_at'] ?? ''
        ];
        
        // Escape CSV values
        $escapedRow = array_map(function($value) {
            if (strpos($value, ',') !== false || strpos($value, '"') !== false || strpos($value, "\n") !== false) {
                return '"' . str_replace('"', '""', $value) . '"';
            }
            return $value;
        }, $row);
        
        $csvContent .= implode(',', $escapedRow) . "\n";
    }
    
    return $csvContent;
}
?>
