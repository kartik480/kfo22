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
    
    // Query to get all departments from tbl_department
    $sql = "SELECT id, department_name FROM tbl_department ORDER BY department_name ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $departmentList = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $departmentList[] = array(
                'id' => $row['id'],
                'department_name' => $row['department_name']
            );
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $departmentList,
        'message' => 'Department list fetched successfully',
        'count' => count($departmentList)
    ));
    
} catch (Exception $e) {
    error_log("Error in get_department_list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch department list: ' . $e->getMessage()
    ));
}
?> 