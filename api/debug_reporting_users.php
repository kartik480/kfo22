<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h2>Debug Report: tbl_user Table Analysis</h2>";
    
    // 1. Check total number of users
    $totalQuery = "SELECT COUNT(*) as total FROM tbl_user";
    $totalResult = $conn->query($totalQuery);
    $totalCount = $totalResult->fetch_assoc()['total'];
    echo "<h3>1. Total Users in tbl_user: $totalCount</h3>";
    
    // 2. Check users by status
    $statusQuery = "SELECT status, COUNT(*) as count FROM tbl_user GROUP BY status";
    $statusResult = $conn->query($statusQuery);
    echo "<h3>2. Users by Status:</h3>";
    echo "<ul>";
    while($row = $statusResult->fetch_assoc()) {
        $status = $row['status'] ? $row['status'] : 'NULL/Empty';
        echo "<li>Status '$status': " . $row['count'] . " users</li>";
    }
    echo "</ul>";
    
    // 3. Check users with empty names
    $emptyNamesQuery = "SELECT COUNT(*) as count FROM tbl_user WHERE firstName IS NULL OR firstName = '' OR lastName IS NULL OR lastName = ''";
    $emptyNamesResult = $conn->query($emptyNamesQuery);
    $emptyNamesCount = $emptyNamesResult->fetch_assoc()['count'];
    echo "<h3>3. Users with Empty Names: $emptyNamesCount</h3>";
    
    // 4. Show all users with their details
    $allUsersQuery = "SELECT id, firstName, lastName, status FROM tbl_user ORDER BY id";
    $allUsersResult = $conn->query($allUsersQuery);
    echo "<h3>4. All Users Details:</h3>";
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Status</th><th>Full Name</th></tr>";
    while($row = $allUsersResult->fetch_assoc()) {
        $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
        $status = $row['status'] ? $row['status'] : 'NULL';
        echo "<tr>";
        echo "<td>" . $row['id'] . "</td>";
        echo "<td>" . ($row['firstName'] ? $row['firstName'] : 'NULL') . "</td>";
        echo "<td>" . ($row['lastName'] ? $row['lastName'] : 'NULL') . "</td>";
        echo "<td>" . $status . "</td>";
        echo "<td>" . $fullName . "</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    // 5. Test the exact query used in fetch_sdsa_reporting_users.php
    echo "<h3>5. Testing the Reporting Users Query:</h3>";
    $sql = "SELECT id, firstName, lastName 
                 FROM tbl_user 
            WHERE (status = 'Active' OR status IS NULL OR status = '' OR status != 'Inactive')
                 AND firstName IS NOT NULL 
                 AND firstName != '' 
                 AND lastName IS NOT NULL 
                 AND lastName != ''
                 ORDER BY firstName ASC, lastName ASC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        echo "<p style='color: red;'>Query failed: " . $conn->error . "</p>";
    } else {
        echo "<p>Query successful. Found " . $result->num_rows . " users.</p>";
        echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
        echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Full Name</th></tr>";
        while($row = $result->fetch_assoc()) {
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
    
    // 6. Check which users are being excluded and why
    echo "<h3>6. Users Excluded from Reporting Dropdown:</h3>";
    $excludedQuery = "SELECT id, firstName, lastName, status 
                      FROM tbl_user 
                      WHERE NOT (
                          (status = 'Active' OR status IS NULL OR status = '' OR status != 'Inactive')
                          AND firstName IS NOT NULL 
                          AND firstName != '' 
                          AND lastName IS NOT NULL 
                          AND lastName != ''
                      )
                      ORDER BY id";
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
            echo "<td>" . $reasonText . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p>No users are being excluded from the reporting dropdown.</p>";
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 