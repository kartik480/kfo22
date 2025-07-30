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
    
    $results = array();
    
    // Test 1: Check if 'Business Head' designation exists
    try {
        $stmt = $pdo->prepare("SELECT id, designation_name FROM tbl_designation WHERE designation_name LIKE '%Business Head%'");
        $stmt->execute();
        $designations = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $designations[] = $row;
        }
        $results['test1_designations'] = $designations;
    } catch (Exception $e) {
        $results['test1_error'] = $e->getMessage();
    }
    
    // Test 2: Check users with designation_id = 15 (Business Head)
    try {
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.status,
                u.designation_id,
                d.designation_name
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.designation_id = 15
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
        ");
        $stmt->execute();
        $users = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $users[] = $row;
        }
        $results['test2_users_by_id'] = $users;
    } catch (Exception $e) {
        $results['test2_error'] = $e->getMessage();
    }
    
    // Test 3: Check users with 'Business Head' designation name
    try {
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.status,
                u.designation_id,
                d.designation_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE d.designation_name = 'Business Head'
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
        ");
        $stmt->execute();
        $users = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $users[] = $row;
        }
        $results['test3_users_by_name'] = $users;
    } catch (Exception $e) {
        $results['test3_error'] = $e->getMessage();
    }
    
    // Test 4: Check users with different status conditions
    try {
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.status,
                u.designation_id,
                d.designation_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE d.designation_name = 'Business Head'
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
        ");
        $stmt->execute();
        $users = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $users[] = $row;
        }
        $results['test4_users_with_status'] = $users;
    } catch (Exception $e) {
        $results['test4_error'] = $e->getMessage();
    }
    
    // Test 5: Check all users with any status
    try {
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.status,
                u.designation_id,
                d.designation_name
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE d.designation_name = 'Business Head'
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
        ");
        $stmt->execute();
        $users = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $users[] = $row;
        }
        $results['test5_all_business_heads'] = $users;
    } catch (Exception $e) {
        $results['test5_error'] = $e->getMessage();
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Business Head users debug completed',
        'data' => $results
    ));
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 