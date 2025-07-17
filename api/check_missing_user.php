<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>🔍 Check Why User ID 1 is Missing</h2>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // Check user ID 1 specifically
    echo "<h3>1. User ID 1 Details</h3>";
    $user1Query = "SELECT id, firstName, lastName, status FROM tbl_user WHERE id = 1";
    $user1Result = $conn->query($user1Query);
    
    if ($user1Result->num_rows > 0) {
        $user1 = $user1Result->fetch_assoc();
        echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
        echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Status</th></tr>";
        echo "<tr>";
        echo "<td>" . $user1['id'] . "</td>";
        echo "<td>" . $user1['firstName'] . "</td>";
        echo "<td>" . $user1['lastName'] . "</td>";
        echo "<td>" . $user1['status'] . "</td>";
        echo "</tr>";
        echo "</table><br>";
        
        // Test each condition individually
        echo "<h3>2. Testing Each Condition for User ID 1</h3>";
        
        // Test status condition
        $statusTest = "SELECT COUNT(*) as count FROM tbl_user WHERE id = 1 AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')";
        $statusResult = $conn->query($statusTest);
        $statusCount = $statusResult->fetch_assoc()['count'];
        echo "Status condition (should be 1): <strong>$statusCount</strong><br>";
        
        // Test firstName condition
        $firstNameTest = "SELECT COUNT(*) as count FROM tbl_user WHERE id = 1 AND firstName IS NOT NULL AND firstName != '' AND firstName != 'EMPTY'";
        $firstNameResult = $conn->query($firstNameTest);
        $firstNameCount = $firstNameResult->fetch_assoc()['count'];
        echo "firstName condition (should be 1): <strong>$firstNameCount</strong><br>";
        
        // Test lastName condition
        $lastNameTest = "SELECT COUNT(*) as count FROM tbl_user WHERE id = 1 AND lastName IS NOT NULL AND lastName != ''";
        $lastNameResult = $conn->query($lastNameTest);
        $lastNameCount = $lastNameResult->fetch_assoc()['count'];
        echo "lastName condition (should be 1): <strong>$lastNameCount</strong><br>";
        
        // Test combined query
        $combinedTest = "SELECT COUNT(*) as count FROM tbl_user WHERE id = 1 AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '') AND firstName IS NOT NULL AND firstName != '' AND firstName != 'EMPTY' AND lastName IS NOT NULL AND lastName != ''";
        $combinedResult = $conn->query($combinedTest);
        $combinedCount = $combinedResult->fetch_assoc()['count'];
        echo "Combined query (should be 1): <strong>$combinedCount</strong><br>";
        
    } else {
        echo "❌ User ID 1 not found in database<br>";
    }
    
    // Show current API response
    echo "<h3>3. Current API Response</h3>";
    echo "<p>Testing the actual API:</p>";
    
    $apiQuery = "SELECT id, firstName, lastName 
                 FROM tbl_user 
                 WHERE (status = 'Active' OR status = 1 OR status IS NULL OR status = '') 
                 AND firstName IS NOT NULL 
                 AND firstName != '' 
                 AND firstName != 'EMPTY'
                 AND lastName IS NOT NULL 
                 AND lastName != ''
                 ORDER BY firstName ASC, lastName ASC";
    
    $apiResult = $conn->query($apiQuery);
    
    if ($apiResult === false) {
        echo "❌ API query failed: " . $conn->error . "<br>";
    } else {
        echo "✅ API query successful<br>";
        echo "Records found: <strong>" . $apiResult->num_rows . "</strong><br><br>";
        
        if ($apiResult->num_rows > 0) {
            echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
            echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Full Name</th></tr>";
            
            while($row = $apiResult->fetch_assoc()) {
                $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
                echo "<tr>";
                echo "<td>" . $row['id'] . "</td>";
                echo "<td>" . $row['firstName'] . "</td>";
                echo "<td>" . $row['lastName'] . "</td>";
                echo "<td>" . $fullName . "</td>";
                echo "</tr>";
            }
            echo "</table>";
        }
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 