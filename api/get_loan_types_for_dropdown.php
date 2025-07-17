<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Query to get all loan type names
    $query = "SELECT DISTINCT loan_type_name FROM loan_types WHERE status = 'active' ORDER BY loan_type_name ASC";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $loanTypes = array();
    while ($row = $result->fetch_assoc()) {
        $loanTypes[] = $row['loan_type_name'];
    }
    
    echo json_encode(array(
        'success' => true,
        'loan_types' => $loanTypes,
        'count' => count($loanTypes)
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 