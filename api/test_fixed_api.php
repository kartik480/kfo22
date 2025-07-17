<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>🧪 Test Fixed API Query</h2>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h3>1. Testing Fixed API Query</h3>";
    
    // Updated query that handles integer status values
    // Include users even if lastName is 'EMPTY' (treat as valid)
    $sql = "SELECT id, firstName, lastName 
            FROM tbl_user 
            WHERE (status = 'Active' OR status = 1 OR status IS NULL OR status = '') 
            AND firstName IS NOT NULL 
            AND firstName != '' 
            AND firstName != 'EMPTY'
            AND lastName IS NOT NULL 
            AND lastName != ''
            ORDER BY firstName ASC, lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        echo "❌ Query failed: " . $conn->error . "<br>";
    } else {
        echo "✅ Query successful<br>";
        echo "Records found: <strong>" . $result->num_rows . "</strong><br><br>";
        
        if ($result->num_rows > 0) {
            echo "<h3>2. Users Found</h3>";
            echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
            echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Full Name</th><th>Status</th></tr>";
            
            while($row = $result->fetch_assoc()) {
                $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
                
                // Get status for this user
                $statusQuery = "SELECT status FROM tbl_user WHERE id = " . $row['id'];
                $statusResult = $conn->query($statusQuery);
                $statusRow = $statusResult->fetch_assoc();
                $status = $statusRow['status'];
                
                echo "<tr>";
                echo "<td>" . $row['id'] . "</td>";
                echo "<td>" . $row['firstName'] . "</td>";
                echo "<td>" . $row['lastName'] . "</td>";
                echo "<td>" . $fullName . "</td>";
                echo "<td>" . $status . "</td>";
                echo "</tr>";
            }
            echo "</table>";
            
            echo "<br><p style='color: green;'><strong>✅ SUCCESS!</strong> The API should now return " . $result->num_rows . " users.</p>";
        } else {
            echo "<p style='color: red;'>❌ Still no users found.</p>";
        }
    }
    
    echo "<h3>3. Test the Actual API</h3>";
    echo "<p>Now test the actual API endpoint:</p>";
    echo "<p><a href='fetch_sdsa_reporting_users.php' target='_blank'>fetch_sdsa_reporting_users.php</a></p>";
    
    echo "<h3>4. Expected JSON Response</h3>";
    echo "<p>The API should now return something like:</p>";
    echo "<pre style='background: #f5f5f5; padding: 10px; border-radius: 5px;'>";
    echo '{
  "status": "success",
  "data": [
    {"id": "8", "name": "K RAJESH KUMAR", "firstName": "K RAJESH", "lastName": "KUMAR"},
    {"id": "11", "name": "SILPA KURAKULA", "firstName": "SILPA", "lastName": "KURAKULA"},
    {"id": "12", "name": "AMARNATH GOPALDAS", "firstName": "AMARNATH", "lastName": "GOPALDAS"},
    {"id": "19", "name": "SUPRAJA A", "firstName": "SUPRAJA", "lastName": "A"},
    {"id": "21", "name": "VENKATESWARA RAO BALUSU", "firstName": "VENKATESWARA RAO", "lastName": "BALUSU"}
  ],
  "message": "Reporting to list fetched successfully",
  "count": 5
}';
    echo "</pre>";
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 