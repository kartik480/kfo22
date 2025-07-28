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
    
    // Get all users reporting to ID 8 (without status filter)
    $query = "SELECT 
                id,
                username,
                first_name,
                last_name,
                Phone_number,
                email_id,
                employee_no,
                department,
                designation,
                branchloaction,
                status,
                reportingTo
              FROM tbl_sdsa_users 
              WHERE reportingTo = '8'
              ORDER BY first_name, last_name";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get count of all users reporting to ID 8
    $countQuery = "SELECT COUNT(*) as total FROM tbl_sdsa_users WHERE reportingTo = '8'";
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute();
    $totalCount = $countStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get count by status
    $statusQuery = "SELECT status, COUNT(*) as count 
                    FROM tbl_sdsa_users 
                    WHERE reportingTo = '8' 
                    GROUP BY status";
    $statusStmt = $pdo->prepare($statusQuery);
    $statusStmt->execute();
    $statusCounts = $statusStmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Debug: All users reporting to ID 8',
        'data' => [
            'total_users_reporting_to_8' => $totalCount['total'],
            'users' => $users,
            'status_breakdown' => $statusCounts
        ]
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