<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

require_once 'db_config.php';

try {
    global $conn;
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }

    // Check if tbl_manage_icon table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('Table tbl_manage_icon does not exist');
    }

    // Check table structure
    $columnsQuery = $conn->query("DESCRIBE tbl_manage_icon");
    $columns = [];
    while ($column = $columnsQuery->fetch_assoc()) {
        $columns[] = $column['Field'];
    }

    // Build SELECT query with available columns
    $selectColumns = [];
    if (in_array('id', $columns)) $selectColumns[] = 'id';
    if (in_array('icon_name', $columns)) $selectColumns[] = 'icon_name';
    if (in_array('icon_image', $columns)) $selectColumns[] = 'icon_image';
    if (in_array('icon_description', $columns)) $selectColumns[] = 'icon_description';
    if (in_array('status', $columns)) $selectColumns[] = 'status';
    if (in_array('created_at', $columns)) $selectColumns[] = 'created_at';
    if (in_array('updated_at', $columns)) $selectColumns[] = 'updated_at';
    if (in_array('icon_url', $columns)) $selectColumns[] = 'icon_url';

    if (empty($selectColumns)) {
        throw new Exception('No valid columns found in tbl_manage_icon');
    }

    $sql = "SELECT " . implode(', ', $selectColumns) . " FROM tbl_manage_icon WHERE status = 1 OR status = 'Active' OR status IS NULL OR status = '' ORDER BY icon_name ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }

    $icons = [];
    $totalCount = 0;
    $activeCount = 0;
    $inactiveCount = 0;

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $status = isset($row['status']) ? $row['status'] : 'Active';
            if ($status == 'Active' || $status == '1' || empty($status)) {
                $activeCount++;
            } else {
                $inactiveCount++;
            }
            $totalCount++;

            $iconData = [
                'id' => isset($row['id']) ? $row['id'] : '',
                'icon_name' => isset($row['icon_name']) ? $row['icon_name'] : '',
                'icon_url' => isset($row['icon_url']) ? $row['icon_url'] : '',
                'icon_image' => isset($row['icon_image']) ? $row['icon_image'] : '',
                'icon_description' => isset($row['icon_description']) ? $row['icon_description'] : '',
                'status' => $status,
                'created_at' => isset($row['created_at']) ? $row['created_at'] : '',
                'updated_at' => isset($row['updated_at']) ? $row['updated_at'] : ''
            ];

            $icons[] = $iconData;
        }
    }

    echo json_encode([
        'status' => 'success',
        'message' => 'Manage icons fetched successfully',
        'data' => $icons,
        'summary' => [
            'total_icons' => $totalCount,
            'active_icons' => $activeCount,
            'inactive_icons' => $inactiveCount
        ],
        'debug' => [
            'available_columns' => $columns,
            'selected_columns' => $selectColumns
        ]
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_icons' => 0,
            'active_icons' => 0,
            'inactive_icons' => 0
        ]
    ]);
}

if (isset($conn)) {
    $conn->close();
}
?> 