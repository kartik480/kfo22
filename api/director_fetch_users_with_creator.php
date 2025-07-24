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
                u.id, 
                u.firstName, 
                u.lastName, 
                u.email_id, 
                u.mobile, 
                pu.createdBy, 
                CONCAT(creator.firstName, ' ', creator.lastName) AS creator_name
            FROM tbl_user u
            LEFT JOIN tbl_partner_users pu ON pu.id = u.id
            LEFT JOIN tbl_user creator ON pu.createdBy = creator.id
            $where
            ORDER BY u.id DESC";
    $result = $conn->query($sql);
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = [
            'id' => $row['id'],
            'first_name' => $row['firstName'],
            'last_name' => $row['lastName'],
            'email_id' => $row['email_id'],
            'mobile' => $row['mobile'],
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