<?php
// Script to fix user status in tbl_user table
echo "=== Fixing User Status in tbl_user ===\n\n";

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        echo "❌ Database connection failed: " . mysqli_connect_error() . "\n";
        exit;
    }
    
    echo "✅ Database connection successful\n\n";
    
    // Check current status distribution
    echo "=== Current Status Distribution ===\n";
    $statusQuery = $conn->query("SELECT status, COUNT(*) as count FROM tbl_user GROUP BY status");
    while ($row = $statusQuery->fetch_assoc()) {
        echo "- Status '" . $row['status'] . "': " . $row['count'] . " users\n";
    }
    echo "\n";
    
    // Check users with NULL or empty status
    $nullStatusQuery = $conn->query("SELECT COUNT(*) as count FROM tbl_user WHERE status IS NULL OR status = ''");
    $nullCount = $nullStatusQuery->fetch_assoc()['count'];
    echo "Users with NULL/empty status: " . $nullCount . "\n\n";
    
    // Update users with NULL, empty, or non-Active status to 'Active'
    $updateQuery = "UPDATE tbl_user SET status = 'Active' WHERE status IS NULL OR status = '' OR status != 'Active'";
    $result = $conn->query($updateQuery);
    
    if ($result) {
        $affectedRows = $conn->affected_rows;
        echo "✅ Updated " . $affectedRows . " users to 'Active' status\n\n";
    } else {
        echo "❌ Failed to update users: " . $conn->error . "\n";
        exit;
    }
    
    // Show final status distribution
    echo "=== Final Status Distribution ===\n";
    $finalStatusQuery = $conn->query("SELECT status, COUNT(*) as count FROM tbl_user GROUP BY status");
    while ($row = $finalStatusQuery->fetch_assoc()) {
        echo "- Status '" . $row['status'] . "': " . $row['count'] . " users\n";
    }
    echo "\n";
    
    // Test the API query
    echo "=== Testing API Query ===\n";
    $apiQuery = "SELECT id, firstName, lastName 
                 FROM tbl_user 
                 WHERE status = 'Active' 
                 AND firstName IS NOT NULL 
                 AND firstName != '' 
                 AND lastName IS NOT NULL 
                 AND lastName != ''
                 ORDER BY firstName ASC, lastName ASC";
    
    $result = $conn->query($apiQuery);
    if ($result === false) {
        echo "❌ Query failed: " . $conn->error . "\n";
    } else {
        echo "✅ Query successful\n";
        echo "Found " . $result->num_rows . " active users with valid names:\n";
        
        if ($result->num_rows > 0) {
            while ($row = $result->fetch_assoc()) {
                $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
                echo "- ID: " . $row['id'] . ", Name: " . $fullName . "\n";
            }
        } else {
            echo "No users found matching the criteria.\n";
        }
    }
    
    echo "\n✅ Status fix complete! The dropdown should now show users.\n";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
}
?> 