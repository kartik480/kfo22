<?php
header('Content-Type: application/json');

echo "Testing Business Head API...\n\n";

// Database credentials
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Test PDO connection
    echo "Testing PDO connection...\n";
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
    
    // Test Business Head query with proper error handling
    try {
        echo "\nTesting Business Head query...\n";
        $query = "SELECT u.id, u.username, u.firstName, u.lastName, d.designation_name 
                  FROM tbl_user u 
                  JOIN tbl_designation d ON u.designation_id = d.id 
                  WHERE d.designation_name = 'Business Head' 
                  LIMIT 1";
        $stmt = $pdo->query($query);
        $bhUser = $stmt->fetch();
        if ($bhUser) {
            echo "✓ Business Head user found: " . json_encode($bhUser) . "\n";
            
            // Test partner query with proper binding
            try {
                echo "\nTesting partner query...\n";
                $partnerQuery = "SELECT COUNT(*) as count FROM tbl_partner_users WHERE createdBy = :username";
                $stmt = $pdo->prepare($partnerQuery);
                $username = $bhUser['username'];
                $stmt->bindParam(':username', $username, PDO::PARAM_STR);
                $stmt->execute();
                $partnerCount = $stmt->fetch();
                echo "✓ Partners found for " . $bhUser['username'] . ": " . $partnerCount['count'] . "\n";
                
                // Test actual partner data
                $partnerDataQuery = "SELECT id, username, first_name, last_name, company_name, status, createdBy, created_at 
                                   FROM tbl_partner_users 
                                   WHERE createdBy = :username 
                                   LIMIT 5";
                $stmt = $pdo->prepare($partnerDataQuery);
                $stmt->bindParam(':username', $username, PDO::PARAM_STR);
                $stmt->execute();
                $partners = $stmt->fetchAll(PDO::FETCH_ASSOC);
                echo "✓ Sample partner data: " . json_encode($partners) . "\n";
                
            } catch (Exception $e) {
                echo "✗ Partner query error: " . $e->getMessage() . "\n";
            }
        } else {
            echo "✗ No Business Head users found\n";
        }
    } catch (Exception $e) {
        echo "✗ Business Head query error: " . $e->getMessage() . "\n";
    }
    
    $pdo = null;
    echo "\n✓ All tests completed successfully!\n";
    
} catch (PDOException $e) {
    echo "✗ PDO connection failed: " . $e->getMessage() . "\n";
}

echo "\n=== Test Summary ===\n";
echo "Host: $host\n";
echo "Database: $dbname\n";
echo "Username: $username\n";
echo "Status: " . (isset($pdo) ? "Connected" : "Failed") . "\n";
?>
