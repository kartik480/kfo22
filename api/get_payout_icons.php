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
    
    // Fetch all payout_icons from tbl_user
    $sql = "SELECT DISTINCT payout_icons FROM tbl_user WHERE payout_icons IS NOT NULL AND payout_icons != ''";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    
    $payout_icons = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    if ($payout_icons) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Payout icons fetched successfully',
            'data' => $payout_icons,
            'count' => count($payout_icons)
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout icons found',
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
