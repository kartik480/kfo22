<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Method not allowed. Only POST requests are accepted.'
    ]);
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
    
    // Get POST data
    $documentName = $_POST['document_name'] ?? '';
    $uploadedFile = $_POST['uploaded_file'] ?? '';
    
    // Validate required fields
    if (empty($documentName)) {
        throw new Exception('Document name is required');
    }
    
    if (empty($uploadedFile)) {
        throw new Exception('Uploaded file is required');
    }
    
    // Check if table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'tbl_document_upload'");
    if ($tableCheck->rowCount() == 0) {
        // Create table if it doesn't exist
        $createTableSQL = "
            CREATE TABLE tbl_document_upload (
                id INT AUTO_INCREMENT PRIMARY KEY,
                document_name VARCHAR(255) NOT NULL,
                uploaded_file TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ";
        $pdo->exec($createTableSQL);
    }
    
    // Insert the document upload
    $stmt = $pdo->prepare("
        INSERT INTO tbl_document_upload (document_name, uploaded_file) 
        VALUES (:document_name, :uploaded_file)
    ");
    
    $stmt->execute([
        ':document_name' => $documentName,
        ':uploaded_file' => $uploadedFile
    ]);
    
    $insertedId = $pdo->lastInsertId();
    
    // Return success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Document uploaded successfully',
        'data' => [
            'id' => $insertedId,
            'document_name' => $documentName,
            'uploaded_file' => $uploadedFile
        ]
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?> 