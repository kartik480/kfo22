<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Create the tbl_banker_designation table
    $createTableQuery = "CREATE TABLE IF NOT EXISTS tbl_banker_designation (
        id INT AUTO_INCREMENT PRIMARY KEY,
        designation_name VARCHAR(255) NOT NULL UNIQUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )";
    
    if ($conn->query($createTableQuery) === TRUE) {
        echo json_encode([
            'success' => true,
            'message' => 'Table tbl_banker_designation created successfully'
        ]);
    } else {
        throw new Exception('Failed to create table: ' . $conn->error);
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 