<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

function fetchBanks($conn) {
    $sql = 'SELECT id, bank_name FROM tbl_bank ORDER BY bank_name ASC';
    $result = $conn->query($sql);
    if (!$result) {
        return [
            'success' => false,
            'data' => [],
            'error' => 'Query failed: ' . $conn->error
        ];
    }
    $banks = [];
    while ($row = $result->fetch_assoc()) {
        $banks[] = [
            'id' => $row['id'],
            'bank_name' => $row['bank_name']
        ];
    }
    return [
        'success' => true,
        'data' => $banks,
        'count' => count($banks)
    ];
}

try {
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    $response = fetchBanks($conn);
    echo json_encode($response);
    $conn->close();
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 