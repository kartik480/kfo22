<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

require_once 'db_config.php';

try {
    global $conn;
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }

    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        throw new Exception('Invalid JSON input');
    }

    // Validate required fields
    $required_fields = ['icon_name', 'icon_url', 'icon_description'];
    foreach ($required_fields as $field) {
        if (!isset($input[$field]) || empty(trim($input[$field]))) {
            throw new Exception("Missing required field: $field");
        }
    }

    // Check if tbl_work_icon table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_work_icon'");
    if ($tableCheck->num_rows == 0) {
        throw new Exception('Table tbl_work_icon does not exist');
    }

    // Get table structure to understand available columns
    $columnsQuery = $conn->query("DESCRIBE tbl_work_icon");
    $columns = [];
    while ($column = $columnsQuery->fetch_assoc()) {
        $columns[] = $column['Field'];
    }

    // Prepare data for insertion
    $icon_name = trim($input['icon_name']);
    $icon_url = trim($input['icon_url']);
    $icon_description = trim($input['icon_description']);
    $icon_image = isset($input['icon_image']) ? $input['icon_image'] : null;
    $status = 'Active';
    $created_at = date('Y-m-d H:i:s');

    // Build SQL query based on available columns
    $fields = [];
    $placeholders = [];
    $values = [];
    $types = '';

    if (in_array('icon_name', $columns)) {
        $fields[] = 'icon_name';
        $placeholders[] = '?';
        $values[] = $icon_name;
        $types .= 's';
    }

    if (in_array('icon_url', $columns)) {
        $fields[] = 'icon_url';
        $placeholders[] = '?';
        $values[] = $icon_url;
        $types .= 's';
    }

    if (in_array('icon_description', $columns)) {
        $fields[] = 'icon_description';
        $placeholders[] = '?';
        $values[] = $icon_description;
        $types .= 's';
    }

    if (in_array('icon_image', $columns) && $icon_image) {
        $fields[] = 'icon_image';
        $placeholders[] = '?';
        $values[] = $icon_image;
        $types .= 's';
    }

    if (in_array('status', $columns)) {
        $fields[] = 'status';
        $placeholders[] = '?';
        $values[] = $status;
        $types .= 's';
    }

    if (in_array('created_at', $columns)) {
        $fields[] = 'created_at';
        $placeholders[] = '?';
        $values[] = $created_at;
        $types .= 's';
    }

    if (empty($fields)) {
        throw new Exception('No valid columns found in tbl_work_icon table');
    }

    $sql = "INSERT INTO tbl_work_icon (" . implode(', ', $fields) . ") VALUES (" . implode(', ', $placeholders) . ")";
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception('Prepare failed: ' . $conn->error);
    }

    if (!empty($types)) {
        $stmt->bind_param($types, ...$values);
    }

    if ($stmt->execute()) {
        $icon_id = $conn->insert_id;
        
        echo json_encode([
            'success' => true,
            'message' => 'Work icon added successfully',
            'icon_id' => $icon_id,
            'data' => [
                'icon_name' => $icon_name,
                'icon_url' => $icon_url,
                'icon_description' => $icon_description,
                'status' => $status,
                'created_at' => $created_at
            ]
        ]);
    } else {
        throw new Exception('Failed to insert work icon: ' . $stmt->error);
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => $e->getMessage()
    ]);
}
?> 