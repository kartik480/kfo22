<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // First, get the designation IDs for Chief Business Officer and Regional Business Head
    $designationSql = "SELECT id, designation_name FROM tbl_designation 
                       WHERE LOWER(designation_name) LIKE '%chief%' AND LOWER(designation_name) LIKE '%business%' AND LOWER(designation_name) LIKE '%officer%'
                       OR LOWER(designation_name) LIKE '%cbo%'
                       OR (LOWER(designation_name) LIKE '%regional%' AND LOWER(designation_name) LIKE '%business%' AND LOWER(designation_name) LIKE '%head%')
                       OR LOWER(designation_name) LIKE '%rbh%'
                       ORDER BY designation_name ASC";
    
    $designationResult = $conn->query($designationSql);
    
    if ($designationResult === false) {
        throw new Exception("Designation query failed: " . $conn->error);
    }
    
    $designationIds = array();
    while ($row = $designationResult->fetch_assoc()) {
        $designationIds[] = $row['id'];
        error_log("Found designation: " . $row['designation_name'] . " (ID: " . $row['id'] . ")");
    }
    
    if (empty($designationIds)) {
        throw new Exception("No CBO or RBH designations found");
    }
    
    // Now get users with these designation IDs
    $designationIdsStr = implode(',', $designationIds);
    $sql = "SELECT 
                u.id, u.username, u.firstName, u.lastName, 
                CONCAT(u.firstName, ' ', u.lastName) AS fullName,
                u.email_id, u.mobile, u.employee_no, u.status,
                d.designation_name, dept.department_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            LEFT JOIN tbl_department dept ON u.department_id = dept.id
            WHERE u.designation_id IN ($designationIdsStr)
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL AND u.firstName != ''
            ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    
    error_log("SQL Query: " . $sql);
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $users = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $fullName = trim($row['fullName']);
            if (!empty($fullName)) {
                $users[] = array(
                    'id' => $row['id'],
                    'username' => $row['username'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'fullName' => $fullName,
                    'email_id' => $row['email_id'],
                    'mobile' => $row['mobile'],
                    'employee_no' => $row['employee_no'],
                    'designation_name' => $row['designation_name'],
                    'department_name' => $row['department_name'],
                    'status' => $row['status']
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $users,
        'message' => 'Chief Business Officer and Regional Business Head users fetched successfully',
        'count' => count($users),
        'debug' => array(
            'designation_ids_found' => $designationIds,
            'sql_used' => $sql,
            'note' => 'Users with CBO and RBH designations for Director Partner Team dropdown'
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_director_partner_team_dropdown.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch users: ' . $e->getMessage(),
        'debug' => array(
            'error_details' => $e->getMessage(),
            'file' => __FILE__,
            'line' => __LINE__
        )
    ));
}
?> 