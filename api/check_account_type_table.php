<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Check if tbl_account_type table exists
    $checkTableQuery = "SHOW TABLES LIKE 'tbl_account_type'";
    $checkTableResult = $conn->query($checkTableQuery);
    
    if ($checkTableResult->num_rows == 0) {
        // Create the table if it doesn't exist
        $createTableQuery = "CREATE TABLE tbl_account_type (
            id INT AUTO_INCREMENT PRIMARY KEY,
            account_type VARCHAR(255) NOT NULL UNIQUE,
            status ENUM('active', 'inactive') DEFAULT 'active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTableQuery)) {
            echo json_encode([
                'success' => true,
                'message' => 'tbl_account_type table created successfully',
                'table_created' => true
            ]);
        } else {
            throw new Exception('Failed to create tbl_account_type table: ' . $conn->error);
        }
    } else {
        // Check table structure
        $describeQuery = "DESCRIBE tbl_account_type";
        $describeResult = $conn->query($describeQuery);
        
        $columns = [];
        while ($row = $describeResult->fetch_assoc()) {
            $columns[] = $row;
        }
        
        // Check if account_type column exists
        $hasAccountTypeColumn = false;
        foreach ($columns as $column) {
            if ($column['Field'] === 'account_type') {
                $hasAccountTypeColumn = true;
                break;
            }
        }
        
        if (!$hasAccountTypeColumn) {
            // Add account_type column if it doesn't exist
            $addColumnQuery = "ALTER TABLE tbl_account_type ADD COLUMN account_type VARCHAR(255) NOT NULL UNIQUE AFTER id";
            if ($conn->query($addColumnQuery)) {
                echo json_encode([
                    'success' => true,
                    'message' => 'account_type column added to tbl_account_type table',
                    'column_added' => true
                ]);
            } else {
                throw new Exception('Failed to add account_type column: ' . $conn->error);
            }
        } else {
            echo json_encode([
                'success' => true,
                'message' => 'tbl_account_type table exists and has correct structure',
                'table_structure' => $columns
            ]);
        }
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
}

$conn->close();
?> 