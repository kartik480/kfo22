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
    
    // Since reportingTo contains user IDs, we'll search for user ID "8" directly
    $managerId = "8";
    
    // Log the manager ID for debugging
    error_log("Searching for users reporting to manager ID: " . $managerId);
    
    // Query to fetch users who report to the manager (by user ID) - Essential columns only
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
              WHERE reportingTo = ?
              ORDER BY first_name, last_name";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute([$managerId]);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Log the number of users found for debugging
    error_log("Users found reporting to manager ID " . $managerId . ": " . count($users));
    
    // Format the response - Essential columns only
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'] ?? '',
            'first_name' => $user['first_name'] ?? '',
            'last_name' => $user['last_name'] ?? '',
            'full_name' => trim(($user['first_name'] ?? '') . ' ' . ($user['last_name'] ?? '')),
            'phone_number' => $user['Phone_number'] ?? '',
            'email_id' => $user['email_id'] ?? '',
            'employee_no' => $user['employee_no'] ?? '',
            'department' => $user['department'] ?? '',
            'designation' => $user['designation'] ?? '',
            'branchloaction' => $user['branchloaction'] ?? '',
            'status' => $user['status'] ?? '',
            'reportingTo' => $user['reportingTo'] ?? ''
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'SDSA users fetched successfully',
        'data' => $formattedUsers,
        'count' => count($formattedUsers)
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