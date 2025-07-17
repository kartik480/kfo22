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
    
    $sql = 'SELECT id, partner_type FROM tbl_partner_type ORDER BY partner_type ASC';
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    
    $partnerTypes = [];
    while ($row = $result->fetch_assoc()) {
        $partnerTypes[] = [
            'id' => $row['id'],
            'name' => $row['partner_type'],
            'partner_type' => $row['partner_type']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'success' => true,
        'data' => $partnerTypes,
        'count' => count($partnerTypes)
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?> 