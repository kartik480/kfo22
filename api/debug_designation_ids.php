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
    
    // First, get the designation IDs for our target designations
    $designation_sql = "SELECT id, designation_name FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')";
    $designation_result = $conn->query($designation_sql);
    
    $target_designation_ids = array();
    if ($designation_result && $designation_result->num_rows > 0) {
        while($row = $designation_result->fetch_assoc()) {
            $target_designation_ids[] = $row;
        }
    }
    
    // Now get all users with these designation IDs directly
    $user_ids = array();
    foreach ($target_designation_ids as $designation) {
        $user_ids[] = $designation['id'];
    }
    
    $user_ids_str = implode(',', $user_ids);
    
    // Get all users with these designation IDs (any status)
    $all_users_sql = "SELECT 
                        u.id,
                        u.firstName,
                        u.lastName,
                        u.designation_id,
                        u.status,
                        d.designation_name
                    FROM tbl_user u
                    LEFT JOIN tbl_designation d ON u.designation_id = d.id
                    WHERE u.designation_id IN ($user_ids_str)
                    ORDER BY u.designation_id, u.firstName ASC";
    
    $all_users_result = $conn->query($all_users_sql);
    $all_users = array();
    
    if ($all_users_result && $all_users_result->num_rows > 0) {
        while($row = $all_users_result->fetch_assoc()) {
            $all_users[] = $row;
        }
    }
    
    // Get active users with these designation IDs
    $active_users_sql = "SELECT 
                            u.id,
                            u.firstName,
                            u.lastName,
                            u.designation_id,
                            u.status,
                            d.designation_name
                        FROM tbl_user u
                        LEFT JOIN tbl_designation d ON u.designation_id = d.id
                        WHERE u.designation_id IN ($user_ids_str)
                        AND u.status = 'Active'
                        ORDER BY u.designation_id, u.firstName ASC";
    
    $active_users_result = $conn->query($active_users_sql);
    $active_users = array();
    
    if ($active_users_result && $active_users_result->num_rows > 0) {
        while($row = $active_users_result->fetch_assoc()) {
            $active_users[] = $row;
        }
    }
    
    // Count by designation ID
    $designation_id_counts = array();
    foreach ($all_users as $user) {
        $designation_id = $user['designation_id'];
        if (!isset($designation_id_counts[$designation_id])) {
            $designation_id_counts[$designation_id] = array(
                'count' => 0,
                'active_count' => 0,
                'designation_name' => $user['designation_name']
            );
        }
        $designation_id_counts[$designation_id]['count']++;
        if ($user['status'] === 'Active') {
            $designation_id_counts[$designation_id]['active_count']++;
        }
    }
    
    // Get all unique designation IDs from tbl_user to see what's there
    $all_designation_ids_sql = "SELECT DISTINCT designation_id, COUNT(*) as user_count FROM tbl_user GROUP BY designation_id ORDER BY designation_id";
    $all_designation_ids_result = $conn->query($all_designation_ids_sql);
    $all_designation_ids = array();
    
    if ($all_designation_ids_result && $all_designation_ids_result->num_rows > 0) {
        while($row = $all_designation_ids_result->fetch_assoc()) {
            $all_designation_ids[] = $row;
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Designation ID analysis completed',
        'data' => array(
            'target_designation_ids' => $target_designation_ids,
            'all_users_with_target_ids' => $all_users,
            'active_users_with_target_ids' => $active_users,
            'designation_id_counts' => $designation_id_counts,
            'all_designation_ids_in_user_table' => $all_designation_ids,
            'total_users_with_target_ids' => count($all_users),
            'total_active_users_with_target_ids' => count($active_users)
        )
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 