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

try {
    // Include database configuration
    require_once 'db_config.php';
    
    $conn = getConnection();
    
    // Check if tbl_state table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_state'");
    $stmt->execute();
    $tableExists = $stmt->fetch();
    
    if (!$tableExists) {
        echo json_encode(array(
            'success' => false,
            'error' => 'tbl_state table does not exist',
            'message' => 'Creating tbl_state table...'
        ));
        
        // Create the table
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
        
        echo json_encode(array(
            'success' => true,
            'message' => 'tbl_state table created and populated with sample data',
            'states_added' => count($sampleStates)
        ));
        
    } else {
        // Table exists, check its structure and data
        $stmt = $conn->prepare("DESCRIBE tbl_state");
        $stmt->execute();
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Count states
        $stmt = $conn->prepare("SELECT COUNT(*) as count FROM tbl_state");
        $stmt->execute();
        $stateCount = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Get all states
        $stmt = $conn->prepare("SELECT state_name FROM tbl_state ORDER BY state_name ASC");
        $stmt->execute();
        $states = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
        
        echo json_encode(array(
            'success' => true,
            'message' => 'tbl_state table exists and has data',
            'table_structure' => $columns,
            'total_states' => $stateCount['count'],
            'states' => $states
        ));
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Debug tbl_state error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'Database error: ' . $e->getMessage()
    ));
}
?> 