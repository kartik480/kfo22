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
    
    // Check if tbl_pincode table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_pincode'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        // Create the table if it doesn't exist
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_pincode (
            id INT AUTO_INCREMENT PRIMARY KEY,
            pincode VARCHAR(6) NOT NULL UNIQUE,
            state_id INT,
            location_id INT,
            sub_location_id INT,
            status TINYINT(1) DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (state_id) REFERENCES tbl_state(id) ON DELETE SET NULL,
            FOREIGN KEY (location_id) REFERENCES tbl_location(id) ON DELETE SET NULL,
            FOREIGN KEY (sub_location_id) REFERENCES tbl_sub_location(id) ON DELETE SET NULL
        )";
        
        $conn->exec($createTableSQL);
        
        echo json_encode(array(
            'success' => true,
            'message' => 'tbl_pincode table created successfully',
            'pincodes' => []
        ));
    } else {
        // Fetch pincodes with all required columns and related data
        $stmt = $conn->prepare("
            SELECT 
                p.id,
                p.pincode,
                p.state_id,
                p.location_id,
                p.sub_location_id,
                p.status,
                s.state_name,
                l.location as location_name,
                sl.sub_location as sub_location_name
            FROM tbl_pincode p 
            LEFT JOIN tbl_state s ON p.state_id = s.id 
            LEFT JOIN tbl_location l ON p.location_id = l.id 
            LEFT JOIN tbl_sub_location sl ON p.sub_location_id = sl.id 
            ORDER BY p.pincode ASC
        ");
        $stmt->execute();
        $pincodes = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Format the response for the Android app
        $formattedPincodes = [];
        foreach ($pincodes as $pincode) {
            // Convert numeric status to text (1 = Active, 0 = Inactive)
            $statusText = ($pincode['status'] == 1) ? 'Active' : 'Inactive';
            
            $formattedPincodes[] = array(
                'id' => $pincode['id'],
                'pincode' => $pincode['pincode'],
                'state_id' => $pincode['state_id'],
                'location_id' => $pincode['location_id'],
                'sub_location_id' => $pincode['sub_location_id'],
                'status' => $statusText,
                'status_value' => $pincode['status'], // Keep the numeric value for reference
                'state_name' => $pincode['state_name'] ?: 'Unknown',
                'location_name' => $pincode['location_name'] ?: 'Unknown',
                'sub_location_name' => $pincode['sub_location_name'] ?: 'Unknown'
            );
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Pincodes fetched successfully from tbl_pincode table',
            'total_pincodes' => count($formattedPincodes),
            'pincodes' => $formattedPincodes
        ));
    }
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Get pincodes with relations error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database error: ' . $e->getMessage()
    ));
}
?> 