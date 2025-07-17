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
    
    // Get the designation IDs for our target designations
    $designation_sql = "SELECT id FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')";
    $designation_result = $conn->query($designation_sql);
    
    $target_designation_ids = array();
    if ($designation_result && $designation_result->num_rows > 0) {
        while($row = $designation_result->fetch_assoc()) {
            $target_designation_ids[] = $row['id'];
        }
    }
    
    if (empty($target_designation_ids)) {
        throw new Exception("No target designations found");
    }
    
    $designation_ids_str = implode(',', $target_designation_ids);
    
    // Query to fetch all SDSA users who report to users with target designations
    $sql = "SELECT 
                s.id,
                s.first_name,
                s.last_name,
                s.Phone_number as mobile,
                s.email_id,
                s.reportingTo,
                u.firstName as manager_first_name,
                u.lastName as manager_last_name,
                d.designation_name as manager_designation
            FROM tbl_sdsa_users s
            LEFT JOIN tbl_user u ON s.reportingTo = u.id
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.designation_id IN ($designation_ids_str)
            ORDER BY s.first_name ASC";
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $data = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $data[] = array(
                'id' => $row['id'],
                'fullName' => $row['first_name'] . ' ' . $row['last_name'],
                'mobile' => $row['mobile'],
                'email' => $row['email_id'],
                'reportingTo' => $row['reportingTo'],
                'managerName' => $row['manager_first_name'] . ' ' . $row['manager_last_name'],
                'managerDesignation' => $row['manager_designation']
            );
        }
        
        echo json_encode(array(
            'status' => 'success',
            'message' => 'All SDSA team members fetched successfully',
            'data' => $data,
            'count' => count($data)
        ));
    } else {
        echo json_encode(array(
            'status' => 'success',
            'message' => 'No SDSA team members found',
            'data' => array(),
            'count' => 0
        ));
    }
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 