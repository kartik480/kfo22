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
    
    $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '21';
    
    // Check what's in payout_icons for this specific user
    $user_sql = "SELECT id, username, firstName, lastName, payout_icons FROM tbl_user WHERE id = :user_id";
    $user_stmt = $conn->prepare($user_sql);
    $user_stmt->bindParam(':user_id', $user_id);
    $user_stmt->execute();
    $user_data = $user_stmt->fetch(PDO::FETCH_ASSOC);
    
    // Also check all users with payout_icons to see the pattern
    $all_sql = "SELECT id, username, firstName, lastName, payout_icons FROM tbl_user WHERE payout_icons IS NOT NULL AND payout_icons != '' LIMIT 10";
    $all_stmt = $conn->prepare($all_sql);
    $all_stmt->execute();
    $all_users = $all_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'User payout_icons data',
        'target_user' => $user_data,
        'sample_users_with_payout_icons' => $all_users,
        'target_user_id' => $user_id
    ]);
    
} catch(PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage()
    ]);
}

$conn = null;
?>
