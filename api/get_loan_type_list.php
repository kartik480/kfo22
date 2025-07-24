<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

require_once 'db_config.php';

try {
    $conn = new mysqli($db_host, $db_username, $db_password, $db_name);
    
    if ($conn->connect_error) {
        error_log('Connection failed: ' . $conn->connect_error);
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    // Check if table exists
    $checkTable = $conn->query("SHOW TABLES LIKE 'tbl_loan_type'");
    if (!$checkTable || $checkTable->num_rows == 0) {
        throw new Exception('Table tbl_loan_type does not exist');
    }
    
    $sql = 'SELECT id, loan_type FROM tbl_loan_type ORDER BY loan_type ASC';
    $result = $conn->query($sql);
    
    if (!$result) {
        error_log('Query failed: ' . $conn->error);
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
    error_log('Database error: ' . $e->getMessage());
    echo json_encode(['success' => false, 'error' => 'Database error: ' . $e->getMessage()]);
}
?> 