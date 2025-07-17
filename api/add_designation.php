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
    if (!isset($data['designationName']) || empty(trim($data['designationName']))) {
        throw new Exception('Designation name is required');
    }
    
    if (!isset($data['departmentName']) || empty(trim($data['departmentName']))) {
        throw new Exception('Department name is required');
    }
    
    $designationName = trim($data['designationName']);
    $departmentName = trim($data['departmentName']);
    
    // Create connection
    $conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);
    
    if (!$conn) {
        throw new Exception('Connection failed: ' . mysqli_connect_error());
    }
    
    // Check if tbl_designation table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_designation'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('tbl_designation table does not exist');
    }
    
    // Check if tbl_department table exists
    $deptTableCheck = $conn->query("SHOW TABLES LIKE 'tbl_department'");
    if ($deptTableCheck->num_rows == 0) {
        throw new Exception('tbl_department table does not exist');
    }
    
    // Get table structure to check columns
    $columnsQuery = "SHOW COLUMNS FROM tbl_designation";
    $columnsResult = $conn->query($columnsQuery);
    
    if ($columnsResult === false) {
        throw new Exception("Failed to get designation table structure: " . $conn->error);
    }
    
    $availableColumns = array();
    while ($column = $columnsResult->fetch_assoc()) {
        $availableColumns[] = $column['Field'];
    }
    
    // Get department table structure
    $deptColumnsQuery = "SHOW COLUMNS FROM tbl_department";
    $deptColumnsResult = $conn->query($deptColumnsQuery);
    
    if ($deptColumnsResult === false) {
        throw new Exception("Failed to get department table structure: " . $conn->error);
    }
    
    $deptAvailableColumns = array();
    while ($column = $deptColumnsResult->fetch_assoc()) {
        $deptAvailableColumns[] = $column['Field'];
    }
    
    // First, get the department ID
    $deptId = null;
    $deptCheckQuery = "";
    if (in_array('department_name', $deptAvailableColumns)) {
        $deptCheckQuery = "SELECT id FROM tbl_department WHERE department_name = ?";
    } else if (in_array('name', $deptAvailableColumns)) {
        $deptCheckQuery = "SELECT id FROM tbl_department WHERE name = ?";
    } else {
        throw new Exception('No department name column found in tbl_department table');
    }
    
    $stmt = $conn->prepare($deptCheckQuery);
    $stmt->bind_param("s", $departmentName);
    $stmt->execute();
    $deptResult = $stmt->get_result();
    
    if ($deptResult->num_rows === 0) {
        throw new Exception('Department not found: ' . $departmentName);
    }
    
    $deptRow = $deptResult->fetch_assoc();
    $deptId = $deptRow['id'];
    
    // Check if designation already exists
    $checkQuery = "";
    if (in_array('name', $availableColumns)) {
        $checkQuery = "SELECT id FROM tbl_designation WHERE name = ?";
    } else if (in_array('designation_name', $availableColumns)) {
        $checkQuery = "SELECT id FROM tbl_designation WHERE designation_name = ?";
    } else {
        throw new Exception('No designation name column found in tbl_designation table');
    }
    
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("s", $designationName);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        throw new Exception('Designation already exists');
    }
    
    // Insert new designation
    $insertQuery = "";
    if (in_array('name', $availableColumns) && in_array('department_id', $availableColumns)) {
        $insertQuery = "INSERT INTO tbl_designation (name, department_id, status, created_at) VALUES (?, ?, 'Active', NOW())";
    } else if (in_array('designation_name', $availableColumns) && in_array('department_id', $availableColumns)) {
        $insertQuery = "INSERT INTO tbl_designation (designation_name, department_id, status, created_at) VALUES (?, ?, 'Active', NOW())";
    } else if (in_array('name', $availableColumns)) {
        $insertQuery = "INSERT INTO tbl_designation (name, status, created_at) VALUES (?, 'Active', NOW())";
    } else if (in_array('designation_name', $availableColumns)) {
        $insertQuery = "INSERT INTO tbl_designation (designation_name, status, created_at) VALUES (?, 'Active', NOW())";
    } else {
        throw new Exception('No suitable columns found for inserting designation');
    }
    
    $stmt = $conn->prepare($insertQuery);
    
    if (in_array('department_id', $availableColumns)) {
        $stmt->bind_param("si", $designationName, $deptId);
    } else {
        $stmt->bind_param("s", $designationName);
    }
    
    if ($stmt->execute()) {
        $newId = $conn->insert_id;
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Designation added successfully',
            'data' => [
                'id' => $newId,
                'designationName' => $designationName,
                'departmentName' => $departmentName,
                'departmentId' => $deptId,
                'status' => 'Active'
            ]
        ]);
    } else {
        throw new Exception('Failed to insert designation: ' . $stmt->error);
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