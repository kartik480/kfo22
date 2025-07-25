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
    $createdBy = isset($_GET['createdBy']) ? $conn->real_escape_string($_GET['createdBy']) : '';
    $where = '';
    if (!empty($createdBy)) {
        $where = "WHERE pu.createdBy = '$createdBy'";
    }
    $sql = "SELECT 
                pu.*, 
                creator.username AS creator_username, 
                creator.firstName AS creator_firstName, 
                creator.lastName AS creator_lastName, 
                creator.designation_id AS creator_designation_id
            FROM tbl_partner_users pu
            LEFT JOIN tbl_user creator ON pu.createdBy = creator.id
            WHERE creator.designation_id IN (90000, 30000)
            ORDER BY pu.id DESC";
    $result = $conn->query($sql);
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = $row;
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