<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    $results = array();
    
    // Add sample partner types
    $partnerTypes = array('Individual', 'Company', 'Partnership', 'LLP', 'Proprietorship');
    foreach ($partnerTypes as $type) {
        $sql = "INSERT IGNORE INTO tbl_partner_type (partner_type) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $type);
        $stmt->execute();
    }
    $results['partner_types_added'] = count($partnerTypes);
    
    // Add sample branch states
    $branchStates = array('Maharashtra', 'Delhi', 'Karnataka', 'Tamil Nadu', 'Gujarat', 'Uttar Pradesh');
    foreach ($branchStates as $state) {
        $sql = "INSERT IGNORE INTO tbl_branch_state (branch_state_name) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $state);
        $stmt->execute();
    }
    $results['branch_states_added'] = count($branchStates);
    
    // Add sample branch locations
    $branchLocations = array('Mumbai', 'Delhi', 'Bangalore', 'Chennai', 'Ahmedabad', 'Lucknow');
    foreach ($branchLocations as $location) {
        $sql = "INSERT IGNORE INTO tbl_branch_location (branch_location) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $location);
        $stmt->execute();
    }
    $results['branch_locations_added'] = count($branchLocations);
    
    // Add sample banks
    $banks = array('State Bank of India', 'HDFC Bank', 'ICICI Bank', 'Axis Bank', 'Punjab National Bank');
    foreach ($banks as $bank) {
        $sql = "INSERT IGNORE INTO tbl_bank (bank_name) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $bank);
        $stmt->execute();
    }
    $results['banks_added'] = count($banks);
    
    // Add sample account types
    $accountTypes = array('Savings Account', 'Current Account', 'Fixed Deposit', 'Recurring Deposit');
    foreach ($accountTypes as $type) {
        $sql = "INSERT IGNORE INTO tbl_account_type (account_type) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $type);
        $stmt->execute();
    }
    $results['account_types_added'] = count($accountTypes);
    
    echo json_encode([
        'success' => true,
        'message' => 'Sample data added successfully',
        'data' => $results
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => null
    ]);
}
?> 