<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>🧪 Final API Test - All 6 Users Including ID 1</h2>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h3>1. Testing Final API Query</h3>";
    
    // Final query that includes all users including ID 1 without lastName
    $sql = "SELECT id, firstName, lastName 
            FROM tbl_user 
            WHERE (status = 'Active' OR status = 1 OR status IS NULL OR status = '') 
            AND firstName IS NOT NULL 
            AND firstName != '' 
            AND firstName != 'EMPTY'
            ORDER BY firstName ASC, lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        echo "❌ Query failed: " . $conn->error . "<br>";
    } else {
        echo "✅ Query successful<br>";
        echo "Records found: <strong>" . $result->num_rows . "</strong><br><br>";
        
        if ($result->num_rows > 0) {
            echo "<h3>2. All Users That Will Appear in Dropdown</h3>";
            echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
            echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Full Name</th><th>Status</th><th>Notes</th></tr>";
            
            while($row = $result->fetch_assoc()) {
                $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
                
                // Get status for this user
                $statusQuery = "SELECT status FROM tbl_user WHERE id = " . $row['id'];
                $statusResult = $conn->query($statusQuery);
                $statusRow = $statusResult->fetch_assoc();
                $status = $statusRow['status'];
                
                $notes = "";
                if ($row['id'] == 1) {
                    $notes = "ID 1 - Included without lastName";
                } elseif (empty($row['lastName']) || $row['lastName'] == 'EMPTY') {
                    $notes = "Empty lastName - now included";
                }
                
                echo "<tr>";
                echo "<td>" . $row['id'] . "</td>";
                echo "<td>" . $row['firstName'] . "</td>";
                echo "<td>" . ($row['lastName'] ? $row['lastName'] : 'NULL/EMPTY') . "</td>";
                echo "<td>" . $fullName . "</td>";
                echo "<td>" . $status . "</td>";
                echo "<td style='color: blue;'>" . $notes . "</td>";
                echo "</tr>";
            }
            echo "</table>";
            
            echo "<br><p style='color: green;'><strong>✅ SUCCESS!</strong> All " . $result->num_rows . " users will now appear in the reporting dropdown.</p>";
            
            if ($result->num_rows == 6) {
                echo "<p style='color: green;'><strong>🎉 Perfect!</strong> All 6 users including ID 1 (KRAJESHK) are included.</p>";
            }
        } else {
            echo "<p style='color: red;'>❌ Still no users found.</p>";
        }
    }
    
    echo "<h3>3. Expected JSON Response</h3>";
    echo "<p>The API should now return all 6 users:</p>";
    echo "<pre style='background: #f5f5f5; padding: 10px; border-radius: 5px;'>";
    echo '{
  "status": "success",
  "data": [
    {"id": "1", "name": "KRAJESHK", "firstName": "KRAJESHK", "lastName": ""},
    {"id": "8", "name": "K RAJESH KUMAR", "firstName": "K RAJESH", "lastName": "KUMAR"},
    {"id": "11", "name": "SILPA KURAKULA", "firstName": "SILPA", "lastName": "KURAKULA"},
    {"id": "12", "name": "AMARNATH GOPALDAS", "firstName": "AMARNATH", "lastName": "GOPALDAS"},
    {"id": "19", "name": "SUPRAJA A", "firstName": "SUPRAJA", "lastName": "A"},
    {"id": "21", "name": "VENKATESWARA RAO BALUSU", "firstName": "VENKATESWARA RAO", "lastName": "BALUSU"}
  ],
  "message": "Reporting to list fetched successfully",
  "count": 6
}';
    echo "</pre>";
    
    echo "<h3>4. Changes Made</h3>";
    echo "<ul>";
    echo "<li>✅ Removed lastName validation to include user ID 1</li>";
    echo "<li>✅ Removed 'Select Reporting To' from Android dropdown</li>";
    echo "<li>✅ Updated validation logic in Android app</li>";
    echo "<li>✅ Now all 6 users will appear in dropdown</li>";
    echo "</ul>";
    
    echo "<h3>5. Test the API</h3>";
    echo "<p>Test the final API:</p>";
    echo "<p><a href='fetch_sdsa_reporting_users.php' target='_blank'>fetch_sdsa_reporting_users.php</a></p>";
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 