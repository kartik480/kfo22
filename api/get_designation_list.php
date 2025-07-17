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
    
    // Query to get all designations from tbl_designation
    $sql = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $designationList = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $designationList[] = array(
                'id' => $row['id'],
                'designation_name' => $row['designation_name']
            );
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $designationList,
        'message' => 'Designation list fetched successfully',
        'count' => count($designationList)
    ));
    
} catch (Exception $e) {
    error_log("Error in get_designation_list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch designation list: ' . $e->getMessage()
    ));
}
?> 