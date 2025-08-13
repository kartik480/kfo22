<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database connection
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$username = "emp_kfinone";
$password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Fetch all payout names from tbl_payout_type
    $sql = "SELECT DISTINCT payout_name FROM tbl_payout_type WHERE payout_name IS NOT NULL AND payout_name != ''";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    
    $payout_names = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    if ($payout_names) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Payout names fetched successfully',
            'data' => $payout_names,
            'count' => count($payout_names)
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout names found',
            'data' => [],
            'count' => 0
        ]);
    }
    
} catch(PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage()
    ]);
}

$conn = null;
?>
