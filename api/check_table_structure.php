<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration for kfinone database
$db_host = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_name = "emp_kfinone";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";

try {
    // Create connection
    $conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);
    
    if (!$conn) {
        throw new Exception('Connection failed: ' . mysqli_connect_error());
    }
    
    // Check if tbl_user table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('tbl_user table does not exist');
    }
    
    // Get table structure
    $columnsQuery = "SHOW COLUMNS FROM tbl_user";
    $columnsResult = $conn->query($columnsQuery);
    
    if ($columnsResult === false) {
        throw new Exception("Failed to get table structure: " . $conn->error);
    }
    
    $columns = array();
    while ($column = $columnsResult->fetch_assoc()) {
        $columns[] = array(
            'field' => $column['Field'],
            'type' => $column['Type'],
            'null' => $column['Null'],
            'key' => $column['Key'],
            'default' => $column['Default'],
            'extra' => $column['Extra']
        );
    }
    
    // Get sample data (first 3 rows)
    $sampleQuery = "SELECT * FROM tbl_user LIMIT 3";
    $sampleResult = $conn->query($sampleQuery);
    
    $sampleData = array();
    if ($sampleResult && $sampleResult->num_rows > 0) {
        while ($row = $sampleResult->fetch_assoc()) {
            $sampleData[] = $row;
        }
    }
    
    // Get total count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_user";
    $countResult = $conn->query($countQuery);
    $totalCount = $countResult->fetch_assoc()['total'];
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Table structure retrieved successfully',
        'table_info' => [
            'table_name' => 'tbl_user',
            'total_rows' => $totalCount,
            'columns_count' => count($columns)
        ],
        'columns' => $columns,
        'sample_data' => $sampleData
    ]);
    
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