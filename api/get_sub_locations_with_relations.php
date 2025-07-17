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
    
    // Check if tbl_sub_location table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_sub_location'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        // Create the table if it doesn't exist
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_sub_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            sub_location VARCHAR(255) NOT NULL UNIQUE,
            state_id INT,
            location_id INT,
            status TINYINT(1) DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (state_id) REFERENCES tbl_state(id) ON DELETE SET NULL,
            FOREIGN KEY (location_id) REFERENCES tbl_location(id) ON DELETE SET NULL
        )";
        
        $conn->exec($createTableSQL);
        
        echo json_encode(array(
            'success' => true,
            'message' => 'tbl_sub_location table created successfully',
            'sub_locations' => []
        ));
    } else {
        // Fetch sub locations with all required columns and related data
        $stmt = $conn->prepare("
            SELECT 
                sl.id,
                sl.sub_location,
                sl.state_id,
                sl.location_id,
                sl.status,
                s.state_name,
                l.location as location_name
            FROM tbl_sub_location sl 
            LEFT JOIN tbl_state s ON sl.state_id = s.id 
            LEFT JOIN tbl_location l ON sl.location_id = l.id 
            ORDER BY sl.sub_location ASC
        ");
        $stmt->execute();
        $subLocations = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Format the response for the Android app
        $formattedSubLocations = [];
        foreach ($subLocations as $subLoc) {
            // Convert numeric status to text (1 = Active, 0 = Inactive)
            $statusText = ($subLoc['status'] == 1) ? 'Active' : 'Inactive';
            
            $formattedSubLocations[] = array(
                'id' => $subLoc['id'],
                'sub_location' => $subLoc['sub_location'],
                'state_id' => $subLoc['state_id'],
                'location_id' => $subLoc['location_id'],
                'status' => $statusText,
                'status_value' => $subLoc['status'], // Keep the numeric value for reference
                'state_name' => $subLoc['state_name'] ?: 'Unknown',
                'location_name' => $subLoc['location_name'] ?: 'Unknown'
            );
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Sub locations fetched successfully from tbl_sub_location table',
            'total_sub_locations' => count($formattedSubLocations),
            'sub_locations' => $formattedSubLocations
        ));
    }
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Get sub locations with relations error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database error: ' . $e->getMessage()
    ));
}
?> 