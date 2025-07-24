<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    // Find all unique createdBy values in tbl_partner_users that do not exist in tbl_user
    $sql = "SELECT DISTINCT pu.createdBy FROM tbl_partner_users pu LEFT JOIN tbl_user u ON pu.createdBy = u.id WHERE u.id IS NULL AND pu.createdBy IS NOT NULL AND pu.createdBy != ''";
    $result = $conn->query($sql);
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    $inserted = 0;
    while ($row = $result->fetch_assoc()) {
        $missingId = $conn->real_escape_string($row['createdBy']);
        // Insert a placeholder user
        $insertSql = "INSERT INTO tbl_user (id, firstName, lastName, status) VALUES ('$missingId', 'Unknown', 'Creator $missingId', 'Active')";
        if ($conn->query($insertSql)) {
            $inserted++;
        }
    }
    echo json_encode([
        'status' => 'success',
        'message' => "Inserted $inserted placeholder users for missing createdBy IDs.",
        'inserted' => $inserted
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 