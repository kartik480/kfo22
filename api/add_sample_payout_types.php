<?php
header('Content-Type: text/html');
require_once 'db_config.php';

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "<h2>Adding Sample Payout Types</h2>";
    
    // Check if table exists, if not create it
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_payout_type'");
    if ($tableCheck->num_rows == 0) {
        $createTable = "CREATE TABLE tbl_payout_type (
            id INT AUTO_INCREMENT PRIMARY KEY,
            payout_name VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable) === TRUE) {
            echo "<p>✅ Table 'tbl_payout_type' created successfully</p>";
        } else {
            echo "<p>❌ Error creating table: " . $conn->error . "</p>";
            exit;
        }
    } else {
        echo "<p>✅ Table 'tbl_payout_type' already exists</p>";
    }
    
    // Check if table has data
    $dataCheck = $conn->query("SELECT COUNT(*) as count FROM tbl_payout_type");
    $row = $dataCheck->fetch_assoc();
    
    if ($row['count'] == 0) {
        // Add sample payout types
        $sampleTypes = [
            "Commission",
            "Bonus",
            "Incentive",
            "Performance Reward",
            "Sales Commission",
            "Referral Bonus"
        ];
        
        $insertCount = 0;
        foreach ($sampleTypes as $type) {
            $sql = "INSERT INTO tbl_payout_type (payout_name) VALUES (?)";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("s", $type);
            
            if ($stmt->execute()) {
                $insertCount++;
                echo "<p>✅ Added: " . $type . "</p>";
            } else {
                echo "<p>❌ Error adding " . $type . ": " . $stmt->error . "</p>";
            }
            $stmt->close();
        }
        
        echo "<p><strong>Total added: " . $insertCount . " payout types</strong></p>";
    } else {
        echo "<p>✅ Table already has " . $row['count'] . " payout types</p>";
    }
    
    // Show current data
    echo "<h3>Current Payout Types:</h3>";
    $result = $conn->query("SELECT * FROM tbl_payout_type ORDER BY payout_name");
    if ($result->num_rows > 0) {
        echo "<table border='1'>";
        echo "<tr><th>ID</th><th>Payout Name</th><th>Created At</th></tr>";
        while ($row = $result->fetch_assoc()) {
            echo "<tr>";
            echo "<td>" . $row['id'] . "</td>";
            echo "<td>" . $row['payout_name'] . "</td>";
            echo "<td>" . $row['created_at'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
} catch (Exception $e) {
    echo "<p>❌ Error: " . $e->getMessage() . "</p>";
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 