<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>🔍 Debug: Remote Database Analysis</h2>";
echo "<p><strong>Database:</strong> $dbname</p>";
echo "<p><strong>Host:</strong> $host</p>";
echo "<p><strong>Username:</strong> $username</p><br>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h3>1. Database Connection Status</h3>";
    echo "✅ Connected to remote database successfully<br>";
    echo "Database: " . $conn->database . "<br>";
    echo "Server: " . $conn->server_info . "<br><br>";
    
    // 1. Check if tbl_user table exists
    echo "<h3>2. Table Check</h3>";
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows > 0) {
        echo "✅ tbl_user table exists<br>";
    } else {
        echo "❌ tbl_user table does not exist<br>";
        echo "<p>Available tables:</p>";
        $tables = $conn->query("SHOW TABLES");
        while ($table = $tables->fetch_array()) {
            echo "- " . $table[0] . "<br>";
        }
        exit;
    }
    
    // 2. Check total users
    $totalQuery = "SELECT COUNT(*) as total FROM tbl_user";
    $totalResult = $conn->query($totalQuery);
    $totalCount = $totalResult->fetch_assoc()['total'];
    echo "Total users in tbl_user: <strong>$totalCount</strong><br><br>";
    
    if ($totalCount == 0) {
        echo "❌ No users found in tbl_user table. This explains the empty API response.<br>";
        echo "<p><strong>Possible solutions:</strong></p>";
        echo "<ul>";
        echo "<li>Check if you're connected to the correct database</li>";
        echo "<li>Import users from your local database to the remote database</li>";
        echo "<li>Create sample users in the remote database</li>";
        echo "</ul>";
        exit;
    }
    
    // 3. Show all users with their details
    echo "<h3>3. All Users in tbl_user</h3>";
    $allUsersQuery = "SELECT id, firstName, lastName, status FROM tbl_user ORDER BY id";
    $allUsersResult = $conn->query($allUsersQuery);
    
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Status</th><th>Full Name</th><th>Issues</th></tr>";
    
    while($row = $allUsersResult->fetch_assoc()) {
        $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
        $status = $row['status'] ? $row['status'] : 'NULL';
        $firstName = $row['firstName'] ? $row['firstName'] : 'NULL';
        $lastName = $row['lastName'] ? $row['lastName'] : 'NULL';
        
        // Check for issues
        $issues = [];
        if (empty($row['firstName'])) $issues[] = 'Empty firstName';
        if (empty($row['lastName'])) $issues[] = 'Empty lastName';
        if ($row['firstName'] === null) $issues[] = 'NULL firstName';
        if ($row['lastName'] === null) $issues[] = 'NULL lastName';
        if ($row['status'] == 'Inactive') $issues[] = 'Status Inactive';
        if (empty($fullName)) $issues[] = 'Empty full name';
        
        $issueText = implode(', ', $issues);
        
        echo "<tr>";
        echo "<td>" . $row['id'] . "</td>";
        echo "<td>" . $firstName . "</td>";
        echo "<td>" . $lastName . "</td>";
        echo "<td>" . $status . "</td>";
        echo "<td>" . $fullName . "</td>";
        echo "<td style='color: red;'>" . ($issueText ? $issueText : 'None') . "</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
    // 4. Test the current API query step by step
    echo "<h3>4. Testing API Query Step by Step</h3>";
    
    // Step 1: Test status condition
    echo "<h4>Step 1: Status Condition Test</h4>";
    $statusTest = "SELECT COUNT(*) as count FROM tbl_user WHERE (status = 'Active' OR status IS NULL OR status = '')";
    $statusResult = $conn->query($statusTest);
    $statusCount = $statusResult->fetch_assoc()['count'];
    echo "Users with valid status (Active, NULL, or empty): <strong>$statusCount</strong><br>";
    
    if ($statusCount == 0) {
        echo "❌ No users have valid status. All users might have status = 'Inactive'<br>";
        echo "Users by status:<br>";
        $statusBreakdown = $conn->query("SELECT status, COUNT(*) as count FROM tbl_user GROUP BY status");
        while ($row = $statusBreakdown->fetch_assoc()) {
            $status = $row['status'] ? $row['status'] : 'NULL/Empty';
            echo "- Status '$status': " . $row['count'] . " users<br>";
        }
    }
    
    // Step 2: Test name conditions
    echo "<h4>Step 2: Name Conditions Test</h4>";
    $nameTest = "SELECT COUNT(*) as count FROM tbl_user WHERE firstName IS NOT NULL AND firstName != '' AND lastName IS NOT NULL AND lastName != ''";
    $nameResult = $conn->query($nameTest);
    $nameCount = $nameResult->fetch_assoc()['count'];
    echo "Users with valid names (non-null, non-empty): <strong>$nameCount</strong><br>";
    
    if ($nameCount == 0) {
        echo "❌ No users have valid names. Check firstName and lastName fields.<br>";
    }
    
    // Step 3: Test combined query
    echo "<h4>Step 3: Combined Query Test</h4>";
    $combinedTest = "SELECT COUNT(*) as count FROM tbl_user WHERE (status = 'Active' OR status IS NULL OR status = '') AND firstName IS NOT NULL AND firstName != '' AND lastName IS NOT NULL AND lastName != ''";
    $combinedResult = $conn->query($combinedTest);
    $combinedCount = $combinedResult->fetch_assoc()['count'];
    echo "Users matching all conditions: <strong>$combinedCount</strong><br>";
    
    if ($combinedCount == 0) {
        echo "❌ No users match all conditions. This explains the empty API response.<br>";
    }
    
    // 5. Show users that don't match the criteria
    echo "<h3>5. Users Excluded from API Response</h3>";
    $excludedQuery = "SELECT id, firstName, lastName, status FROM tbl_user WHERE NOT ((status = 'Active' OR status IS NULL OR status = '') AND firstName IS NOT NULL AND firstName != '' AND lastName IS NOT NULL AND lastName != '') ORDER BY id";
    $excludedResult = $conn->query($excludedQuery);
    
    if ($excludedResult->num_rows > 0) {
        echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
        echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Status</th><th>Reason for Exclusion</th></tr>";
        
        while($row = $excludedResult->fetch_assoc()) {
            $reasons = [];
            if ($row['status'] == 'Inactive') $reasons[] = 'Status is Inactive';
            if (empty($row['firstName'])) $reasons[] = 'Empty firstName';
            if (empty($row['lastName'])) $reasons[] = 'Empty lastName';
            if ($row['firstName'] === null) $reasons[] = 'NULL firstName';
            if ($row['lastName'] === null) $reasons[] = 'NULL lastName';
            
            $reasonText = implode(', ', $reasons);
            echo "<tr>";
            echo "<td>" . $row['id'] . "</td>";
            echo "<td>" . ($row['firstName'] ? $row['firstName'] : 'NULL') . "</td>";
            echo "<td>" . ($row['lastName'] ? $row['lastName'] : 'NULL') . "</td>";
            echo "<td>" . ($row['status'] ? $row['status'] : 'NULL') . "</td>";
            echo "<td style='color: red;'>" . $reasonText . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "✅ No users are being excluded.<br>";
    }
    
    // 6. Quick fix suggestion
    echo "<h3>6. Quick Fix</h3>";
    if ($combinedCount == 0) {
        echo "<p style='color: orange;'><strong>Issue identified:</strong> No users match the API criteria.</p>";
        echo "<p><strong>Quick fix:</strong> Run this SQL query to fix user status:</p>";
        echo "<pre style='background: #f5f5f5; padding: 10px; border-radius: 5px;'>";
        echo "UPDATE tbl_user SET status = 'Active' WHERE status IS NULL OR status = '' OR status != 'Active';";
        echo "</pre>";
        echo "<p>Or visit: <a href='fix_user_status.php' target='_blank'>fix_user_status.php</a></p>";
    } else {
        echo "<p style='color: green;'>✅ Users found that match criteria. API should work.</p>";
    }
    
    // 7. Database sync suggestion
    echo "<h3>7. Database Sync Suggestion</h3>";
    echo "<p>Since you're using a remote database, you might need to sync your local users to the remote database.</p>";
    echo "<p><strong>Options:</strong></p>";
    echo "<ul>";
    echo "<li>Export users from your local database and import to remote</li>";
    echo "<li>Create sample users directly in the remote database</li>";
    echo "<li>Use the sample user creation script: <a href='add_sample_users.php' target='_blank'>add_sample_users.php</a></li>";
    echo "</ul>";
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 