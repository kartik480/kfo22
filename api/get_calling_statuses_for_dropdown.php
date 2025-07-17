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
    
    // Check if table exists, if not create it with sample data
    $tableExists = false;
    try {
        $checkQuery = $conn->query("SELECT 1 FROM tbl_partner_calling_status LIMIT 1");
        $tableExists = true;
    } catch (Exception $e) {
        $tableExists = false;
    }
    
    if (!$tableExists) {
        // Create the table
        $createSQL = "
        CREATE TABLE IF NOT EXISTS tbl_partner_calling_status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            calling_status VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        $conn->exec($createSQL);
        
        // Insert sample data
        $sampleStatuses = [
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
        
        foreach ($sampleStatuses as $status) {
            try {
                $insertStmt->execute([$status]);
            } catch (PDOException $e) {
                // Ignore duplicate errors
                if ($e->getCode() != 23000) {
                    throw $e;
                }
            }
        }
    }
    
    // Fetch calling statuses for dropdown
    $stmt = $conn->prepare("SELECT calling_status FROM tbl_partner_calling_status ORDER BY calling_status ASC");
    $stmt->execute();
    $callingStatuses = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Calling statuses fetched successfully for dropdown.',
        'calling_statuses' => $callingStatuses
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get calling statuses for dropdown error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'Error: ' . $e->getMessage()
    ));
}
?> 