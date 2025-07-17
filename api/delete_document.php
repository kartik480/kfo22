<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
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
    $documentId = $_POST['document_id'] ?? '';
    
    // Validate input
    if (empty($documentId) || !is_numeric($documentId)) {
        echo json_encode([
            'success' => false,
            'message' => 'Valid document ID is required'
        ]);
        exit();
    }
    
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
    $sql = "DELETE FROM tbl_document_upload WHERE id = ?";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        echo json_encode([
            'success' => false,
            'message' => 'Failed to prepare statement: ' . $conn->error
        ]);
        exit();
    }
    
    // Bind parameters
    $stmt->bind_param("i", $documentId);
    
    // Execute the statement
    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                'success' => true,
                'message' => 'Document deleted successfully'
            ]);
        } else {
            echo json_encode([
                'success' => false,
                'message' => 'Document not found or already deleted'
            ]);
        }
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Failed to delete document: ' . $stmt->error
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