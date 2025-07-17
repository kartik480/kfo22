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
    
    $results = array();
    
    // Check if tbl_branch_state exists and add sample data
    $sql = "SHOW TABLES LIKE 'tbl_branch_state'";
    $result = $conn->query($sql);
    
    if ($result->num_rows > 0) {
        // Check if table is empty
        $sql = "SELECT COUNT(*) as count FROM tbl_branch_state";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        
        if ($row['count'] == 0) {
            // Add sample branch states
            $branchStates = [
                'Maharashtra',
                'Delhi',
                'Karnataka',
                'Tamil Nadu',
                'Gujarat',
                'Uttar Pradesh',
                'West Bengal',
                'Telangana',
                'Andhra Pradesh',
                'Kerala'
            ];
            
            foreach ($branchStates as $state) {
                $sql = "INSERT INTO tbl_branch_state (branch_state_name) VALUES (?)";
                $stmt = $conn->prepare($sql);
                $stmt->bind_param("s", $state);
                $stmt->execute();
            }
            
            $results['branch_states_added'] = count($branchStates);
        } else {
            $results['branch_states_existing'] = $row['count'];
        }
    } else {
        $results['branch_state_table_missing'] = true;
    }
    
    // Check if tbl_branch_location exists and add sample data
    $sql = "SHOW TABLES LIKE 'tbl_branch_location'";
    $result = $conn->query($sql);
    
    if ($result->num_rows > 0) {
        // Check if table is empty
        $sql = "SELECT COUNT(*) as count FROM tbl_branch_location";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        
        if ($row['count'] == 0) {
            // Add sample branch locations
            $branchLocations = [
                'Mumbai Central',
                'Andheri West',
                'Bandra West',
                'Juhu',
                'Powai',
                'Thane West',
                'Navi Mumbai',
                'Pune City',
                'Nagpur',
                'Aurangabad'
            ];
            
            foreach ($branchLocations as $location) {
                $sql = "INSERT INTO tbl_branch_location (branch_location) VALUES (?)";
                $stmt = $conn->prepare($sql);
                $stmt->bind_param("s", $location);
                $stmt->execute();
            }
            
            $results['branch_locations_added'] = count($branchLocations);
        } else {
            $results['branch_locations_existing'] = $row['count'];
        }
    } else {
        $results['branch_location_table_missing'] = true;
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Sample branch data setup completed',
        'data' => $results
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => null
    ]);
}
?> 