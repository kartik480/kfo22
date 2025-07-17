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
    
    $results = array();
    
    // Step 1: Check if table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sub_location'");
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    $results['table_exists'] = $tableExists;
    
    if (!$tableExists) {
        // Step 2: Create the table
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
        $results['table_created'] = true;
        $results['table_creation_message'] = 'Table tbl_sub_location created successfully';
    } else {
        $results['table_created'] = false;
        $results['table_creation_message'] = 'Table already exists';
    }
    
    // Step 3: Check table structure
    $stmt = $conn->prepare("DESCRIBE tbl_sub_location");
    $stmt->execute();
    $structure = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $results['table_structure'] = $structure;
    
    // Step 4: Check if data exists
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sub_location");
    $stmt->execute();
    $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    $results['total_records'] = $totalCount;
    
    // Step 5: Add sample data if table is empty
    if ($totalCount == 0) {
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
        
        foreach ($sampleSubLocations as $subLocation) {
            try {
                $insertStmt->execute(array($subLocation));
                $insertedCount++;
            } catch (PDOException $e) {
                // Ignore duplicate key errors
                if ($e->getCode() != 23000) {
                    throw $e;
                }
            }
        }
        
        $results['sample_data_added'] = true;
        $results['inserted_count'] = $insertedCount;
        $results['sample_data_message'] = "Added $insertedCount sample sub locations";
    } else {
        $results['sample_data_added'] = false;
        $results['inserted_count'] = 0;
        $results['sample_data_message'] = 'Table already has data';
    }
    
    // Step 6: Test the exact query that the API uses
    $stmt = $conn->prepare("SELECT sub_location AS subLocationName FROM tbl_sub_location ORDER BY sub_location ASC LIMIT 5");
    $stmt->execute();
    $testData = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $results['test_query_success'] = true;
    $results['test_data'] = $testData;
    
    // Step 7: Get final count
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sub_location");
    $stmt->execute();
    $finalCountResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $finalCount = $finalCountResult['total'];
    $results['final_total_records'] = $finalCount;
    
    echo json_encode(array(
        'success' => true,
        'message' => 'Sub location table check and fix completed successfully',
        'results' => $results,
        'summary' => array(
            'table_exists' => $tableExists,
            'table_created' => $results['table_created'],
            'total_records' => $finalCount,
            'sample_data_added' => $results['sample_data_added'],
            'test_query_working' => $results['test_query_success']
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Check and fix sub location error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while checking and fixing sub location table: ' . $e->getMessage()
    ));
}
?> 