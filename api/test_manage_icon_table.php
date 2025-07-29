<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
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
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Test 1: Check if table exists
    $tableQuery = "SHOW TABLES LIKE 'tbl_manage_icon'";
    $tableStmt = $pdo->prepare($tableQuery);
    $tableStmt->execute();
    $tableExists = $tableStmt->rowCount() > 0;

    // Test 2: Get table structure
    $structureQuery = "DESCRIBE tbl_manage_icon";
    $structureStmt = $pdo->prepare($structureQuery);
    $structureStmt->execute();
    $structure = $structureStmt->fetchAll(PDO::FETCH_ASSOC);

    // Test 3: Get sample data
    $sampleQuery = "SELECT * FROM tbl_manage_icon LIMIT 5";
    $sampleStmt = $pdo->prepare($sampleQuery);
    $sampleStmt->execute();
    $sampleData = $sampleStmt->fetchAll(PDO::FETCH_ASSOC);

    // Test 4: Get total count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_manage_icon";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];

    echo json_encode([
        'success' => true,
        'message' => 'Table test completed',
        'data' => [
            'table_exists' => $tableExists,
            'table_structure' => $structure,
            'sample_data' => $sampleData,
            'total_records' => $totalCount
        ]
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage(),
        'data' => []
    ]);
}
?> 