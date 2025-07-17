<?php
require_once 'db_config.php';

echo "Testing calling status setup...\n";

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "Database connection successful!\n";
    
    // Check if table exists
    $result = $conn->query("SHOW TABLES LIKE 'tbl_partner_calling_status'");
    if ($result->num_rows == 0) {
        echo "Creating table...\n";
        
        $createTable = "CREATE TABLE tbl_partner_calling_status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            calling_status VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable) === TRUE) {
            echo "Table created successfully!\n";
        } else {
            echo "Error creating table: " . $conn->error . "\n";
            exit;
        }
    } else {
        echo "Table already exists!\n";
    }
    
    // Add sample data
    $sampleStatuses = ['Not Interested', 'Interested', 'Call Back Later', 'No Answer', 'Busy'];
    
    foreach ($sampleStatuses as $status) {
        $insertSql = "INSERT IGNORE INTO tbl_partner_calling_status (calling_status) VALUES (?)";
        $insertStmt = $conn->prepare($insertSql);
        $insertStmt->bind_param("s", $status);
        $insertStmt->execute();
        $insertStmt->close();
    }
    
    echo "Sample data added!\n";
    
    // Test the API
    $result = $conn->query("SELECT id, calling_status FROM tbl_partner_calling_status ORDER BY calling_status ASC");
    $count = $result->num_rows;
    echo "Found $count calling statuses in database.\n";
    
    $conn->close();
    echo "Setup complete!\n";
    
} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?> 