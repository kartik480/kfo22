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
    
    // Fetch all users from tbl_user table for Active Emp List
    // Focus on firstName and lastName columns as requested
    $sql = "SELECT 
                id,
                firstName,
                lastName,
                username,
                email_id,
                mobile,
                status,
                designation,
                department,
                reportingTo,
                created_at,
                updated_at
            FROM tbl_user 
            ORDER BY firstName ASC, lastName ASC";
    
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
            $firstName = $row['firstName'] ? $row['firstName'] : '';
            $lastName = $row['lastName'] ? $row['lastName'] : '';
            
            // Create full name by combining firstName and lastName
            $fullName = trim($firstName . ' ' . $lastName);
            
            // If full name is empty, use username as fallback
            if (empty($fullName)) {
                $fullName = $row['username'] ? $row['username'] : 'Unknown User';
            }
            
            // Count by status
            $status = $row['status'] ? $row['status'] : 'Active';
            if ($status == 'Active' || $status == '1' || empty($status)) {
                $activeCount++;
            } else {
                $inactiveCount++;
            }
            $totalCount++;
            
            $users[] = array(
                'id' => $row['id'],
                'firstName' => $firstName,
                'lastName' => $lastName,
                'fullName' => $fullName,
                'username' => $row['username'],
                'email' => $row['email_id'],
                'mobile' => $row['mobile'],
                'status' => $status,
                'designation' => $row['designation'],
                'department' => $row['department'],
                'reportingTo' => $row['reportingTo'],
                'employeeNo' => $row['username'], // Using username as employee number
                'created_at' => $row['created_at'],
                'updated_at' => $row['updated_at']
            );
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Active users list fetched successfully',
        'data' => $users,
        'summary' => [
            'total_users' => $totalCount,
            'active_users' => $activeCount,
            'inactive_users' => $inactiveCount
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