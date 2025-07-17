<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    $results = array();
    
    // Create tbl_partner_type if not exists
    $sql = "CREATE TABLE IF NOT EXISTS tbl_partner_type (
        id INT AUTO_INCREMENT PRIMARY KEY,
        partner_type VARCHAR(100) NOT NULL UNIQUE,
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM('Active', 'Inactive') DEFAULT 'Active'
    )";
    
    if ($conn->query($sql) === TRUE) {
        $results['partner_type_table'] = 'Created or already exists';
    } else {
        throw new Exception('Error creating partner_type table: ' . $conn->error);
    }
    
    // Create tbl_branch_state if not exists
    $sql = "CREATE TABLE IF NOT EXISTS tbl_branch_state (
        id INT AUTO_INCREMENT PRIMARY KEY,
        branch_state_name VARCHAR(100) NOT NULL UNIQUE,
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM('Active', 'Inactive') DEFAULT 'Active'
    )";
    
    if ($conn->query($sql) === TRUE) {
        $results['branch_state_table'] = 'Created or already exists';
    } else {
        throw new Exception('Error creating branch_state table: ' . $conn->error);
    }
    
    // Create tbl_branch_location if not exists
    $sql = "CREATE TABLE IF NOT EXISTS tbl_branch_location (
        id INT AUTO_INCREMENT PRIMARY KEY,
        branch_location VARCHAR(100) NOT NULL UNIQUE,
        branch_state_id INT,
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM('Active', 'Inactive') DEFAULT 'Active',
        FOREIGN KEY (branch_state_id) REFERENCES tbl_branch_state(id)
    )";
    
    if ($conn->query($sql) === TRUE) {
        $results['branch_location_table'] = 'Created or already exists';
    } else {
        throw new Exception('Error creating branch_location table: ' . $conn->error);
    }
    
    // Create tbl_bank if not exists
    $sql = "CREATE TABLE IF NOT EXISTS tbl_bank (
        id INT AUTO_INCREMENT PRIMARY KEY,
        bank_name VARCHAR(200) NOT NULL UNIQUE,
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM('Active', 'Inactive') DEFAULT 'Active'
    )";
    
    if ($conn->query($sql) === TRUE) {
        $results['bank_table'] = 'Created or already exists';
    } else {
        throw new Exception('Error creating bank table: ' . $conn->error);
    }
    
    // Create tbl_account_type if not exists
    $sql = "CREATE TABLE IF NOT EXISTS tbl_account_type (
        id INT AUTO_INCREMENT PRIMARY KEY,
        account_type VARCHAR(100) NOT NULL UNIQUE,
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM('Active', 'Inactive') DEFAULT 'Active'
    )";
    
    if ($conn->query($sql) === TRUE) {
        $results['account_type_table'] = 'Created or already exists';
    } else {
        throw new Exception('Error creating account_type table: ' . $conn->error);
    }
    
    // Create tbl_partner if not exists
    $sql = "CREATE TABLE IF NOT EXISTS tbl_partner (
        id INT AUTO_INCREMENT PRIMARY KEY,
        email VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        alias_name VARCHAR(100),
        phone_no VARCHAR(20),
        alternative_phone_no VARCHAR(20),
        partner_type_id INT,
        branch_state_id INT,
        branch_location_id INT,
        office_address TEXT,
        residential_address TEXT,
        aadhaar_number VARCHAR(12),
        pan_number VARCHAR(10),
        account_number VARCHAR(50),
        ifsc_code VARCHAR(11),
        bank_id INT,
        account_type_id INT,
        pan_card_upload VARCHAR(255),
        aadhaar_card_upload VARCHAR(255),
        photo_upload VARCHAR(255),
        bank_proof_upload VARCHAR(255),
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM('Active', 'Inactive') DEFAULT 'Active',
        FOREIGN KEY (partner_type_id) REFERENCES tbl_partner_type(id),
        FOREIGN KEY (branch_state_id) REFERENCES tbl_branch_state(id),
        FOREIGN KEY (branch_location_id) REFERENCES tbl_branch_location(id),
        FOREIGN KEY (bank_id) REFERENCES tbl_bank(id),
        FOREIGN KEY (account_type_id) REFERENCES tbl_account_type(id)
    )";
    
    if ($conn->query($sql) === TRUE) {
        $results['partner_table'] = 'Created or already exists';
    } else {
        throw new Exception('Error creating partner table: ' . $conn->error);
    }
    
    // Add sample partner types
    $partnerTypes = array('Individual', 'Company', 'Partnership', 'LLP', 'Proprietorship', 'Corporation');
    $partnerTypesAdded = 0;
    foreach ($partnerTypes as $type) {
        $sql = "INSERT IGNORE INTO tbl_partner_type (partner_type) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $type);
        if ($stmt->execute() && $stmt->affected_rows > 0) {
            $partnerTypesAdded++;
        }
    }
    $results['partner_types_added'] = $partnerTypesAdded;
    
    // Add sample branch states
    $branchStates = array(
        'Maharashtra', 'Delhi', 'Karnataka', 'Tamil Nadu', 'Gujarat', 
        'Uttar Pradesh', 'West Bengal', 'Telangana', 'Andhra Pradesh', 'Kerala'
    );
    $branchStatesAdded = 0;
    foreach ($branchStates as $state) {
        $sql = "INSERT IGNORE INTO tbl_branch_state (branch_state_name) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $state);
        if ($stmt->execute() && $stmt->affected_rows > 0) {
            $branchStatesAdded++;
        }
    }
    $results['branch_states_added'] = $branchStatesAdded;
    
    // Add sample branch locations
    $branchLocations = array(
        'Mumbai', 'Delhi', 'Bangalore', 'Chennai', 'Ahmedabad', 
        'Lucknow', 'Kolkata', 'Hyderabad', 'Vishakhapatnam', 'Kochi'
    );
    $branchLocationsAdded = 0;
    foreach ($branchLocations as $location) {
        $sql = "INSERT IGNORE INTO tbl_branch_location (branch_location) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $location);
        if ($stmt->execute() && $stmt->affected_rows > 0) {
            $branchLocationsAdded++;
        }
    }
    $results['branch_locations_added'] = $branchLocationsAdded;
    
    // Add sample banks
    $banks = array(
        'State Bank of India', 'HDFC Bank', 'ICICI Bank', 'Axis Bank', 'Punjab National Bank',
        'Bank of Baroda', 'Canara Bank', 'Union Bank of India', 'Bank of India', 'Central Bank of India'
    );
    $banksAdded = 0;
    foreach ($banks as $bank) {
        $sql = "INSERT IGNORE INTO tbl_bank (bank_name) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $bank);
        if ($stmt->execute() && $stmt->affected_rows > 0) {
            $banksAdded++;
        }
    }
    $results['banks_added'] = $banksAdded;
    
    // Add sample account types
    $accountTypes = array(
        'Savings Account', 'Current Account', 'Fixed Deposit', 'Recurring Deposit',
        'Salary Account', 'Business Account', 'NRI Account', 'Joint Account'
    );
    $accountTypesAdded = 0;
    foreach ($accountTypes as $type) {
        $sql = "INSERT IGNORE INTO tbl_account_type (account_type) VALUES (?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $type);
        if ($stmt->execute() && $stmt->affected_rows > 0) {
            $accountTypesAdded++;
        }
    }
    $results['account_types_added'] = $accountTypesAdded;
    
    // Get final counts
    $sql = "SELECT COUNT(*) as count FROM tbl_partner_type";
    $result = $conn->query($sql);
    $results['final_partner_type_count'] = $result->fetch_assoc()['count'];
    
    $sql = "SELECT COUNT(*) as count FROM tbl_branch_state";
    $result = $conn->query($sql);
    $results['final_branch_state_count'] = $result->fetch_assoc()['count'];
    
    $sql = "SELECT COUNT(*) as count FROM tbl_branch_location";
    $result = $conn->query($sql);
    $results['final_branch_location_count'] = $result->fetch_assoc()['count'];
    
    $sql = "SELECT COUNT(*) as count FROM tbl_bank";
    $result = $conn->query($sql);
    $results['final_bank_count'] = $result->fetch_assoc()['count'];
    
    $sql = "SELECT COUNT(*) as count FROM tbl_account_type";
    $result = $conn->query($sql);
    $results['final_account_type_count'] = $result->fetch_assoc()['count'];
    
    echo json_encode([
        'success' => true,
        'message' => 'Partner tables setup completed successfully',
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