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
    
    // Check if table exists, if not create it
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sub_location'");
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    
    if (!$tableExists) {
        // Create the table first
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
    }
    
    // Sample sub locations data
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
        'Commercial Hub',
        'Suburban Area',
        'Rural District',
        'Business Park',
        'Shopping Center',
        'Educational Zone',
        'Healthcare District',
        'Entertainment District',
        'Transportation Hub',
        'Government Center',
        'Financial District'
    );
    
    $insertStmt = $conn->prepare("INSERT INTO tbl_sub_location (sub_location) VALUES (?)");
    $insertedCount = 0;
    $skippedCount = 0;
    
    foreach ($sampleSubLocations as $subLocation) {
        try {
            $insertStmt->execute(array($subLocation));
            $insertedCount++;
        } catch (PDOException $e) {
            // Ignore duplicate key errors
            if ($e->getCode() == 23000) {
                $skippedCount++;
            } else {
                throw $e;
            }
        }
    }
    
    // Get total count
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sub_location");
    $stmt->execute();
    $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    echo json_encode(array(
        'success' => true,
        'message' => "Successfully added $insertedCount new sub locations. Skipped $skippedCount duplicates. Total sub locations: $totalCount",
        'inserted_count' => $insertedCount,
        'skipped_count' => $skippedCount,
        'total_count' => $totalCount,
        'table_created' => !$tableExists
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Add sample sub locations error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding sample sub locations: ' . $e->getMessage()
    ));
}
?> 