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
    
    // First, let's see all designations in the database
    $designation_sql = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name";
    $designation_result = $conn->query($designation_sql);
    
    $all_designations = array();
    if ($designation_result && $designation_result->num_rows > 0) {
        while($row = $designation_result->fetch_assoc()) {
            $all_designations[] = $row;
        }
    }
    
    // Now let's see all users with their designations
    $user_sql = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.designation_id,
                    u.status,
                    d.designation_name
                FROM tbl_user u
                LEFT JOIN tbl_designation d ON u.designation_id = d.id
                WHERE u.status = 'Active'
                ORDER BY u.firstName ASC";
    
    $user_result = $conn->query($user_sql);
    
    $all_users = array();
    if ($user_result && $user_result->num_rows > 0) {
        while($row = $user_result->fetch_assoc()) {
            $all_users[] = $row;
        }
    }
    
    // Now let's check for users with our specific designations
    $specific_sql = "SELECT 
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
    
    $specific_result = $conn->query($specific_sql);
    
    $specific_users = array();
    if ($specific_result && $specific_result->num_rows > 0) {
        while($row = $specific_result->fetch_assoc()) {
            $specific_users[] = $row;
        }
    }
    
    // Let's also check for partial matches
    $partial_sql = "SELECT 
                        u.id,
                        u.firstName,
                        u.lastName,
                        u.designation_id,
                        d.designation_name
                    FROM tbl_user u
                    LEFT JOIN tbl_designation d ON u.designation_id = d.id
                    WHERE (d.designation_name LIKE '%Chief%' 
                           OR d.designation_name LIKE '%Business%' 
                           OR d.designation_name LIKE '%Director%'
                           OR d.designation_name LIKE '%Regional%'
                           OR d.designation_name LIKE '%Head%')
                    AND u.status = 'Active'
                    ORDER BY u.firstName ASC";
    
    $partial_result = $conn->query($partial_sql);
    
    $partial_users = array();
    if ($partial_result && $partial_result->num_rows > 0) {
        while($row = $partial_result->fetch_assoc()) {
            $partial_users[] = $row;
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Debug information retrieved',
        'data' => array(
            'all_designations' => $all_designations,
            'all_users_count' => count($all_users),
            'specific_users' => $specific_users,
            'partial_users' => $partial_users,
            'designation_count' => count($all_designations),
            'specific_count' => count($specific_users),
            'partial_count' => count($partial_users)
        )
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 