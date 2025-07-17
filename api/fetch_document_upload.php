<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Fetch all documents from tbl_document_upload
    $query = "SELECT document_name, uploaded_file FROM tbl_document_upload ORDER BY document_name ASC";
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    
    $documents = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($documents) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Documents fetched successfully',
            'data' => $documents,
            'count' => count($documents)
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'message' => 'No documents found',
            'data' => [],
            'count' => 0
        ]);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 