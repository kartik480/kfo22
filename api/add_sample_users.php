<?php
// Script to add sample users to tbl_user table for testing
echo "=== Adding Sample Users to tbl_user ===\n\n";

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        echo "❌ Database connection failed: " . mysqli_connect_error() . "\n";
        exit;
    }
    
    echo "✅ Database connection successful\n\n";
    
    // Check if tbl_user table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows == 0) {
        echo "❌ tbl_user table does not exist! Creating it...\n";
        
        // Create tbl_user table
        $createTable = "CREATE TABLE tbl_user (
            id INT AUTO_INCREMENT PRIMARY KEY,
            firstName VARCHAR(100) NOT NULL,
            lastName VARCHAR(100) NOT NULL,
            email VARCHAR(255),
            password VARCHAR(255),
            status ENUM('Active', 'Inactive') DEFAULT 'Active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable)) {
            echo "✅ tbl_user table created successfully\n\n";
        } else {
            echo "❌ Failed to create table: " . $conn->error . "\n";
            exit;
        }
    } else {
        echo "✅ tbl_user table exists\n\n";
    }
    
    // Check current user count
    $countQuery = $conn->query("SELECT COUNT(*) as total FROM tbl_user");
    $currentCount = $countQuery->fetch_assoc()['total'];
    echo "Current users in table: " . $currentCount . "\n\n";
    
    // Add sample users if table is empty or has few users
    if ($currentCount < 5) {
        echo "Adding sample users...\n";
        
        $sampleUsers = [
            ['firstName' => 'John', 'lastName' => 'Doe', 'email' => 'john.doe@example.com'],
            ['firstName' => 'Jane', 'lastName' => 'Smith', 'email' => 'jane.smith@example.com'],
            ['firstName' => 'Michael', 'lastName' => 'Johnson', 'email' => 'michael.johnson@example.com'],
            ['firstName' => 'Sarah', 'lastName' => 'Williams', 'email' => 'sarah.williams@example.com'],
            ['firstName' => 'David', 'lastName' => 'Brown', 'email' => 'david.brown@example.com']
        ];
        
        $inserted = 0;
        foreach ($sampleUsers as $user) {
            // Check if user already exists
            $checkQuery = "SELECT id FROM tbl_user WHERE firstName = ? AND lastName = ?";
            $stmt = $conn->prepare($checkQuery);
            $stmt->bind_param("ss", $user['firstName'], $user['lastName']);
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows == 0) {
                // Insert new user
                $insertQuery = "INSERT INTO tbl_user (firstName, lastName, email, status) VALUES (?, ?, ?, 'Active')";
                $insertStmt = $conn->prepare($insertQuery);
                $insertStmt->bind_param("sss", $user['firstName'], $user['lastName'], $user['email']);
                
                if ($insertStmt->execute()) {
                    echo "✅ Added: " . $user['firstName'] . " " . $user['lastName'] . "\n";
                    $inserted++;
                } else {
                    echo "❌ Failed to add: " . $user['firstName'] . " " . $user['lastName'] . " - " . $insertStmt->error . "\n";
                }
            } else {
                echo "⏭️  Skipped: " . $user['firstName'] . " " . $user['lastName'] . " (already exists)\n";
            }
        }
        
        echo "\nTotal new users added: " . $inserted . "\n\n";
    } else {
        echo "Table already has sufficient users for testing.\n\n";
    }
    
    // Show final user count
    $finalCount = $conn->query("SELECT COUNT(*) as total FROM tbl_user")->fetch_assoc()['total'];
    echo "Final user count: " . $finalCount . "\n\n";
    
    // Show active users
    echo "=== Active Users ===\n";
    $activeUsers = $conn->query("SELECT id, firstName, lastName, status FROM tbl_user WHERE status = 'Active' ORDER BY firstName");
    while ($row = $activeUsers->fetch_assoc()) {
        echo "- ID: " . $row['id'] . ", Name: " . $row['firstName'] . " " . $row['lastName'] . ", Status: " . $row['status'] . "\n";
    }
    
    echo "\n✅ Sample users setup complete!\n";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
}
?> 