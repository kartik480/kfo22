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
                pu.id,
                pu.first_name,
                pu.last_name,
                pu.Phone_number,
                pu.email_id,
                pu.createdBy,
                CONCAT(u.firstName, ' ', u.lastName) AS creator_name
            FROM tbl_partner_users pu
            LEFT JOIN tbl_user u ON pu.createdBy = u.id
            $where
            ORDER BY pu.id DESC";
    $result = $conn->query($sql);
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = [
            'id' => $row['id'],
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'Phone_number' => $row['Phone_number'],
            'email_id' => $row['email_id'],
            'createdBy' => $row['createdBy'],
            'creator_name' => $row['creator_name']
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