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
    
    // First, let's get all designations to see what's available
    $designation_sql = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name";
    $designation_result = $conn->query($designation_sql);
    
    $all_designations = array();
    if ($designation_result && $designation_result->num_rows > 0) {
        while($row = $designation_result->fetch_assoc()) {
            $all_designations[] = $row;
        }
    }
    
    // Query to fetch users with specific designations for dropdown
    // Using exact designation names: Chief Business Officer, Regional Business Head, Director
    // Including ALL users regardless of status to see all available users
    $sql = "SELECT 
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
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $data = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $data[] = array(
                'id' => $row['id'],
                'fullName' => $row['firstName'] . ' ' . $row['lastName'] . ' (' . $row['designation_name'] . ')',
                'status' => $row['status'],
                'designation_name' => $row['designation_name']
            );
        }
        
        echo json_encode(array(
            'status' => 'success',
            'message' => 'Users with specific designations fetched successfully for dropdown',
            'data' => $data,
            'count' => count($data),
            'debug' => array(
                'all_designations' => $all_designations,
                'designation_count' => count($all_designations)
            )
        ));
    } else {
        echo json_encode(array(
            'status' => 'success',
            'message' => 'No users found with specified designations',
            'data' => array(),
            'count' => 0,
            'debug' => array(
                'all_designations' => $all_designations,
                'designation_count' => count($all_designations)
            )
        ));
    }
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 