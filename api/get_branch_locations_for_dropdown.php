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
    // Query to get all branch location names
    $query = "SELECT DISTINCT branch_location FROM branch_locations WHERE status = 'active' ORDER BY branch_location ASC";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $branchLocations = array();
    while ($row = $result->fetch_assoc()) {
        $branchLocations[] = $row['branch_location'];
    }
    
    echo json_encode(array(
        'success' => true,
        'branch_locations' => $branchLocations,
        'count' => count($branchLocations)
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 