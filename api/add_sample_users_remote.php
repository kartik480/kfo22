<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>📝 Add Sample Users to Remote Database</h2>";
echo "<p><strong>Database:</strong> $dbname</p>";
echo "<p><strong>Host:</strong> $host</p><br>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h3>1. Database Connection Status</h3>";
    echo "✅ Connected to remote database successfully<br><br>";
    
    // Check if tbl_user table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows == 0) {
        echo "❌ tbl_user table does not exist. Creating it...<br>";
        
        $createTable = "CREATE TABLE tbl_user (
            id INT AUTO_INCREMENT PRIMARY KEY,
            firstName VARCHAR(100) NOT NULL,
            lastName VARCHAR(100) NOT NULL,
            email_id VARCHAR(255),
            mobile VARCHAR(20),
            status ENUM('Active', 'Inactive') DEFAULT 'Active',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )";
        
        if ($conn->query($createTable)) {
            echo "✅ tbl_user table created successfully<br><br>";
        } else {
            echo "❌ Failed to create table: " . $conn->error . "<br>";
            exit;
        }
    } else {
        echo "✅ tbl_user table exists<br><br>";
    }
    
    // Check current user count
    $countQuery = "SELECT COUNT(*) as count FROM tbl_user";
    $countResult = $conn->query($countQuery);
    $currentCount = $countResult->fetch_assoc()['count'];
    echo "<h3>2. Current User Count</h3>";
    echo "Current users in tbl_user: <strong>$currentCount</strong><br><br>";
    
    // Sample users data
    $sampleUsers = [
        ['firstName' => 'John', 'lastName' => 'Doe', 'email_id' => 'john.doe@example.com', 'mobile' => '1234567890'],
        ['firstName' => 'Jane', 'lastName' => 'Smith', 'email_id' => 'jane.smith@example.com', 'mobile' => '1234567891'],
        ['firstName' => 'Mike', 'lastName' => 'Johnson', 'email_id' => 'mike.johnson@example.com', 'mobile' => '1234567892'],
        ['firstName' => 'Sarah', 'lastName' => 'Williams', 'email_id' => 'sarah.williams@example.com', 'mobile' => '1234567893'],
        ['firstName' => 'David', 'lastName' => 'Brown', 'email_id' => 'david.brown@example.com', 'mobile' => '1234567894'],
        ['firstName' => 'Lisa', 'lastName' => 'Davis', 'email_id' => 'lisa.davis@example.com', 'mobile' => '1234567895']
    ];
    
    echo "<h3>3. Adding Sample Users</h3>";
    
    $insertedCount = 0;
    foreach ($sampleUsers as $user) {
        $firstName = $conn->real_escape_string($user['firstName']);
        $lastName = $conn->real_escape_string($user['lastName']);
        $email = $conn->real_escape_string($user['email_id']);
        $mobile = $conn->real_escape_string($user['mobile']);
        
        $insertQuery = "INSERT INTO tbl_user (firstName, lastName, email_id, mobile, status) 
                       VALUES ('$firstName', '$lastName', '$email', '$mobile', 'Active')";
        
        if ($conn->query($insertQuery)) {
            echo "✅ Added user: " . $user['firstName'] . " " . $user['lastName'] . "<br>";
            $insertedCount++;
        } else {
            echo "❌ Failed to add user " . $user['firstName'] . ": " . $conn->error . "<br>";
        }
    }
    
    echo "<br><strong>Total users added: $insertedCount</strong><br><br>";
    
    // Verify the users were added
    echo "<h3>4. Verification</h3>";
    $newCountQuery = "SELECT COUNT(*) as count FROM tbl_user";
    $newCountResult = $conn->query($newCountQuery);
    $newCount = $newCountResult->fetch_assoc()['count'];
    echo "Total users after adding samples: <strong>$newCount</strong><br>";
    
    // Test the API query
    echo "<h3>5. Testing API Query</h3>";
    $apiQuery = "SELECT COUNT(*) as count FROM tbl_user 
                 WHERE (status = 'Active' OR status IS NULL OR status = '') 
                 AND firstName IS NOT NULL 
                 AND firstName != '' 
                 AND lastName IS NOT NULL 
                 AND lastName != ''";
    
    $apiResult = $conn->query($apiQuery);
    $apiCount = $apiResult->fetch_assoc()['count'];
    echo "Users matching API criteria: <strong>$apiCount</strong><br>";
    
    if ($apiCount > 0) {
        echo "<p style='color: green;'>✅ Success! The API should now return users.</p>";
        echo "<p><strong>Test the API:</strong> <a href='fetch_sdsa_reporting_users.php' target='_blank'>fetch_sdsa_reporting_users.php</a></p>";
    } else {
        echo "<p style='color: red;'>❌ Still no users match the API criteria.</p>";
    }
    
    // Show all users
    echo "<h3>6. All Users in Database</h3>";
    $allUsersQuery = "SELECT id, firstName, lastName, status FROM tbl_user ORDER BY id";
    $allUsersResult = $conn->query($allUsersQuery);
    
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Status</th><th>Full Name</th></tr>";
    
    while($row = $allUsersResult->fetch_assoc()) {
        $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
        echo "<tr>";
        echo "<td>" . $row['id'] . "</td>";
        echo "<td>" . $row['firstName'] . "</td>";
        echo "<td>" . $row['lastName'] . "</td>";
        echo "<td>" . $row['status'] . "</td>";
        echo "<td>" . $fullName . "</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    echo "<br><p><strong>Next Steps:</strong></p>";
    echo "<ul>";
    echo "<li>Test the API: <a href='fetch_sdsa_reporting_users.php' target='_blank'>fetch_sdsa_reporting_users.php</a></li>";
    echo "<li>Check the debug report: <a href='debug_remote_db.php' target='_blank'>debug_remote_db.php</a></li>";
    echo "<li>Test in your Android app</li>";
    echo "</ul>";
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 