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

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array('success' => false, 'error' => 'Method not allowed'));
    exit();
}

try {
    // Create connection using PDO
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $input = $_POST;
    }
    
    $subLocationName = trim($input['sub_location'] ?? '');
    $stateName = trim($input['state'] ?? '');
    $locationName = trim($input['location'] ?? '');
    
    // Validate input
    if (empty($subLocationName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Sub location name is required'));
        exit();
    }
    
    if (empty($stateName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'State is required'));
        exit();
    }
    
    if (empty($locationName)) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Location is required'));
        exit();
    }
    
    // Check if the sub_location table exists, create if not
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_sub_location LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
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
    }
    
    // Get state_id from state name
    $stateId = null;
    $stateStmt = $conn->prepare("SELECT id FROM tbl_state WHERE state_name = ?");
    $stateStmt->execute([$stateName]);
    $stateResult = $stateStmt->fetch(PDO::FETCH_ASSOC);
    if ($stateResult) {
        $stateId = $stateResult['id'];
    } else {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'State not found: ' . $stateName));
        exit();
    }
    
    // Get location_id from location name
    $locationId = null;
    $locationStmt = $conn->prepare("SELECT id FROM tbl_location WHERE location = ?");
    $locationStmt->execute([$locationName]);
    $locationResult = $locationStmt->fetch(PDO::FETCH_ASSOC);
    if ($locationResult) {
        $locationId = $locationResult['id'];
    } else {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Location not found: ' . $locationName));
        exit();
    }
    
    // Check if sub location already exists
    $checkStmt = $conn->prepare("SELECT id FROM tbl_sub_location WHERE sub_location = ?");
    $checkStmt->execute([$subLocationName]);
    
    if ($checkStmt->fetch()) {
        http_response_code(409);
        echo json_encode(array('success' => false, 'error' => 'Sub location already exists'));
        exit();
    }
    
    // Insert new sub location
    $insertStmt = $conn->prepare("INSERT INTO tbl_sub_location (sub_location, state_id, location_id) VALUES (?, ?, ?)");
    $insertStmt->execute([$subLocationName, $stateId, $locationId]);
    
    $subLocationId = $conn->lastInsertId();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Sub location added successfully',
        'data' => array(
            'id' => $subLocationId,
            'sub_location' => $subLocationName,
            'state_id' => $stateId,
            'location_id' => $locationId,
            'state_name' => $stateName,
            'location_name' => $locationName
        )
    ));
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Add sub location error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while adding sub location: ' . $e->getMessage()
    ));
}
?> 