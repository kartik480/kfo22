<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

require_once 'db_config.php';

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    $sql = 'SELECT id, loan_type FROM tbl_loan_type ORDER BY loan_type ASC';
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception('Query failed: ' . $conn->error);
    }
    
    $loanTypes = [];
    while ($row = $result->fetch_assoc()) {
        $loanTypes[] = [
            'id' => $row['id'],
            'loan_type' => $row['loan_type']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $loanTypes,
        'count' => count($loanTypes)
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?> 