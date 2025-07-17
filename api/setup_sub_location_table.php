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
    
    // Check if table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sub_location'");
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    
    if ($tableExists) {
        echo json_encode(array(
            'success' => true,
            'message' => 'Table tbl_sub_location already exists.',
            'action' => 'none'
        ));
        exit();
    }
    
    // Create the table
    $createTableSQL = "
        CREATE TABLE tbl_sub_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            sub_location VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    ";
    
    $stmt = $conn->prepare($createTableSQL);
    $stmt->execute();
    
    // Add some sample data
    $sampleSubLocations = array(
        'Downtown',
        'Uptown',
        'West Side',
        'East Side',
        'North District',
        'South District',
        'Central Business District',
        'Residential Area',
        'Industrial Zone',
        'Commercial Hub'
    );
    
    $insertStmt = $conn->prepare("INSERT INTO tbl_sub_location (sub_location) VALUES (?)");
    $insertedCount = 0;
    
    foreach ($sampleSubLocations as $subLocation) {
        try {
            $insertStmt->execute(array($subLocation));
            $insertedCount++;
        } catch (PDOException $e) {
            // Ignore duplicate key errors
            if ($e->getCode() != 23000) {
                throw $e;
            }
        }
    }
    
    echo json_encode(array(
        'success' => true,
        'message' => "Table tbl_sub_location created successfully with $insertedCount sample sub locations.",
        'action' => 'created',
        'sample_data_added' => $insertedCount
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Setup sub location table error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while setting up sub location table: ' . $e->getMessage()
    ));
}
?> 