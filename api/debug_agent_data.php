<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Check if tbl_agent_data table exists
    $tableCheck = "SHOW TABLES LIKE 'tbl_agent_data'";
    $stmt = $conn->prepare($tableCheck);
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    
    if (!$tableExists) {
        throw new Exception("tbl_agent_data table does not exist");
    }
    
    // Get table structure
    $structureQuery = "DESCRIBE tbl_agent_data";
    $stmt = $conn->prepare($structureQuery);
    $stmt->execute();
    $structure = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_agent_data";
    $stmt = $conn->prepare($countQuery);
    $stmt->execute();
    $totalCount = $stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // Get sample data (first 5 records)
    $sampleQuery = "SELECT * FROM tbl_agent_data LIMIT 5";
    $stmt = $conn->prepare($sampleQuery);
    $stmt->execute();
    $sampleData = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check unique createdBy values
    $createdByQuery = "SELECT DISTINCT createdBy, COUNT(*) as count FROM tbl_agent_data GROUP BY createdBy ORDER BY count DESC LIMIT 10";
    $stmt = $conn->prepare($createdByQuery);
    $stmt->execute();
    $createdByValues = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Check if any createdBy values match usernames in tbl_user
    $usernameMatchQuery = "
        SELECT DISTINCT a.createdBy, COUNT(*) as agent_count
        FROM tbl_agent_data a
        INNER JOIN tbl_user u ON a.createdBy = u.username
        GROUP BY a.createdBy
        ORDER BY agent_count DESC
        LIMIT 10
    ";
    $stmt = $conn->prepare($usernameMatchQuery);
    $stmt->execute();
    $usernameMatches = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $response = [
        'status' => 'success',
        'message' => 'Debug information for tbl_agent_data',
        'table_exists' => $tableExists,
        'total_records' => $totalCount,
        'table_structure' => $structure,
        'sample_data' => $sampleData,
        'created_by_values' => $createdByValues,
        'username_matches' => $usernameMatches
    ];
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?> 