<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    if (!$conn) {
        throw new Exception("Database connection failed");
    }
    
    $response = [
        'status' => 'success',
        'message' => 'Dropdown options fetched successfully',
        'data' => []
    ];
    
    // 1. Fetch Agent Types from tbl_agent_data
    try {
        $agentTypeQuery = "SELECT DISTINCT agent_type FROM tbl_agent_data WHERE agent_type IS NOT NULL AND agent_type != '' ORDER BY agent_type ASC";
        $agentTypeStmt = $conn->prepare($agentTypeQuery);
        $agentTypeStmt->execute();
        $agentTypes = $agentTypeStmt->fetchAll(PDO::FETCH_COLUMN);
        
        // Add "All Agent Types" as first option
        array_unshift($agentTypes, "All Agent Types");
        
        $response['data']['agent_types'] = $agentTypes;
        $response['data']['agent_types_count'] = count($agentTypes);
        
    } catch (Exception $e) {
        $response['data']['agent_types'] = ["All Agent Types", "Individual Agent", "Corporate Agent", "Broker"];
        $response['data']['agent_types_count'] = 4;
        $response['warnings']['agent_types'] = "Using fallback agent types due to error: " . $e->getMessage();
    }
    
    // 2. Fetch Branch States from tbl_branch_state
    try {
        $branchStateQuery = "SELECT id, branch_state_name FROM tbl_branch_state WHERE branch_state_name IS NOT NULL AND branch_state_name != '' ORDER BY branch_state_name ASC";
        $branchStateStmt = $conn->prepare($branchStateQuery);
        $branchStateStmt->execute();
        $branchStates = $branchStateStmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Add "All States" as first option
        array_unshift($branchStates, ['id' => 0, 'branch_state_name' => 'All States']);
        
        $response['data']['branch_states'] = $branchStates;
        $response['data']['branch_states_count'] = count($branchStates);
        
    } catch (Exception $e) {
        $response['data']['branch_states'] = [
            ['id' => 0, 'branch_state_name' => 'All States'],
            ['id' => 1, 'branch_state_name' => 'Maharashtra'],
            ['id' => 2, 'branch_state_name' => 'Delhi'],
            ['id' => 3, 'branch_state_name' => 'Karnataka'],
            ['id' => 4, 'branch_state_name' => 'Tamil Nadu'],
            ['id' => 5, 'branch_state_name' => 'Gujarat']
        ];
        $response['data']['branch_states_count'] = 6;
        $response['warnings']['branch_states'] = "Using fallback branch states due to error: " . $e->getMessage();
    }
    
    // 3. Fetch Branch Locations from tbl_branch_location
    try {
        $branchLocationQuery = "SELECT id, branch_location FROM tbl_branch_location WHERE branch_location IS NOT NULL AND branch_location != '' ORDER BY branch_location ASC";
        $branchLocationStmt = $conn->prepare($branchLocationQuery);
        $branchLocationStmt->execute();
        $branchLocations = $branchLocationStmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Add "All Locations" as first option
        array_unshift($branchLocations, ['id' => 0, 'branch_location' => 'All Locations']);
        
        $response['data']['branch_locations'] = $branchLocations;
        $response['data']['branch_locations_count'] = count($branchLocations);
        
    } catch (Exception $e) {
        $response['data']['branch_locations'] = [
            ['id' => 0, 'branch_location' => 'All Locations'],
            ['id' => 1, 'branch_location' => 'Mumbai'],
            ['id' => 2, 'branch_location' => 'Pune'],
            ['id' => 3, 'branch_location' => 'Nagpur'],
            ['id' => 4, 'branch_location' => 'Thane'],
            ['id' => 5, 'branch_location' => 'Nashik']
        ];
        $response['data']['branch_locations_count'] = 6;
        $response['warnings']['branch_locations'] = "Using fallback branch locations due to error: " . $e->getMessage();
    }
    
    // Add summary information
    $response['data']['summary'] = [
        'total_agent_types' => $response['data']['agent_types_count'],
        'total_branch_states' => $response['data']['branch_states_count'],
        'total_branch_locations' => $response['data']['branch_locations_count']
    ];
    
    // Add debug information
    $response['debug_info'] = [
        'tables_checked' => ['tbl_agent_data', 'tbl_branch_state', 'tbl_branch_location'],
        'queries_executed' => [
            'agent_types' => isset($agentTypeQuery) ? $agentTypeQuery : 'Fallback used',
            'branch_states' => isset($branchStateQuery) ? $branchStateQuery : 'Fallback used',
            'branch_locations' => isset($branchLocationQuery) ? $branchLocationQuery : 'Fallback used'
        ],
        'fallback_used' => isset($response['warnings']),
        'timestamp' => date('Y-m-d H:i:s')
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch dropdown options: ' . $e->getMessage(),
        'debug_info' => [
            'error_details' => $e->getMessage(),
            'trace' => $e->getTraceAsString(),
            'timestamp' => date('Y-m-d H:i:s')
        ]
    ], JSON_PRETTY_PRINT);
}
?>
