<?php
header('Content-Type: application/json');

echo "Testing database connection...\n\n";

// Test 1: Check if db_config.php exists
if (file_exists('db_config.php')) {
    echo "✓ db_config.php exists\n";
    require_once 'db_config.php';
} else {
    echo "✗ db_config.php does not exist\n";
    echo "Creating basic connection...\n";
    
    // Basic connection parameters - Updated with actual KfinOne credentials
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    try {
        // Test PDO connection
        $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        echo "✓ PDO connection successful\n";
        
        // Test basic query
        $stmt = $pdo->query("SELECT 1 as test");
        $result = $stmt->fetch();
        echo "✓ Basic query successful: " . json_encode($result) . "\n";
        
        // Test if tables exist
        $tables = ['tbl_user', 'tbl_designation', 'tbl_partner_users'];
        foreach ($tables as $table) {
            try {
                $stmt = $pdo->query("SELECT COUNT(*) as count FROM $table");
                $count = $stmt->fetch();
                echo "✓ Table '$table' exists with " . $count['count'] . " rows\n";
            } catch (Exception $e) {
                echo "✗ Table '$table' error: " . $e->getMessage() . "\n";
            }
        }
        
        // Test Business Head query
        try {
            $query = "SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name 
                      FROM tbl_user u 
                      JOIN tbl_designation d ON u.designation_id = d.id 
                      WHERE d.designation_name = 'Business Head' 
                      LIMIT 1";
            $stmt = $pdo->query($query);
            $bhUser = $stmt->fetch();
            if ($bhUser) {
                echo "✓ Business Head user found: " . json_encode($bhUser) . "\n";
                
                // Test partner query
                $partnerQuery = "SELECT COUNT(*) as count FROM tbl_partner_users WHERE createdBy = :username";
                $stmt = $pdo->prepare($partnerQuery);
                $stmt->bindParam(':username', $bhUser['username'], PDO::PARAM_STR);
                $stmt->execute();
                $partnerCount = $stmt->fetch();
                echo "✓ Partners found for " . $bhUser['username'] . ": " . $partnerCount['count'] . "\n";
            } else {
                echo "✗ No Business Head users found\n";
            }
        } catch (Exception $e) {
            echo "✗ Business Head query error: " . $e->getMessage() . "\n";
        }
        
        $pdo = null;
        
    } catch (PDOException $e) {
        echo "✗ PDO connection failed: " . $e->getMessage() . "\n";
        
        // Try MySQLi
        try {
            $mysqli = new mysqli($host, $username, $password, $dbname);
            if ($mysqli->connect_error) {
                echo "✗ MySQLi connection failed: " . $mysqli->connect_error . "\n";
            } else {
                echo "✓ MySQLi connection successful\n";
                $mysqli->close();
            }
        } catch (Exception $e2) {
            echo "✗ MySQLi connection failed: " . $e2->getMessage() . "\n";
        }
    }
} else {
    echo "Testing with db_config.php...\n";
    
    try {
        $result = testConnection();
        echo json_encode($result, JSON_PRETTY_PRINT) . "\n";
    } catch (Exception $e) {
        echo "✗ Test connection failed: " . $e->getMessage() . "\n";
    }
}

echo "\n=== Database Configuration Help ===\n";
echo "If you're getting connection errors, please update the following in db_config.php:\n";
echo "1. DB_HOST: Your database server address (usually 'localhost')\n";
echo "2. DB_NAME: Your database name\n";
echo "3. DB_USER: Your database username\n";
echo "4. DB_PASS: Your database password\n\n";

echo "Common database names for KfinOne:\n";
echo "- kfinone_db\n";
echo "- emp_kfinone\n";
echo "- kfinone\n";
echo "- kfinone_app\n\n";

echo "Common usernames:\n";
echo "- root (for local development)\n";
echo "- kfinone_user\n";
echo "- emp_kfinone\n\n";

echo "To test your connection, visit:\n";
echo "http://your-domain.com/api/test_db_connection.php\n";
?> 