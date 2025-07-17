<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if tbl_branch_location table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_branch_location'");
    if ($stmt->rowCount() == 0) {
        echo json_encode(array(
            'status' => 'error',
            'message' => 'Table tbl_branch_location does not exist'
        ));
        exit;
    }
    
    $sql = "SELECT id, branch_location FROM tbl_branch_location ORDER BY branch_location ASC";
    $result = $pdo->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed");
    }
    
    $branchLocations = array();
    while($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $branchLocations[] = array(
            'id' => $row['id'],
            'name' => $row['branch_location']
        );
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $branchLocations,
        'message' => 'Branch locations fetched successfully'
    ));
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => $e->getMessage()
    ));
}
?> 