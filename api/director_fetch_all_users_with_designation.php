<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    $sql = "SELECT u.id, CONCAT(u.firstName, ' ', u.lastName) AS fullName, d.designation_name FROM tbl_user u LEFT JOIN tbl_designation d ON u.designation_id = d.id ORDER BY u.firstName, u.lastName";
    $result = $conn->query($sql);
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = [
            'id' => $row['id'],
            'fullName' => $row['fullName'],
            'designation_name' => $row['designation_name']
        ];
    }
    echo json_encode([
        'status' => 'success',
        'data' => $users,
        'count' => count($users)
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 