<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Connection failed: " . mysqli_connect_error());
    }
    
    $sql = "SELECT branch_state_name FROM tbl_branch_state ORDER BY branch_state_name ASC";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $branchStates = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $branchStates[] = $row['branch_state_name'];
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $branchStates,
        'message' => 'Branch states fetched successfully'
    ));
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => $e->getMessage()
    ));
}
?> 