<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "Database connection successful!\n\n";
    
    // Check if tbl_manage_icon exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($stmt->rowCount() > 0) {
        echo "Table tbl_manage_icon exists\n";
    } else {
        echo "Table tbl_manage_icon does NOT exist\n";
        exit();
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_manage_icon");
    echo "Table structure:\n";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "- " . $row['Field'] . " (" . $row['Type'] . ")\n";
    }
    
    // Check total records
    $stmt = $pdo->query("SELECT COUNT(*) as total FROM tbl_manage_icon");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "\nTotal records in tbl_manage_icon: " . $result['total'] . "\n";
    
    // Check if specific IDs exist
    $testIds = [1, 2, 3, 4, 5];
    echo "\nChecking if test IDs exist:\n";
    foreach ($testIds as $id) {
        $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM tbl_manage_icon WHERE id = ?");
        $stmt->execute([$id]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        echo "- ID $id: " . ($result['count'] > 0 ? "EXISTS" : "NOT FOUND") . "\n";
    }
    
    // Show sample records
    $stmt = $pdo->query("SELECT id, icon_name, icon_url, icon_description, status FROM tbl_manage_icon LIMIT 5");
    echo "\nSample records:\n";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "- ID: " . $row['id'] . ", Name: " . $row['icon_name'] . ", URL: " . $row['icon_url'] . ", Status: " . $row['status'] . "\n";
    }
    
} catch (PDOException $e) {
    echo "Database error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "Server error: " . $e->getMessage() . "\n";
}
?>
