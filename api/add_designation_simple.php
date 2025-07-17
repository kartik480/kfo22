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
    
    $designationName = trim($data['designationName']);
    
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
    
    // Check if designation_name column exists
    if (!in_array('designation_name', $availableColumns)) {
        throw new Exception('designation_name column does not exist in tbl_designation table. Available columns: ' . implode(', ', $availableColumns));
    }
    
    // Check if designation already exists
    $checkQuery = "SELECT id FROM tbl_designation WHERE designation_name = ?";
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("s", $designationName);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        throw new Exception('Designation already exists: ' . $designationName);
    }
    
    // Insert new designation - focus on designation_name column
    $insertQuery = "INSERT INTO tbl_designation (designation_name, status, created_at) VALUES (?, 'Active', NOW())";
    $stmt = $conn->prepare($insertQuery);
    $stmt->bind_param("s", $designationName);
    
    if ($stmt->execute()) {
        $newId = $conn->insert_id;
        
        echo json_encode([
            'status' => 'success',
            'message' => 'Designation added successfully to designation_name column',
            'data' => [
                'id' => $newId,
                'designationName' => $designationName,
                'status' => 'Active'
            ],
            'debug' => [
                'available_columns' => $availableColumns,
                'query_used' => $insertQuery,
                'designation_name_column_exists' => true
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