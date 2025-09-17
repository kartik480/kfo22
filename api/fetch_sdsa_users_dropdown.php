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
    
    // Query to fetch only Regional Business Head users for CBO Team SDSA dropdown
    $sql = "SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.designation_id,
                u.status,
                d.designation_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE d.designation_name = 'Regional Business Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL AND u.firstName != ''
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
                'username' => $row['firstName'] . $row['lastName'], // Simple username
                'firstName' => $row['firstName'],
                'lastName' => $row['lastName'],
                'designation_id' => $row['designation_id'],
                'designation_name' => $row['designation_name'],
                'fullName' => $row['fullName'],
                'displayName' => $row['displayName']
            );
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Regional Business Head users fetched successfully for CBO Team SDSA dropdown',
            'users' => $data,
            'count' => count($data),
            'debug' => array(
                'all_designations' => $all_designations,
                'designation_count' => count($all_designations)
            )
        ));
    } else {
        echo json_encode(array(
            'success' => true,
            'message' => 'No Regional Business Head users found',
            'users' => array(),
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