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
    
    // Build SELECT query based on available columns
    $selectColumns = array();
    if (in_array('id', $availableColumns)) $selectColumns[] = 'id';
    if (in_array('department_name', $availableColumns)) $selectColumns[] = 'department_name';
    if (in_array('name', $availableColumns)) $selectColumns[] = 'name';
    
    // If no columns found, use basic ones
    if (empty($selectColumns)) {
        $selectColumns = ['id', 'department_name'];
    }
    
    $sql = "SELECT " . implode(', ', $selectColumns) . " FROM tbl_department ORDER BY ";
    
    // Add ORDER BY clause based on available columns
    if (in_array('department_name', $availableColumns)) {
        $sql .= "department_name ASC";
    } else if (in_array('name', $availableColumns)) {
        $sql .= "name ASC";
    } else {
        $sql .= "id ASC";
    }
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error . " | SQL: " . $sql);
    }
    
    $departments = array();
    $totalCount = 0;
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $totalCount++;
            
            // Determine department name field
            $departmentName = '';
            if (isset($row['department_name'])) {
                $departmentName = $row['department_name'];
            } else if (isset($row['name'])) {
                $departmentName = $row['name'];
            }
            
            // Format for dropdown - just the essential fields
            $departmentData = array(
                'id' => isset($row['id']) ? $row['id'] : '',
                'departmentName' => $departmentName,
                'value' => $departmentName, // For dropdown value
                'label' => $departmentName  // For dropdown label
            );
            
            $departments[] = $departmentData;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Departments fetched successfully for dropdown',
        'data' => $departments,
        'summary' => [
            'total_departments' => $totalCount
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
            'total_departments' => 0
        ]
    ]);
}

// Close connection
if (isset($conn)) {
    mysqli_close($conn);
}
?> 