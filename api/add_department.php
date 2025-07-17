<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration for kfinone database
$db_host = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_name = "emp_kfinone";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";

try {
    // Only allow POST method
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Only POST method is allowed');
    }
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    if (!$data) {
        throw new Exception('Invalid JSON input');
    }
    
    // Validate required fields
    if (!isset($data['departmentName']) || empty(trim($data['departmentName']))) {
        throw new Exception('Department name is required');
    }
    
    $departmentName = trim($data['departmentName']);
    
    // Create connection
    $conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);
    
    if (!$conn) {
        throw new Exception('Connection failed: ' . mysqli_connect_error());
    }
    
    // Check if tbl_department table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_department'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('tbl_department table does not exist');
    }
    
    // Get table structure to check columns
    $columnsQuery = "SHOW COLUMNS FROM tbl_department";
    $columnsResult = $conn->query($columnsQuery);
    
    if ($columnsResult === false) {
        throw new Exception("Failed to get table structure: " . $conn->error);
    }
    
    $availableColumns = array();
    while ($column = $columnsResult->fetch_assoc()) {
        $availableColumns[] = $column['Field'];
    }
    
    // Check if department already exists
    $checkQuery = "";
    if (in_array('department_name', $availableColumns)) {
        $checkQuery = "SELECT id FROM tbl_department WHERE department_name = ?";
    } else if (in_array('name', $availableColumns)) {
        $checkQuery = "SELECT id FROM tbl_department WHERE name = ?";
    } else {
        throw new Exception('No department name column found in table');
    }
    
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("s", $departmentName);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        throw new Exception('Department already exists');
    }
    
    // Insert new department
    $insertQuery = "";
    if (in_array('department_name', $availableColumns)) {
        $insertQuery = "INSERT INTO tbl_department (department_name, status, created_at) VALUES (?, 'Active', NOW())";
    } else if (in_array('name', $availableColumns)) {
        $insertQuery = "INSERT INTO tbl_department (name, status, created_at) VALUES (?, 'Active', NOW())";
    }
    
    $stmt = $conn->prepare($insertQuery);
    $stmt->bind_param("s", $departmentName);
    
    if ($stmt->execute()) {
        $newId = $conn->insert_id;
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Department added successfully',
            'data' => [
                'id' => $newId,
                'departmentName' => $departmentName,
                'status' => 'Active'
            ]
        ]);
    } else {
        throw new Exception('Failed to insert department: ' . $stmt->error);
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage()
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 