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
    
    // Build the query with LEFT JOINs if tables exist - FILTER FOR INACTIVE USERS (status = 0)
    $sql = "SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.email_id,
                u.mobile,
                u.status,
                u.employee_no,
                u.designation_id,
                u.department_id,
                u.reportingTo,
                u.created_at,
                u.updated_at";
    
    // Add designation name if table exists
    if ($designationTableExists) {
        $sql .= ", d.name as designation_name";
    }
    
    // Add department name if table exists
    if ($departmentTableExists) {
        $sql .= ", dept.name as department_name";
    }
    
    $sql .= " FROM tbl_user u";
    
    // Add LEFT JOINs
    if ($designationTableExists) {
        $sql .= " LEFT JOIN tbl_designation d ON u.designation_id = d.id";
    }
    if ($departmentTableExists) {
        $sql .= " LEFT JOIN tbl_department dept ON u.department_id = dept.id";
    }
    
    // Filter for inactive users (status = 0)
    $sql .= " WHERE u.status = 0";
    
    $sql .= " ORDER BY u.firstName ASC, u.lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
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
            
            // Add designation name if available
            if (isset($row['designation_name'])) {
                $userData['designation'] = $row['designation_name'];
            }
            
            // Add department name if available
            if (isset($row['department_name'])) {
                $userData['department'] = $row['department_name'];
            }
            
            $users[] = $userData;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Inactive users (status = 0) fetched successfully',
        'data' => $users,
        'summary' => [
            'total_inactive_users' => $totalCount
        ],
        'debug' => [
            'designation_table_exists' => $designationTableExists,
            'department_table_exists' => $departmentTableExists,
            'query_used' => $sql
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
        ]
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 