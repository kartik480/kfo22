<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Check tbl_branch_state table
    $stateQuery = "SELECT * FROM tbl_branch_state LIMIT 10";
    $stateStmt = $conn->prepare($stateQuery);
    $stateStmt->execute();
    $branchStates = $stateStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check tbl_branch_location table
    $locationQuery = "SELECT * FROM tbl_branch_location LIMIT 10";
    $locationStmt = $conn->prepare($locationQuery);
    $locationStmt->execute();
    $branchLocations = $locationStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check sample agent data
    $agentQuery = "SELECT id, state, location FROM tbl_agent_data LIMIT 5";
    $agentStmt = $conn->prepare($agentQuery);
    $agentStmt->execute();
    $agentData = $agentStmt->fetchAll(PDO::FETCH_ASSOC);
    
    $response = [
        'status' => 'success',
        'branch_states' => $branchStates,
        'branch_locations' => $branchLocations,
        'sample_agent_data' => $agentData
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>
