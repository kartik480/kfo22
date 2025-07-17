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
    // Check if tbl_vendor_bank table exists
    $checkTableQuery = "SHOW TABLES LIKE 'tbl_vendor_bank'";
    $checkTableResult = $conn->query($checkTableQuery);
    
    if ($checkTableResult->num_rows == 0) {
        // Create the table if it doesn't exist
        $createTableQuery = "CREATE TABLE tbl_vendor_bank (
            id INT AUTO_INCREMENT PRIMARY KEY,
            vendor_bank_name VARCHAR(255) NOT NULL UNIQUE,
            status ENUM('active', 'inactive') DEFAULT 'active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTableQuery)) {
            echo json_encode([
                'success' => true,
                'message' => 'tbl_vendor_bank table created successfully',
                'table_created' => true
            ]);
        } else {
            throw new Exception('Failed to create tbl_vendor_bank table: ' . $conn->error);
        }
    } else {
        // Check table structure
        $describeQuery = "DESCRIBE tbl_vendor_bank";
        $describeResult = $conn->query($describeQuery);
        
        $columns = [];
        while ($row = $describeResult->fetch_assoc()) {
            $columns[] = $row;
        }
        
        // Check if vendor_bank_name column exists
        $hasVendorBankNameColumn = false;
        foreach ($columns as $column) {
            if ($column['Field'] === 'vendor_bank_name') {
                $hasVendorBankNameColumn = true;
                break;
            }
        }
        
        if (!$hasVendorBankNameColumn) {
            // Add vendor_bank_name column if it doesn't exist
            $addColumnQuery = "ALTER TABLE tbl_vendor_bank ADD COLUMN vendor_bank_name VARCHAR(255) NOT NULL UNIQUE AFTER id";
            if ($conn->query($addColumnQuery)) {
                echo json_encode([
                    'success' => true,
                    'message' => 'vendor_bank_name column added to tbl_vendor_bank table',
                    'column_added' => true
                ]);
            } else {
                throw new Exception('Failed to add vendor_bank_name column: ' . $conn->error);
            }
        } else {
            echo json_encode([
                'success' => true,
                'message' => 'tbl_vendor_bank table exists and has correct structure',
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