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
    // Test database connection first
    $conn = getConnection();
    
    if (!$conn) {
        throw new Exception("Database connection failed");
    }
    
    // Check if the location table exists
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_location LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
        // Create the table if it doesn't exist
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            location VARCHAR(255) NOT NULL,
            state_id INT,
            status ENUM('active', 'inactive') DEFAULT 'active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        )";
        
        $result = $conn->exec($createTableSQL);
        
        if ($result === false) {
            throw new Exception("Failed to create tbl_location table");
        }
        
        // Insert some sample locations
        $sampleLocations = [
            'Mumbai',
            'Delhi',
            'Bangalore',
            'Hyderabad',
            'Chennai',
            'Kolkata',
            'Pune',
            'Ahmedabad',
            'Jaipur',
            'Surat',
            'Lucknow',
            'Kanpur',
            'Nagpur',
            'Indore',
            'Thane',
            'Bhopal',
            'Visakhapatnam',
            'Pimpri-Chinchwad',
            'Patna',
            'Vadodara'
        ];
        
        $insertStmt = $conn->prepare("INSERT INTO tbl_location (location) VALUES (?)");
        
        foreach ($sampleLocations as $location) {
            try {
                $insertStmt->execute([$location]);
            } catch (PDOException $e) {
                // Ignore duplicate errors
                if ($e->getCode() != 23000) {
                    throw $e;
                }
            }
        }
    }
    
    // Fetch all locations
    $stmt = $conn->prepare("SELECT location FROM tbl_location WHERE status = 'active' ORDER BY location ASC");
    $stmt->execute();
    $locations = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Locations fetched successfully.',
        'data' => array_map(function($location) {
            return array('locationName' => $location);
        }, $locations)
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get locations error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching locations: ' . $e->getMessage(),
        'details' => 'Please check the server logs for more information'
    ));
}
?> 