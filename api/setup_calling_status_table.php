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
require_once 'db_config_fixed.php';

try {
    $conn = getConnection();
    
    // Check if table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_partner_calling_status'");
    $tableExists = $tableCheck->rowCount() > 0;
    
    if (!$tableExists) {
        // Create the table
        $createTableSQL = "
        CREATE TABLE tbl_partner_calling_status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            calling_status VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        )";
        
        $conn->exec($createTableSQL);
        
        // Insert some sample data
        $sampleData = [
            'Interested',
            'Not Interested', 
            'Call Back Later',
            'Busy',
            'No Answer',
            'Wrong Number',
            'Meeting Scheduled',
            'Follow Up Required'
        ];
        
        $insertStmt = $conn->prepare("INSERT INTO tbl_partner_calling_status (calling_status) VALUES (?)");
        
        foreach ($sampleData as $status) {
            try {
                $insertStmt->execute([$status]);
            } catch (PDOException $e) {
                // Ignore duplicate key errors
                if ($e->getCode() != 23000) {
                    throw $e;
                }
            }
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Table tbl_partner_calling_status created successfully with sample data',
            'setup_info' => array(
                'table_created' => true,
                'sample_data_inserted' => count($sampleData),
                'sample_statuses' => $sampleData
            )
        ));
    } else {
        // Table exists, check structure
        $structureQuery = $conn->query("DESCRIBE tbl_partner_calling_status");
        $structure = $structureQuery->fetchAll(PDO::FETCH_ASSOC);
        
        // Count existing records
        $countQuery = $conn->query("SELECT COUNT(*) as count FROM tbl_partner_calling_status");
        $count = $countQuery->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode(array(
            'success' => true,
            'message' => 'Table tbl_partner_calling_status already exists',
            'setup_info' => array(
                'table_exists' => true,
                'table_structure' => $structure,
                'existing_records' => $count['count']
            )
        ));
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Setup calling status table error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'Setup error: ' . $e->getMessage(),
        'setup_info' => array(
            'error_type' => get_class($e),
            'error_message' => $e->getMessage(),
            'error_file' => $e->getFile(),
            'error_line' => $e->getLine()
        )
    ));
}
?> 