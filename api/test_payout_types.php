<?php
header('Content-Type: text/html');
require_once 'db_config.php';

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "<h2>Testing Payout Types Table</h2>";
    
    // Check if table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_payout_type'");
    if ($tableCheck->num_rows > 0) {
        echo "<p>✅ Table 'tbl_payout_type' exists</p>";
        
        // Check table structure
        $structure = $conn->query("DESCRIBE tbl_payout_type");
        echo "<h3>Table Structure:</h3>";
        echo "<table border='1'>";
        echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th></tr>";
        while ($row = $structure->fetch_assoc()) {
            echo "<tr>";
            echo "<td>" . $row['Field'] . "</td>";
            echo "<td>" . $row['Type'] . "</td>";
            echo "<td>" . $row['Null'] . "</td>";
            echo "<td>" . $row['Key'] . "</td>";
            echo "<td>" . $row['Default'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
        
        // Check data
        $data = $conn->query("SELECT * FROM tbl_payout_type");
        echo "<h3>Data in table (" . $data->num_rows . " rows):</h3>";
        if ($data->num_rows > 0) {
            echo "<table border='1'>";
            echo "<tr><th>ID</th><th>Payout Name</th></tr>";
            while ($row = $data->fetch_assoc()) {
                echo "<tr>";
                echo "<td>" . (isset($row['id']) ? $row['id'] : 'N/A') . "</td>";
                echo "<td>" . $row['payout_name'] . "</td>";
                echo "</tr>";
            }
            echo "</table>";
        } else {
            echo "<p>❌ No data found in table</p>";
        }
        
    } else {
        echo "<p>❌ Table 'tbl_payout_type' does not exist</p>";
    }
    
} catch (Exception $e) {
    echo "<p>❌ Error: " . $e->getMessage() . "</p>";
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 