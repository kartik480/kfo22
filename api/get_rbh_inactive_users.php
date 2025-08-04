<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get GET parameters
    $reportingTo = $_GET['reportingTo'] ?? '';
    $status = $_GET['status'] ?? '0'; // Default to 0 for inactive users
    
    if (empty($reportingTo)) {
        echo json_encode([
            'success' => false,
            'message' => 'ReportingTo parameter is required'
        ]);
        exit;
    }
    
    // Prepare and execute query
    $query = "SELECT first_name, last_name, username, Phone_number, email_id, status, reportingTo 
              FROM tbl_rbh_users 
              WHERE reportingTo = :reportingTo AND status = :status 
              ORDER BY first_name, last_name";
    
    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':reportingTo', $reportingTo, PDO::PARAM_STR);
    $stmt->bindParam(':status', $status, PDO::PARAM_STR);
    $stmt->execute();
    
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'message' => 'Inactive users fetched successfully',
        'users' => $users,
        'count' => count($users)
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 