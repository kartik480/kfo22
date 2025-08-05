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
    
    // Test 1: Check all designations
    $sql1 = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name ASC";
    $result1 = $conn->query($sql1);
    
    $allDesignations = array();
    if ($result1 && $result1->num_rows > 0) {
        while($row = $result1->fetch_assoc()) {
            $allDesignations[] = array(
                'id' => $row['id'],
                'designation_name' => $row['designation_name']
            );
        }
    }
    
    // Test 2: Check specific designations we're looking for
    $sql2 = "SELECT id, designation_name FROM tbl_designation 
              WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head')
              ORDER BY designation_name ASC";
    $result2 = $conn->query($sql2);
    
    $targetDesignations = array();
    if ($result2 && $result2->num_rows > 0) {
        while($row = $result2->fetch_assoc()) {
            $targetDesignations[] = array(
                'id' => $row['id'],
                'designation_name' => $row['designation_name']
            );
        }
    }
    
    // Test 3: Check users with these designations
    $sql3 = "SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.designation_id,
                d.designation_name
              FROM tbl_user u
              INNER JOIN tbl_designation d ON u.designation_id = d.id
              WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head')
              AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
              AND u.firstName IS NOT NULL 
              AND u.firstName != ''
              ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    $result3 = $conn->query($sql3);
    
    $usersWithDesignations = array();
    if ($result3 && $result3->num_rows > 0) {
        while($row = $result3->fetch_assoc()) {
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            if (!empty($fullName)) {
                $usersWithDesignations[] = array(
                    'id' => $row['id'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'full_name' => $fullName,
                    'designation_id' => $row['designation_id'],
                    'designation_name' => $row['designation_name']
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Database test completed',
        'all_designations' => $allDesignations,
        'target_designations' => $targetDesignations,
        'users_with_designations' => $usersWithDesignations,
        'all_designations_count' => count($allDesignations),
        'target_designations_count' => count($targetDesignations),
        'users_count' => count($usersWithDesignations)
    ));
    
} catch (Exception $e) {
    error_log("Error in test_designation_table.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to test designation table: ' . $e->getMessage()
    ));
}
?> 