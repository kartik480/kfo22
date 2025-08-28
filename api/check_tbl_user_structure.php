<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response = array();
    
    try {
        // Get table structure
        $stmt = $pdo->prepare("DESCRIBE tbl_user");
        $stmt->execute();
        $tableStructure = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Get sample data (first 3 rows)
        $stmt = $pdo->prepare("SELECT * FROM tbl_user LIMIT 3");
        $stmt->execute();
        $sampleData = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Get total count
        $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM tbl_user");
        $stmt->execute();
        $userCount = $stmt->fetch(PDO::FETCH_ASSOC);
        
        $response['status'] = 'success';
        $response['message'] = 'Table structure retrieved successfully';
        $response['table_structure'] = $tableStructure;
        $response['sample_data'] = $sampleData;
        $response['total_users'] = $userCount['total'];
        
    } catch (Exception $e) {
        $response['status'] = 'error';
        $response['message'] = 'Query error: ' . $e->getMessage();
    }
    
    echo json_encode($response, JSON_PRETTY_PRINT);
    
} catch (Exception $e) {
    $response = array(
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage()
    );
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
