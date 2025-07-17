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
    
    // Get all users with their designations
    $all_users_sql = "SELECT 
                        u.id,
                        u.firstName,
                        u.lastName,
                        u.designation_id,
                        u.status,
                        d.designation_name
                    FROM tbl_user u
                    LEFT JOIN tbl_designation d ON u.designation_id = d.id
                    ORDER BY u.firstName ASC";
    
    $all_users_result = $conn->query($all_users_sql);
    $all_users = array();
    
    if ($all_users_result && $all_users_result->num_rows > 0) {
        while($row = $all_users_result->fetch_assoc()) {
            $all_users[] = $row;
        }
    }
    
    // Get users with our target designations (regardless of status)
    $target_designations_sql = "SELECT 
                                    u.id,
                                    u.firstName,
                                    u.lastName,
                                    u.designation_id,
                                    u.status,
                                    d.designation_name
                                FROM tbl_user u
                                INNER JOIN tbl_designation d ON u.designation_id = d.id
                                WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
                                ORDER BY u.firstName ASC";
    
    $target_result = $conn->query($target_designations_sql);
    $target_users = array();
    
    if ($target_result && $target_result->num_rows > 0) {
        while($row = $target_result->fetch_assoc()) {
            $target_users[] = $row;
        }
    }
    
    // Get active users with target designations
    $active_target_sql = "SELECT 
                            u.id,
                            u.firstName,
                            u.lastName,
                            u.designation_id,
                            u.status,
                            d.designation_name
                        FROM tbl_user u
                        INNER JOIN tbl_designation d ON u.designation_id = d.id
                        WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
                        AND u.status = 'Active'
                        ORDER BY u.firstName ASC";
    
    $active_target_result = $conn->query($active_target_sql);
    $active_target_users = array();
    
    if ($active_target_result && $active_target_result->num_rows > 0) {
        while($row = $active_target_result->fetch_assoc()) {
            $active_target_users[] = $row;
        }
    }
    
    // Count users by status
    $status_counts = array();
    foreach ($all_users as $user) {
        $status = $user['status'] ?: 'NULL';
        if (!isset($status_counts[$status])) {
            $status_counts[$status] = 0;
        }
        $status_counts[$status]++;
    }
    
    // Count users by designation
    $designation_counts = array();
    foreach ($all_users as $user) {
        $designation = $user['designation_name'] ?: 'NULL';
        if (!isset($designation_counts[$designation])) {
            $designation_counts[$designation] = 0;
        }
        $designation_counts[$designation]++;
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Comprehensive user analysis completed',
        'data' => array(
            'total_users' => count($all_users),
            'target_designation_users' => count($target_users),
            'active_target_users' => count($active_target_users),
            'status_counts' => $status_counts,
            'designation_counts' => $designation_counts,
            'all_users' => $all_users,
            'target_users_all_status' => $target_users,
            'active_target_users_only' => $active_target_users
        )
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 