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
    
    $response = [
        'success' => true,
        'message' => 'Database connection successful',
        'data' => []
    ];
    
    // Check if tbl_portfolio table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_portfolio'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if ($tableExists) {
        $response['data']['portfolio_table_exists'] = true;
        
        // Get table structure
        $structureQuery = "DESCRIBE tbl_portfolio";
        $structureStmt = $conn->prepare($structureQuery);
        $structureStmt->execute();
        $structure = $structureStmt->fetchAll(PDO::FETCH_ASSOC);
        $response['data']['portfolio_table_structure'] = $structure;
        
        // Get row count
        $countQuery = "SELECT COUNT(*) as total FROM tbl_portfolio";
        $countStmt = $conn->prepare($countQuery);
        $countStmt->execute();
        $count = $countStmt->fetch(PDO::FETCH_ASSOC);
        $response['data']['portfolio_count'] = $count['total'];
        
        // Get sample data (first 3 rows)
        $sampleQuery = "SELECT * FROM tbl_portfolio LIMIT 3";
        $sampleStmt = $conn->prepare($sampleQuery);
        $sampleStmt->execute();
        $sampleData = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);
        $response['data']['sample_data'] = $sampleData;
        
    } else {
        $response['data']['portfolio_table_exists'] = false;
        $response['data']['message'] = 'tbl_portfolio table does not exist';
    }
    
    // Check if tbl_user table exists
    $checkUserTable = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
    $checkUserTable->execute();
    $userTableExists = $checkUserTable->fetch();
    
    if ($userTableExists) {
        $response['data']['user_table_exists'] = true;
        
        // Get user table structure
        $userStructureQuery = "DESCRIBE tbl_user";
        $userStructureStmt = $conn->prepare($userStructureQuery);
        $userStructureStmt->execute();
        $userStructure = $userStructureStmt->fetchAll(PDO::FETCH_ASSOC);
        $response['data']['user_table_structure'] = $userStructure;
        
        // Get user count
        $userCountQuery = "SELECT COUNT(*) as total FROM tbl_user";
        $userCountStmt = $conn->prepare($userCountQuery);
        $userCountStmt->execute();
        $userCount = $userCountStmt->fetch(PDO::FETCH_ASSOC);
        $response['data']['user_count'] = $userCount['total'];
        
    } else {
        $response['data']['user_table_exists'] = false;
    }
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ], JSON_PRETTY_PRINT);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ], JSON_PRETTY_PRINT);
}
?>
