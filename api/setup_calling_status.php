<?php
// Setup script for calling status functionality
// This script will create the table and add sample data

require_once 'db_config.php';

echo "<h2>Setting up Calling Status System</h2>";

try {
    // Test database connection
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "<p style='color: green;'>✓ Database connection successful!</p>";
    
    // Check if table exists
    $result = $conn->query("SHOW TABLES LIKE 'tbl_partner_calling_status'");
    if ($result->num_rows == 0) {
        echo "<p>Creating tbl_partner_calling_status table...</p>";
        
        $createTable = "CREATE TABLE tbl_partner_calling_status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            calling_status VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable) === TRUE) {
            echo "<p style='color: green;'>✓ Table created successfully!</p>";
        } else {
            echo "<p style='color: red;'>✗ Error creating table: " . $conn->error . "</p>";
            exit;
        }
    } else {
        echo "<p style='color: green;'>✓ Table already exists!</p>";
    }
    
    // Add sample data if table is empty
    $result = $conn->query("SELECT COUNT(*) as count FROM tbl_partner_calling_status");
    $row = $result->fetch_assoc();
    
    if ($row['count'] == 0) {
        echo "<p>Adding sample calling statuses...</p>";
        
        $sampleStatuses = [
            'Not Interested',
            'Interested', 
            'Call Back Later',
            'No Answer',
            'Busy',
            'Wrong Number',
            'Follow Up Required',
            'Appointment Scheduled',
            'Sale Completed',
            'Not Available'
        ];
        
        $addedCount = 0;
        foreach ($sampleStatuses as $status) {
            $insertSql = "INSERT INTO tbl_partner_calling_status (calling_status) VALUES (?)";
            $insertStmt = $conn->prepare($insertSql);
            $insertStmt->bind_param("s", $status);
            
            if ($insertStmt->execute()) {
                echo "<p style='color: green;'>✓ Added: " . htmlspecialchars($status) . "</p>";
                $addedCount++;
            } else {
                echo "<p style='color: red;'>✗ Error adding: " . htmlspecialchars($status) . "</p>";
            }
            $insertStmt->close();
        }
        
        echo "<p style='color: green;'>✓ Added $addedCount sample calling statuses!</p>";
    } else {
        echo "<p style='color: blue;'>ℹ Table already has data, skipping sample data insertion.</p>";
    }
    
    // Test the get_calling_status_list.php functionality
    echo "<h3>Testing API Endpoint:</h3>";
    
    // Simulate the API call
    $result = $conn->query("SELECT id, calling_status FROM tbl_partner_calling_status ORDER BY calling_status ASC");
    $callingStatuses = [];
    while ($row = $result->fetch_assoc()) {
        $callingStatuses[] = [
            'id' => $row['id'],
            'calling_status' => $row['calling_status']
        ];
    }
    
    echo "<p style='color: green;'>✓ API test successful! Found " . count($callingStatuses) . " calling statuses.</p>";
    
    // Show the data
    echo "<h3>Current Calling Statuses:</h3>";
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr style='background-color: #f0f0f0;'><th>ID</th><th>Calling Status</th><th>Created At</th></tr>";
    
    $result = $conn->query("SELECT * FROM tbl_partner_calling_status ORDER BY calling_status ASC");
    while ($row = $result->fetch_assoc()) {
        echo "<tr>";
        echo "<td>" . $row['id'] . "</td>";
        echo "<td>" . htmlspecialchars($row['calling_status']) . "</td>";
        echo "<td>" . $row['created_at'] . "</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    echo "<h3>Setup Complete! 🎉</h3>";
    echo "<p>Your calling status system is now ready. The Android app should be able to connect to:</p>";
    echo "<ul>";
    echo "<li><strong>GET:</strong> http://10.0.2.2/get_calling_status_list.php</li>";
    echo "<li><strong>POST:</strong> http://10.0.2.2/add_calling_status.php</li>";
    echo "</ul>";
    
    $conn->close();
    
} catch (Exception $e) {
    echo "<p style='color: red;'>✗ Error: " . $e->getMessage() . "</p>";
}
?> 