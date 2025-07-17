<?php
header('Content-Type: text/html');
echo "<h2>Testing fetch_payout_types.php</h2>";

// Test the fetch script directly
$url = "http://192.168.1.100/kfinone/fetch_payout_types.php";

echo "<p>Testing URL: <a href='$url' target='_blank'>$url</a></p>";

// Test with cURL
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 10);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$error = curl_error($ch);
curl_close($ch);

echo "<h3>cURL Test Results:</h3>";
echo "<p>HTTP Code: $httpCode</p>";
echo "<p>cURL Error: " . ($error ? $error : "None") . "</p>";
echo "<p>Response:</p>";
echo "<pre>" . htmlspecialchars($response) . "</pre>";

// Also test the database connection directly
echo "<h3>Direct Database Test:</h3>";
require_once 'db_config.php';

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        echo "<p style='color: red;'>❌ Database connection failed: " . $conn->connect_error . "</p>";
    } else {
        echo "<p style='color: green;'>✅ Database connection successful</p>";
        
        // Test the table
        $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_payout_type'");
        if ($tableCheck->num_rows > 0) {
            echo "<p style='color: green;'>✅ Table 'tbl_payout_type' exists</p>";
            
            $dataCheck = $conn->query("SELECT COUNT(*) as count FROM tbl_payout_type");
            $row = $dataCheck->fetch_assoc();
            echo "<p>📊 Table has " . $row['count'] . " rows</p>";
            
            if ($row['count'] > 0) {
                $result = $conn->query("SELECT * FROM tbl_payout_type LIMIT 5");
                echo "<p>Sample data:</p>";
                echo "<ul>";
                while ($row = $result->fetch_assoc()) {
                    echo "<li>" . $row['payout_name'] . "</li>";
                }
                echo "</ul>";
            }
        } else {
            echo "<p style='color: red;'>❌ Table 'tbl_payout_type' does not exist</p>";
        }
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>❌ Error: " . $e->getMessage() . "</p>";
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 