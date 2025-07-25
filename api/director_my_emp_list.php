<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$response = ['success' => false, 'data' => [], 'error' => null];

try {
    $pdo = getConnection();

    // Get the id(s) for the reporting users
    $stmt = $pdo->prepare("SELECT id FROM tbl_user WHERE username = :uname1 OR username = :uname2");
    $stmt->execute([
        ':uname1' => '90000',
        ':uname2' => 'VENKATESWARA RAO BALUSU'
    ]);
    $ids = $stmt->fetchAll(PDO::FETCH_COLUMN);

    if (count($ids) > 0) {
        // Prepare placeholders for IN clause
        $in = str_repeat('?,', count($ids) - 1) . '?';
        $sql = "SELECT * FROM tbl_user WHERE reportingTo IN ($in)";
        $stmt2 = $pdo->prepare($sql);
        $stmt2->execute($ids);
        $response['data'] = $stmt2->fetchAll(PDO::FETCH_ASSOC);
        $response['success'] = true;
    } else {
        $response['error'] = 'No reporting user found with username 90000 or VENKATESWARA RAO BALUSU';
    }
} catch (Exception $e) {
    $response['error'] = $e->getMessage();
}

echo json_encode($response); 