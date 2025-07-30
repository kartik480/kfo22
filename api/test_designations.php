<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get all designations
    $stmt = $pdo->query("SELECT id, designation_name FROM tbl_designation ORDER BY designation_name ASC");
    $designations = array();
    while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $designations[] = array(
            'id' => $row['id'],
            'name' => $row['designation_name']
        );
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Designations fetched successfully',
        'data' => $designations,
        'count' => count($designations)
    ));
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 