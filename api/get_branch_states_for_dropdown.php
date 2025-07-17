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
    // Query to get all branch state names
    $query = "SELECT DISTINCT branch_state_name FROM branch_states WHERE status = 'active' ORDER BY branch_state_name ASC";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $branchStates = array();
    while ($row = $result->fetch_assoc()) {
        $branchStates[] = $row['branch_state_name'];
    }
    
    echo json_encode(array(
        'success' => true,
        'branch_states' => $branchStates,
        'count' => count($branchStates)
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 