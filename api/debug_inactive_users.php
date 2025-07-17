<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration for kfinone database
$db_host = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_name = "emp_kfinone";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";

try {
    // Create connection
    $conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);
    
    if (!$conn) {
        throw new Exception('Connection failed: ' . mysqli_connect_error());
    }
    
    // Check if tbl_user table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('tbl_user table does not exist');
    }
    
    // Check if designation and department tables exist
    $designationTableExists = $conn->query("SHOW TABLES LIKE 'tbl_designation'")->num_rows > 0;
    $departmentTableExists = $conn->query("SHOW TABLES LIKE 'tbl_department'")->num_rows > 0;
    
    // First, let's check what status values exist in the table
    $statusCheckQuery = "SELECT DISTINCT status, COUNT(*) as count FROM tbl_user GROUP BY status";
    $statusResult = $conn->query($statusCheckQuery);
    
    $statusCounts = array();
    if ($statusResult && $statusResult->num_rows > 0) {
        while ($row = $statusResult->fetch_assoc()) {
            $statusCounts[] = array(
                'status' => $row['status'],
                'count' => $row['count']
            );
        }
    }
    
    // Build a simple query first to test
    $sql = "SELECT 
                id,
                firstName,
                lastName,
                username,
                email_id,
                mobile,
                status,
                employee_no,
                designation_id,
                department_id,
                reportingTo,
                created_at,
                updated_at
            FROM tbl_user 
            WHERE status = 0";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error . " | SQL: " . $sql);
    }
    
    $users = array();
    $totalCount = 0;
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Handle firstName and lastName as requested
            $firstName = isset($row['firstName']) ? $row['firstName'] : '';
            $lastName = isset($row['lastName']) ? $row['lastName'] : '';
            
            // Create full name by combining firstName and lastName
            $fullName = trim($firstName . ' ' . $lastName);
            
            // If full name is empty, use username as fallback
            if (empty($fullName)) {
                $fullName = isset($row['username']) ? $row['username'] : 'Unknown User';
            }
            
            $totalCount++;
            
            $userData = array(
                'id' => isset($row['id']) ? $row['id'] : '',
                'firstName' => $firstName,
                'lastName' => $lastName,
                'fullName' => $fullName,
                'username' => isset($row['username']) ? $row['username'] : '',
                'email' => isset($row['email_id']) ? $row['email_id'] : '',
                'mobile' => isset($row['mobile']) ? $row['mobile'] : '',
                'status' => isset($row['status']) ? $row['status'] : '0',
                'employeeNo' => isset($row['employee_no']) ? $row['employee_no'] : (isset($row['username']) ? $row['username'] : ''),
                'designation_id' => isset($row['designation_id']) ? $row['designation_id'] : '',
                'department_id' => isset($row['department_id']) ? $row['department_id'] : '',
                'reportingTo' => isset($row['reportingTo']) ? $row['reportingTo'] : '',
                'created_at' => isset($row['created_at']) ? $row['created_at'] : '',
                'updated_at' => isset($row['updated_at']) ? $row['updated_at'] : ''
            );
            
            $users[] = $userData;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Debug: Inactive users (status = 0) fetched successfully',
        'data' => $users,
        'summary' => [
            'total_inactive_users' => $totalCount
        ],
        'debug' => [
            'designation_table_exists' => $designationTableExists,
            'department_table_exists' => $departmentTableExists,
            'status_counts_in_table' => $statusCounts,
            'query_used' => $sql,
            'connection_successful' => true
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_inactive_users' => 0
        ],
        'debug' => [
            'error_details' => $e->getMessage(),
            'connection_successful' => isset($conn) ? true : false
        ]
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 