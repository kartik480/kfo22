<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    
    // Step 1: Check if we can connect to the database
    if (!$conn || !$conn->ping()) {
        throw new Exception("Database connection failed");
    }
    
    // Step 2: Check what designations exist
    $designation_sql = "SELECT id, designation_name FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')";
    $designation_result = $conn->query($designation_sql);
    
    $designations = [];
    if ($designation_result && $designation_result->num_rows > 0) {
        while ($row = $designation_result->fetch_assoc()) {
            $designations[] = $row;
        }
    }
    
    // Step 3: Check if there are any users with these designations
    $user_ids = [];
    foreach ($designations as $designation) {
        $user_ids[] = $designation['id'];
    }
    
    $users_with_designations = [];
    if (!empty($user_ids)) {
        $user_ids_str = implode(',', $user_ids);
        $users_sql = "SELECT id, firstName, lastName, designation_id FROM tbl_user WHERE designation_id IN ($user_ids_str)";
        $users_result = $conn->query($users_sql);
        
        if ($users_result && $users_result->num_rows > 0) {
            while ($row = $users_result->fetch_assoc()) {
                $users_with_designations[] = $row;
            }
        }
    }
    
    // Step 4: Check if there are any partner users created by these users
    $partner_users = [];
    if (!empty($users_with_designations)) {
        $creator_ids = [];
        foreach ($users_with_designations as $user) {
            $creator_ids[] = $user['id'];
        }
        
        if (!empty($creator_ids)) {
            $creator_ids_str = implode(',', $creator_ids);
            $partners_sql = "SELECT id, username, first_name, last_name, createdBy FROM tbl_partner_users WHERE createdBy IN ($creator_ids_str)";
            $partners_result = $conn->query($partners_sql);
            
            if ($partners_result && $partners_result->num_rows > 0) {
                while ($row = $partners_result->fetch_assoc()) {
                    $partner_users[] = $row;
                }
            }
        }
    }
    
    // Step 5: Try the original query with error handling
    $original_query_result = null;
    $original_query_error = null;
    
    try {
        $sql = "SELECT 
                    pu.id,
                    pu.username,
                    pu.first_name,
                    pu.last_name,
                    pu.createdBy,
                    CONCAT(creator.firstName, ' ', creator.lastName) AS creator_name,
                    creator.designation_id AS creator_designation_id,
                    d.designation_name AS creator_designation_name
                FROM tbl_partner_users pu
                LEFT JOIN tbl_user creator ON pu.createdBy = creator.id
                LEFT JOIN tbl_designation d ON creator.designation_id = d.id
                WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
                ORDER BY pu.id DESC";
        
        $original_query_result = $conn->query($sql);
        
        if (!$original_query_result) {
            $original_query_error = $conn->error;
        }
    } catch (Exception $e) {
        $original_query_error = $e->getMessage();
    }
    
    echo json_encode([
        'status' => 'success',
        'debug_info' => [
            'database_connection' => 'OK',
            'designations_found' => $designations,
            'users_with_designations' => $users_with_designations,
            'partner_users_created_by_these_users' => $partner_users,
            'original_query_error' => $original_query_error,
            'total_designations' => count($designations),
            'total_users_with_designations' => count($users_with_designations),
            'total_partner_users' => count($partner_users)
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 