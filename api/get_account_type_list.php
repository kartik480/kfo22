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
    
    $sql = 'SELECT id, account_type FROM tbl_account_type ORDER BY account_type ASC';
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    
    $accountTypes = [];
    while ($row = $result->fetch_assoc()) {
        $accountTypes[] = [
            'id' => $row['id'],
            'account_type' => $row['account_type']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $accountTypes,
        'count' => count($accountTypes)
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 