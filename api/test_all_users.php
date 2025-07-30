<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get all users with their designations
    $stmt = $pdo->query("
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            u.username,
            u.employee_no,
            u.status,
            u.designation_id,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE u.firstName IS NOT NULL 
        AND u.firstName != ''
        ORDER BY u.firstName ASC, u.lastName ASC
        LIMIT 50
    ");
    
    $users = array();
    while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $users[] = array(
            'id' => $row['id'],
            'firstName' => $row['firstName'],
            'lastName' => $row['lastName'],
            'username' => $row['username'],
            'employee_no' => $row['employee_no'],
            'status' => $row['status'],
            'designation_id' => $row['designation_id'],
            'designation_name' => $row['designation_name'],
            'fullName' => $row['fullName']
        );
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Users fetched successfully',
        'data' => $users,
        'count' => count($users)
    ));
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 