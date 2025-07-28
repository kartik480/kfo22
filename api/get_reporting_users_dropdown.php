<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
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
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get active users for reporting dropdown
    // Include users who are Active, have NULL status, or have empty status
    $query = "SELECT id, firstName, lastName 
              FROM tbl_user 
              WHERE (status = 'Active' OR status IS NULL OR status = '' OR status != 'Inactive')
                    AND firstName IS NOT NULL 
                    AND firstName != '' 
                    AND lastName IS NOT NULL 
                    AND lastName != ''
              ORDER BY firstName ASC, lastName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the data for dropdown
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'name' => trim($user['firstName'] . ' ' . $user['lastName'])
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Reporting users fetched successfully',
        'data' => $formattedUsers,
        'total_users' => count($formattedUsers)
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 