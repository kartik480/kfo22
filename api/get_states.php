<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Include database configuration
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Check if the state table exists
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_state LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
        // Create the table if it doesn't exist
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_state (
            id INT AUTO_INCREMENT PRIMARY KEY,
            state_name VARCHAR(255) NOT NULL UNIQUE,
            status ENUM('active', 'inactive') DEFAULT 'active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        )";
        
        $conn->exec($createTableSQL);
        
        // Insert some sample Indian states
        $sampleStates = [
            'Andhra Pradesh',
            'Arunachal Pradesh',
            'Assam',
            'Bihar',
            'Chhattisgarh',
            'Goa',
            'Gujarat',
            'Haryana',
            'Himachal Pradesh',
            'Jharkhand',
            'Karnataka',
            'Kerala',
            'Madhya Pradesh',
            'Maharashtra',
            'Manipur',
            'Meghalaya',
            'Mizoram',
            'Nagaland',
            'Odisha',
            'Punjab',
            'Rajasthan',
            'Sikkim',
            'Tamil Nadu',
            'Telangana',
            'Tripura',
            'Uttar Pradesh',
            'Uttarakhand',
            'West Bengal'
        ];
        
        $insertStmt = $conn->prepare("INSERT INTO tbl_state (state_name) VALUES (?)");
        
        foreach ($sampleStates as $state) {
            try {
                $insertStmt->execute([$state]);
            } catch (PDOException $e) {
                // Ignore duplicate errors
                if ($e->getCode() != 23000) {
                    throw $e;
                }
            }
        }
    }
    
    // Fetch all states from tbl_state table using state_name column
    $stmt = $conn->prepare("SELECT state_name FROM tbl_state ORDER BY state_name ASC");
    $stmt->execute();
    $states = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'States fetched successfully.',
        'states' => $states
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get states error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching states: ' . $e->getMessage()
    ));
}
?> 