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
    
    // Check if tbl_branch_location table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_branch_location'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        // Create the table if it doesn't exist
        $createTableSQL = "
        CREATE TABLE IF NOT EXISTS tbl_branch_location (
            id INT AUTO_INCREMENT PRIMARY KEY,
            branch_location VARCHAR(255) NOT NULL UNIQUE,
            branch_state_id INT,
            status TINYINT(1) DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (branch_state_id) REFERENCES tbl_branch_state(id) ON DELETE SET NULL
        )";
        
        $conn->exec($createTableSQL);
        
        echo json_encode(array(
            'success' => true,
            'message' => 'tbl_branch_location table created successfully',
            'branch_locations' => []
        ));
    } else {
        // Fetch branch locations with all required columns and related data
        $stmt = $conn->prepare("
            SELECT 
                bl.id,
                bl.branch_location,
                bl.branch_state_id,
                bl.status,
                bl.created_at,
                bs.branch_state_name
            FROM tbl_branch_location bl 
            LEFT JOIN tbl_branch_state bs ON bl.branch_state_id = bs.id 
            ORDER BY bl.branch_location ASC
        ");
        $stmt->execute();
        $branchLocations = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Format the response for the Android app
        $formattedBranchLocations = [];
        foreach ($branchLocations as $branchLocation) {
            // Convert numeric status to text (1 = Active, 0 = Inactive)
            $statusText = ($branchLocation['status'] == 1) ? 'Active' : 'Inactive';
            
            $formattedBranchLocations[] = array(
                'id' => $branchLocation['id'],
                'branch_location' => $branchLocation['branch_location'],
                'branch_state_id' => $branchLocation['branch_state_id'],
                'status' => $statusText,
                'status_value' => $branchLocation['status'], // Keep the numeric value for reference
                'branch_state_name' => $branchLocation['branch_state_name'] ?: 'Unknown',
                'created_at' => $branchLocation['created_at']
            );
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Branch locations fetched successfully from tbl_branch_location table',
            'total_branch_locations' => count($formattedBranchLocations),
            'branch_locations' => $formattedBranchLocations
        ));
    }
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Get branch locations with states error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database error: ' . $e->getMessage()
    ));
}
?> 