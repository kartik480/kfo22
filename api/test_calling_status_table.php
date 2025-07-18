<?php
// Test script to verify tbl_partner_calling_status table

require_once 'db_config.php';

echo "<h2>Testing tbl_partner_calling_status Table</h2>";

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "<p style='color: green;'>Database connection successful!</p>";
    
    // Check if table exists
    $result = $conn->query("SHOW TABLES LIKE 'tbl_partner_calling_status'");
    if ($result->num_rows == 0) {
        echo "<p style='color: red;'>Table 'tbl_partner_calling_status' does not exist!</p>";
        echo "<p>Creating table...</p>";
        
        $createTable = "CREATE TABLE tbl_partner_calling_status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            calling_status VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable) === TRUE) {
            echo "<p style='color: green;'>Table 'tbl_partner_calling_status' created successfully!</p>";
        } else {
            echo "<p style='color: red;'>Error creating table: " . $conn->error . "</p>";
        }
    } else {
        echo "<p style='color: green;'>Table 'tbl_partner_calling_status' exists!</p>";
        
        // Show table structure
        $result = $conn->query("DESCRIBE tbl_partner_calling_status");
        echo "<h3>Table Structure:</h3>";
        echo "<table border='1' style='border-collapse: collapse;'>";
        echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
        while ($row = $result->fetch_assoc()) {
            echo "<tr>";
            echo "<td>" . $row['Field'] . "</td>";
            echo "<td>" . $row['Type'] . "</td>";
            echo "<td>" . $row['Null'] . "</td>";
            echo "<td>" . $row['Key'] . "</td>";
            echo "<td>" . $row['Default'] . "</td>";
            echo "<td>" . $row['Extra'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    // Show existing records
    $result = $conn->query("SELECT * FROM tbl_partner_calling_status ORDER BY created_at DESC LIMIT 10");
    if ($result->num_rows > 0) {
        echo "<h3>Recent Records:</h3>";
        echo "<table border='1' style='border-collapse: collapse;'>";
        echo "<tr><th>ID</th><th>Calling Status</th><th>Created At</th></tr>";
        while ($row = $result->fetch_assoc()) {
            echo "<tr>";
            echo "<td>" . $row['id'] . "</td>";
            echo "<td>" . htmlspecialchars($row['calling_status']) . "</td>";
            echo "<td>" . $row['created_at'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p>No records found in the table.</p>";
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?>
