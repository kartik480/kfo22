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
    $checkTable = $conn->prepare("SHOW TABLES LIKE 'tbl_partner_type'");
    $checkTable->execute();
    $tableExists = $checkTable->fetch();
    
    if (!$tableExists) {
        echo json_encode([
            'success' => false,
            'message' => 'tbl_partner_type table does not exist',
            'partner_types' => []
        ]);
        exit;
    }
    
    // Get partner types
    $sql = "SELECT * FROM tbl_partner_type ORDER BY partner_type ASC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $partnerTypes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $response = [
        'success' => true,
        'message' => 'Director partner types fetched successfully',
        'data' => [
            'partner_types' => $partnerTypes
        ],
        'count' => count($partnerTypes)
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'partner_types' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'partner_types' => []
    ]);
}
?>
