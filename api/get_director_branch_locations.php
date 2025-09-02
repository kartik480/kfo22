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
    
    // Check if table exists
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_branch_location'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        echo json_encode([
            'success' => false,
            'message' => 'tbl_branch_location table does not exist',
            'branch_locations' => []
        ]);
        exit;
    }
    
    // Get branch locations
    $sql = "SELECT * FROM tbl_branch_location ORDER BY branch_location ASC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $branchLocations = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Director branch locations fetched successfully',
        'data' => [
            'branch_locations' => $branchLocations
        ],
        'count' => count($branchLocations)
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'branch_locations' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'branch_locations' => []
    ]);
}
?>
