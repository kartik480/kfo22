<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database connection failed: ' . $e->getMessage()]);
    exit;
}

try {
    // Get CBO, RBH, and Director users specifically
    $query = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.designation_id,
                u.email_id,
                u.mobile,
                d.designation_name
              FROM tbl_user u
              INNER JOIN tbl_designation d ON u.designation_id = d.id
              WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
              ORDER BY d.designation_name, u.firstName, u.lastName";

    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Add status field for compatibility
    foreach ($users as &$user) {
        $user['status'] = 'active';
        $user['emailId'] = $user['email_id']; // Add alias for compatibility
    }

    // Return direct JSON array
    echo json_encode($users, JSON_PRETTY_PRINT);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Server error: ' . $e->getMessage()
    ]);
}
?>
