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
    
    // First, let's see what designations exist
    $query1 = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name";
    $stmt1 = $pdo->prepare($query1);
    $stmt1->execute();
    $designations = $stmt1->fetchAll(PDO::FETCH_ASSOC);
    
    // Now let's see what users exist with their designations
    $query2 = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.designation_id,
                d.designation_name
              FROM tbl_user u
              LEFT JOIN tbl_designation d ON u.designation_id = d.id
              ORDER BY d.designation_name, u.firstName
              LIMIT 20";
    
    $stmt2 = $pdo->prepare($query2);
    $stmt2->execute();
    $users = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    
    // Let's also check if there are any users with the specific designations we're looking for
    $query3 = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.designation_id,
                d.designation_name
              FROM tbl_user u
              INNER JOIN tbl_designation d ON u.designation_id = d.id
              WHERE d.designation_name LIKE '%Business%' 
              OR d.designation_name LIKE '%Director%'
              OR d.designation_name LIKE '%Head%'
              ORDER BY d.designation_name, u.firstName";
    
    $stmt3 = $pdo->prepare($query3);
    $stmt3->execute();
    $filteredUsers = $stmt3->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'all_designations' => $designations,
        'sample_users' => $users,
        'filtered_users' => $filteredUsers,
        'total_designations' => count($designations),
        'total_users' => count($users),
        'total_filtered' => count($filteredUsers)
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage()
    ]);
}
?>
