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
    
    // Check if tbl_designation table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_designation'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('tbl_designation table does not exist');
    }
    
    // Get table structure to check columns
    $columnsQuery = "SHOW COLUMNS FROM tbl_designation";
    $columnsResult = $conn->query($columnsQuery);
    
    if ($columnsResult === false) {
        throw new Exception("Failed to get table structure: " . $conn->error);
    }
    
    $availableColumns = array();
    while ($column = $columnsResult->fetch_assoc()) {
        $availableColumns[] = $column['Field'];
    }
    
    // Build SELECT query based on available columns
    $selectColumns = array();
    if (in_array('id', $availableColumns)) $selectColumns[] = 'id';
    if (in_array('designation_name', $availableColumns)) $selectColumns[] = 'designation_name';
    if (in_array('name', $availableColumns)) $selectColumns[] = 'name';
    if (in_array('department_id', $availableColumns)) $selectColumns[] = 'department_id';
    if (in_array('status', $availableColumns)) $selectColumns[] = 'status';
    if (in_array('created_at', $availableColumns)) $selectColumns[] = 'created_at';
    if (in_array('updated_at', $availableColumns)) $selectColumns[] = 'updated_at';
    
    // If no columns found, use basic ones
    if (empty($selectColumns)) {
        $selectColumns = ['id', 'designation_name'];
    }
    
    $sql = "SELECT " . implode(', ', $selectColumns) . " FROM tbl_designation ORDER BY ";
    
    // Add ORDER BY clause based on available columns
    if (in_array('designation_name', $availableColumns)) {
        $sql .= "designation_name ASC";
    } else if (in_array('name', $availableColumns)) {
        $sql .= "name ASC";
    } else {
        $sql .= "id ASC";
    }
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error . " | SQL: " . $sql);
    }
    
    $designations = array();
    $totalCount = 0;
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $totalCount++;
            
            // Determine designation name field
            $designationName = '';
            if (isset($row['designation_name'])) {
                $designationName = $row['designation_name'];
            } else if (isset($row['name'])) {
                $designationName = $row['name'];
            }
            
            $designationData = array(
                'id' => isset($row['id']) ? $row['id'] : '',
                'designation_name' => $designationName,
                'department_id' => isset($row['department_id']) ? $row['department_id'] : '',
                'status' => isset($row['status']) ? $row['status'] : 'Active',
                'created_at' => isset($row['created_at']) ? $row['created_at'] : '',
                'updated_at' => isset($row['updated_at']) ? $row['updated_at'] : ''
            );
            
            $designations[] = $designationData;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Designations fetched successfully from tbl_designation',
        'data' => $designations,
        'summary' => [
            'total_designations' => $totalCount
        ],
        'debug' => [
            'available_columns' => $availableColumns,
            'selected_columns' => $selectColumns,
            'query_used' => $sql
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_designations' => 0
        ]
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 