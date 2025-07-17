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
    
    // Check if tbl_location table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_location'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        // Create the table if it doesn't exist
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            location VARCHAR(255) NOT NULL UNIQUE,
            state_id INT,
            status ENUM('active', 'inactive') DEFAULT 'active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (state_id) REFERENCES tbl_state(id) ON DELETE SET NULL
        )";
        
        $conn->exec($createTableSQL);
        
        echo json_encode(array(
            'success' => true,
            'message' => 'tbl_location table created successfully',
            'locations' => []
        ));
    } else {
        // Fetch locations with their state names
        $stmt = $conn->prepare("
            SELECT l.location, s.state_name as state_name, l.status 
            FROM tbl_location l 
            LEFT JOIN tbl_state s ON l.state_id = s.id 
            ORDER BY l.location ASC
        ");
        $stmt->execute();
        $locations = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Format the response for the Android app
        $formattedLocations = [];
        foreach ($locations as $loc) {
            $formattedLocations[] = array(
                'name' => $loc['location'],
                'state' => $loc['state_name'] ?: 'Unknown',
                'status' => $loc['status'] === 'active' ? 'Active' : 'Inactive'
            );
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Locations fetched successfully from tbl_location table',
            'total_locations' => count($formattedLocations),
            'locations' => $formattedLocations
        ));
    }
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Get locations with states error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database error: ' . $e->getMessage()
    ));
}
?> 