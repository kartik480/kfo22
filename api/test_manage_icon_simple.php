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
    
    echo "✅ Database connection successful!\n\n";
    
    // Test the exact icon IDs from user 8
    $iconIds = [5, 6, 2, 7, 8, 9, 10, 1, 16, 3, 15, 14, 13, 12, 11, 4];
    
    echo "🔍 Testing icon IDs: " . implode(', ', $iconIds) . "\n\n";
    
    // Check if tbl_manage_icon exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($stmt->rowCount() > 0) {
        echo "✅ Table tbl_manage_icon EXISTS\n";
    } else {
        echo "❌ Table tbl_manage_icon does NOT exist\n";
        exit();
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_manage_icon");
    echo "\n📋 Table structure:\n";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "  - " . $row['Field'] . " (" . $row['Type'] . ")\n";
    }
    
    // Check total records
    $stmt = $pdo->query("SELECT COUNT(*) as total FROM tbl_manage_icon");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "\n📊 Total records in tbl_manage_icon: " . $result['total'] . "\n";
    
    // Check which specific IDs exist
    echo "\n🔍 Checking which icon IDs exist:\n";
    $existingIds = [];
    foreach ($iconIds as $id) {
        $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM tbl_manage_icon WHERE id = ?");
        $stmt->execute([$id]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($result['count'] > 0) {
            echo "  ✅ ID $id: EXISTS\n";
            $existingIds[] = $id;
        } else {
            echo "  ❌ ID $id: NOT FOUND\n";
        }
    }
    
    // Show sample data for existing IDs
    if (!empty($existingIds)) {
        echo "\n📋 Sample data for existing icons:\n";
        $placeholders = str_repeat('?,', count($existingIds) - 1) . '?';
        $stmt = $pdo->prepare("SELECT id, icon_name, icon_url, icon_image, icon_description, status FROM tbl_manage_icon WHERE id IN ($placeholders) LIMIT 3");
        $stmt->execute($existingIds);
        
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            echo "\n  ID: " . $row['id'] . "\n";
            echo "    Name: " . $row['icon_name'] . "\n";
            echo "    URL: " . $row['icon_url'] . "\n";
            echo "    Description: " . $row['icon_description'] . "\n";
            echo "    Status: " . $row['status'] . "\n";
            echo "    ---";
        }
    }
    
    // Test the exact query that will be used
    echo "\n\n🧪 Testing the exact API query:\n";
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    $query = "SELECT id, icon_name, icon_url, icon_image, icon_description, status FROM tbl_manage_icon WHERE id IN ($placeholders) AND status = 'active' ORDER BY icon_name";
    
    echo "Query: " . $query . "\n";
    echo "Parameters: " . implode(', ', $iconIds) . "\n";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute($iconIds);
    
    $icons = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo "\nResult: " . count($icons) . " icons found\n";
    
    if ($icons) {
        echo "Sample result:\n";
        echo json_encode($icons[0], JSON_PRETTY_PRINT);
    }
    
} catch (PDOException $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "❌ Server error: " . $e->getMessage() . "\n";
}
?>
