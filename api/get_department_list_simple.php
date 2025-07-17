<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    // Check if tbl_department table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_department'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('Table tbl_department does not exist');
    }
    
    // Get table structure to verify department_name column exists
    $columnsQuery = $conn->query("DESCRIBE tbl_department");
    $columns = [];
    while ($column = $columnsQuery->fetch_assoc()) {
        $columns[] = $column['Field'];
    }
    
    if (!in_array('department_name', $columns)) {
        throw new Exception('Column department_name does not exist in tbl_department');
    }
    
    // Fetch all departments from tbl_department using department_name column
    $sql = "SELECT id, department_name FROM tbl_department ORDER BY department_name ASC";
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    
    $departments = [];
    while ($row = $result->fetch_assoc()) {
        $departments[] = [
            'id' => $row['id'],
            'department_name' => $row['department_name']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Department list fetched successfully from tbl_department',
        'data' => $departments,
        'count' => count($departments)
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 