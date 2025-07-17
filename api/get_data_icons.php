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
    
    // Check if tbl_data_icon table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_data_icon'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('Table tbl_data_icon does not exist');
    }
    
    // Get table structure to understand available columns
    $columnsQuery = $conn->query("DESCRIBE tbl_data_icon");
    $columns = [];
    while ($column = $columnsQuery->fetch_assoc()) {
        $columns[] = $column['Field'];
    }
    
    // Build SELECT query based on available columns
    $selectColumns = [];
    if (in_array('id', $columns)) $selectColumns[] = 'id';
    if (in_array('icon_name', $columns)) $selectColumns[] = 'icon_name';
    if (in_array('icon_url', $columns)) $selectColumns[] = 'icon_url';
    if (in_array('icon_image', $columns)) $selectColumns[] = 'icon_image';
    if (in_array('icon_description', $columns)) $selectColumns[] = 'icon_description';
    if (in_array('status', $columns)) $selectColumns[] = 'status';
    if (in_array('created_at', $columns)) $selectColumns[] = 'created_at';
    if (in_array('updated_at', $columns)) $selectColumns[] = 'updated_at';
    
    // If no specific columns found, use all columns
    if (empty($selectColumns)) {
        $selectColumns = ['*'];
    }
    
    // Build SQL query
    $sql = "SELECT " . implode(', ', $selectColumns) . " FROM tbl_data_icon ORDER BY ";
    
    // Add ORDER BY clause based on available columns
    if (in_array('created_at', $columns)) {
        $sql .= "created_at DESC";
    } else if (in_array('id', $columns)) {
        $sql .= "id DESC";
    } else {
        $sql .= "1"; // Order by first column
    }
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error . ' | SQL: ' . $sql);
    }
    
    $dataIcons = [];
    $totalCount = 0;
    
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $totalCount++;
            
            $iconData = [
                'id' => isset($row['id']) ? $row['id'] : '',
                'icon_name' => isset($row['icon_name']) ? $row['icon_name'] : '',
                'icon_url' => isset($row['icon_url']) ? $row['icon_url'] : '',
                'icon_image' => isset($row['icon_image']) ? $row['icon_image'] : '',
                'icon_description' => isset($row['icon_description']) ? $row['icon_description'] : '',
                'status' => isset($row['status']) ? $row['status'] : 'Active',
                'created_at' => isset($row['created_at']) ? $row['created_at'] : '',
                'updated_at' => isset($row['updated_at']) ? $row['updated_at'] : ''
            ];
            
            $dataIcons[] = $iconData;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Data icons fetched successfully from tbl_data_icon',
        'data' => $dataIcons,
        'summary' => [
            'total_icons' => $totalCount
        ],
        'debug' => [
            'available_columns' => $columns,
            'selected_columns' => $selectColumns,
            'query_used' => $sql
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_icons' => 0
        ]
    ]);
}

$conn->close();
?> 