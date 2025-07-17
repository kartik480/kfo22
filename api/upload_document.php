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

// Include database configuration
require_once 'db_config_fixed.php';

// Check if it's a POST request
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        'success' => false,
        'message' => 'Only POST method is allowed'
    ]);
    exit();
}

try {
    // Get POST data
    $documentName = $_POST['document_name'] ?? '';
    $fileUri = $_POST['file_uri'] ?? '';
    
    // Validate input
    if (empty($documentName)) {
        echo json_encode([
            'success' => false,
            'message' => 'Document name is required'
        ]);
        exit();
    }
    
    if (empty($fileUri)) {
        echo json_encode([
            'success' => false,
            'message' => 'File URI is required'
        ]);
        exit();
    }
    
    // Extract filename from URI
    $fileName = basename($fileUri);
    
    // Create database connection using the correct variable names
    $conn = new mysqli($host, $username, $password, $dbname);
    
    // Check connection
    if ($conn->connect_error) {
        echo json_encode([
            'success' => false,
            'message' => 'Database connection failed: ' . $conn->connect_error
        ]);
        exit();
    }
    
    // Prepare SQL statement
    $sql = "INSERT INTO tbl_document_upload (document_name, uploaded_file) VALUES (?, ?)";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        echo json_encode([
            'success' => false,
            'message' => 'Failed to prepare statement: ' . $conn->error
        ]);
        exit();
    }
    
    // Bind parameters
    $stmt->bind_param("ss", $documentName, $fileName);
    
    // Execute the statement
    if ($stmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Document uploaded successfully',
            'document_name' => $documentName,
            'uploaded_file' => $fileName
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Failed to upload document: ' . $stmt->error
        ]);
    }
    
    // Close statement and connection
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 