<?php
// Script to add sample calling statuses for testing

require_once 'db_config.php';

echo "<h2>Adding Sample Calling Statuses</h2>";

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "<p style='color: green;'>Database connection successful!</p>";
    
    // Check if table exists, create if not
    $result = $conn->query("SHOW TABLES LIKE 'tbl_partner_calling_status'");
    if ($result->num_rows == 0) {
        echo "<p>Creating table...</p>";
        
        $createTable = "CREATE TABLE tbl_partner_calling_status (
            id INT AUTO_INCREMENT PRIMARY KEY,
            calling_status VARCHAR(255) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable) === TRUE) {
            echo "<p style='color: green;'>Table created successfully!</p>";
        } else {
            echo "<p style='color: red;'>Error creating table: " . $conn->error . "</p>";
            exit;
        }
    }
    
    // Sample calling statuses
    $sampleStatuses = [
        'Not Interested',
        'Interested',
        'Call Back Later',
        'No Answer',
        'Busy',
        'Wrong Number',
        'Follow Up Required'
    ];
    
    $addedCount = 0;
    foreach ($sampleStatuses as $status) {
        // Check if status already exists
        $checkSql = "SELECT id FROM tbl_partner_calling_status WHERE calling_status = ?";
        $checkStmt = $conn->prepare($checkSql);
        $checkStmt->bind_param("s", $status);
        $checkStmt->execute();
        $checkResult = $checkStmt->get_result();
        
        if ($checkResult->num_rows == 0) {
            // Insert new status
            $insertSql = "INSERT INTO tbl_partner_calling_status (calling_status) VALUES (?)";
            $insertStmt = $conn->prepare($insertSql);
            $insertStmt->bind_param("s", $status);
            
            if ($insertStmt->execute()) {
                echo "<p style='color: green;'>Added: " . htmlspecialchars($status) . "</p>";
                $addedCount++;
            } else {
                echo "<p style='color: red;'>Error adding: " . htmlspecialchars($status) . " - " . $insertStmt->error . "</p>";
            }
            $insertStmt->close();
        } else {
            echo "<p style='color: orange;'>Already exists: " . htmlspecialchars($status) . "</p>";
        }
        $checkStmt->close();
    }
    
    echo "<h3>Summary:</h3>";
    echo "<p>Added $addedCount new calling statuses.</p>";
    
    // Show all current statuses
    $result = $conn->query("SELECT * FROM tbl_partner_calling_status ORDER BY calling_status ASC");
    if ($result->num_rows > 0) {
        echo "<h3>All Calling Statuses:</h3>";
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
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 