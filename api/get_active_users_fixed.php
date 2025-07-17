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
    
    // First, let's check what columns actually exist in tbl_user
    $columnsQuery = "SHOW COLUMNS FROM tbl_user";
    $columnsResult = $conn->query($columnsQuery);
    
    if ($columnsResult === false) {
        throw new Exception("Failed to get table structure: " . $conn->error);
    }
    
    $availableColumns = array();
    while ($column = $columnsResult->fetch_assoc()) {
        $availableColumns[] = $column['Field'];
    }
    
    // Build SELECT query with only existing columns
    $selectColumns = array();
    
    // Always include these basic columns
    if (in_array('id', $availableColumns)) $selectColumns[] = 'id';
    if (in_array('firstName', $availableColumns)) $selectColumns[] = 'firstName';
    if (in_array('lastName', $availableColumns)) $selectColumns[] = 'lastName';
    if (in_array('username', $availableColumns)) $selectColumns[] = 'username';
    if (in_array('email_id', $availableColumns)) $selectColumns[] = 'email_id';
    if (in_array('mobile', $availableColumns)) $selectColumns[] = 'mobile';
    if (in_array('status', $availableColumns)) $selectColumns[] = 'status';
    if (in_array('designation_id', $availableColumns)) $selectColumns[] = 'designation_id';
    if (in_array('department_id', $availableColumns)) $selectColumns[] = 'department_id';
    if (in_array('reportingTo', $availableColumns)) $selectColumns[] = 'reportingTo';
    if (in_array('employee_no', $availableColumns)) $selectColumns[] = 'employee_no';
    if (in_array('created_at', $availableColumns)) $selectColumns[] = 'created_at';
    if (in_array('updated_at', $availableColumns)) $selectColumns[] = 'updated_at';
    
    // Include permission columns if they exist
    if (in_array('emp_manage_permission', $availableColumns)) $selectColumns[] = 'emp_manage_permission';
    if (in_array('emp_data_permission', $availableColumns)) $selectColumns[] = 'emp_data_permission';
    if (in_array('emp_work_permission', $availableColumns)) $selectColumns[] = 'emp_work_permission';
    if (in_array('payout_permission', $availableColumns)) $selectColumns[] = 'payout_permission';
    
    // If no columns found, use basic ones
    if (empty($selectColumns)) {
        $selectColumns = ['id', 'username'];
    }
    
    $sql = "SELECT " . implode(', ', $selectColumns) . " FROM tbl_user WHERE status = 1 OR status = 'Active' OR status IS NULL OR status = '' ORDER BY ";
    
    // Add ORDER BY clause based on available columns
    if (in_array('firstName', $availableColumns)) {
        $sql .= "firstName ASC, ";
        if (in_array('lastName', $availableColumns)) {
            $sql .= "lastName ASC";
        } else {
            $sql .= "id ASC";
        }
    } else {
        $sql .= "id ASC";
    }
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $users = array();
    $totalCount = 0;
    $activeCount = 0;
    $inactiveCount = 0;
    
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
            
            // Count by status
            $status = isset($row['status']) ? $row['status'] : 'Active';
            if ($status == 'Active' || $status == '1' || empty($status)) {
                $activeCount++;
            } else {
                $inactiveCount++;
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
                'status' => $status,
                'employeeNo' => isset($row['employee_no']) ? $row['employee_no'] : (isset($row['username']) ? $row['username'] : ''),
                'created_at' => isset($row['created_at']) ? $row['created_at'] : '',
                'updated_at' => isset($row['updated_at']) ? $row['updated_at'] : '',
                'emp_manage_permission' => isset($row['emp_manage_permission']) ? $row['emp_manage_permission'] : 'No',
                'emp_data_permission' => isset($row['emp_data_permission']) ? $row['emp_data_permission'] : 'No',
                'emp_work_permission' => isset($row['emp_work_permission']) ? $row['emp_work_permission'] : 'No',
                'payout_permission' => isset($row['payout_permission']) ? $row['payout_permission'] : 'No'
            );
            
            // Add optional columns if they exist
            if (isset($row['designation_id'])) $userData['designation_id'] = $row['designation_id'];
            if (isset($row['department_id'])) $userData['department_id'] = $row['department_id'];
            if (isset($row['reportingTo'])) $userData['reportingTo'] = $row['reportingTo'];
            
            $users[] = $userData;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'All users fetched from tbl_user successfully',
        'data' => $users,
        'summary' => [
            'total_users' => $totalCount,
            'active_users' => $activeCount,
            'inactive_users' => $inactiveCount
        ],
        'debug' => [
            'available_columns' => $availableColumns,
            'selected_columns' => $selectColumns
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_users' => 0,
            'active_users' => 0,
            'inactive_users' => 0
        ]
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 