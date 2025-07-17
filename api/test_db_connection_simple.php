<?php
// Simple database connection test
require_once 'db_config.php';

echo "Testing database connection...\n";

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "✓ Database connection successful!\n";
    echo "Host: $host\n";
    echo "Database: $dbname\n";
    echo "Username: $username\n";
    
    // Test if we can query the database
    $result = $conn->query("SELECT 1 as test");
    if ($result) {
        echo "✓ Database query test successful!\n";
    } else {
        echo "✗ Database query test failed!\n";
    }
    
    $conn->close();
    echo "✓ Connection closed successfully!\n";
    
} catch (Exception $e) {
    echo "✗ Error: " . $e->getMessage() . "\n";
}
?> 