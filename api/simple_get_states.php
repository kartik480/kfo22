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

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create connection using PDO
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, check if tbl_state table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_state'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
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
        
        // Insert sample states
        $sampleStates = [
            'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh',
            'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand',
            'Karnataka', 'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur',
            'Meghalaya', 'Mizoram', 'Nagaland', 'Odisha', 'Punjab',
            'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura',
            'Uttar Pradesh', 'Uttarakhand', 'West Bengal'
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
    
    // Now fetch all states from tbl_state table
    $stmt = $conn->prepare("SELECT state_name FROM tbl_state ORDER BY state_name ASC");
    $stmt->execute();
    $states = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
    
    // Return the response in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'States fetched successfully from tbl_state table',
        'total_states' => count($states),
        'states' => $states
    ));
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Simple get states error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database error: ' . $e->getMessage()
    ));
}
?> 