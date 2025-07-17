<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database configuration
require_once 'db_config.php';

// Set headers for JSON response
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

try {
    $conn = getConnection();
    
    // Test 1: Check if connection works
    echo "Database connection test:\n";
    echo "✅ Database connection successful\n\n";
    
    // Test 2: Check if tbl_user table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
    $stmt->execute();
    $tableExists = $stmt->fetch();
    
    if ($tableExists) {
        echo "✅ tbl_user table exists\n\n";
        
        // Test 3: Check table structure
        $stmt = $conn->prepare("DESCRIBE tbl_user");
        $stmt->execute();
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo "Table structure:\n";
        foreach ($columns as $column) {
            echo "- {$column['Field']} ({$column['Type']})\n";
        }
        echo "\n";
        
        // Test 4: Count users
        $stmt = $conn->prepare("SELECT COUNT(*) as count FROM tbl_user");
        $stmt->execute();
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        echo "Total users in table: {$result['count']}\n\n";
        
        // Test 5: Check specific user
        $stmt = $conn->prepare("SELECT username, password FROM tbl_user WHERE username = ?");
        $stmt->execute(['krajeshk']);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($user) {
            echo "✅ User 'krajeshk' found\n";
            echo "Password length: " . strlen($user['password']) . " characters\n";
            echo "Password (first 10 chars): " . substr($user['password'], 0, 10) . "...\n";
        } else {
            echo "❌ User 'krajeshk' not found\n";
        }
        
    } else {
        echo "❌ tbl_user table does not exist\n";
    }
    
    closeConnection($conn);
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
    echo "Error type: " . get_class($e) . "\n";
}
?> 