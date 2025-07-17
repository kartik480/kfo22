<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Use the existing connection from db_config.php
    global $conn;
    
    // Check if connection exists and is valid
    if (!$conn || !$conn->ping()) {
        throw new Exception("Database connection not available");
    }
    
    // Check for exact designation names
    $exact_designations = array('Chief Business Officer', 'Regional Business Head', 'Director');
    $found_designations = array();
    $not_found = array();
    
    foreach ($exact_designations as $designation) {
        $sql = "SELECT id, designation_name FROM tbl_designation WHERE designation_name = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $designation);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $found_designations[] = $row;
        } else {
            $not_found[] = $designation;
        }
    }
    
    // Now get users with these exact designations
    $users_sql = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.designation_id,
                    d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
                AND u.status = 'Active'
                ORDER BY u.firstName ASC";
    
    $users_result = $conn->query($users_sql);
    $users = array();
    
    if ($users_result && $users_result->num_rows > 0) {
        while($row = $users_result->fetch_assoc()) {
            $users[] = $row;
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Exact designation check completed',
        'data' => array(
            'found_designations' => $found_designations,
            'not_found_designations' => $not_found,
            'users_with_exact_designations' => $users,
            'total_users_found' => count($users)
        )
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 