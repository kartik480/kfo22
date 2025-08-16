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

echo "🔍 Debugging get_manage_icons.php 400 Error\n";
echo "==========================================\n\n";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "✅ Database connection successful!\n\n";
    
    // Test the exact URL that Android app is calling
    $testIds = "5,6,2,7,8,9,10,1,16,3,15,14,13,12,11,4";
    echo "🧪 Testing with IDs: $testIds\n";
    echo "This is the exact string from Android app\n\n";
    
    // Simulate the API logic step by step
    echo "Step 1: Check if ids parameter exists\n";
    $iconIds = $testIds; // Simulate $_GET['ids']
    echo "   ✅ Icon IDs received: '$iconIds'\n";
    
    if (!$iconIds) {
        echo "   ❌ Icon IDs are required\n";
        exit();
    }
    
    echo "\nStep 2: Split and clean the IDs\n";
    $idArray = array_map('trim', explode(',', $iconIds));
    echo "   Raw split result: " . json_encode($idArray) . "\n";
    
    $idArray = array_filter($idArray, 'is_numeric');
    echo "   After numeric filter: " . json_encode($idArray) . "\n";
    
    if (empty($idArray)) {
        echo "   ❌ Invalid icon IDs provided\n";
        exit();
    }
    
    echo "   ✅ Valid IDs found: " . count($idArray) . "\n";
    
    echo "\nStep 3: Create SQL placeholders\n";
    $placeholders = str_repeat('?,', count($idArray) - 1) . '?';
    echo "   Placeholders: $placeholders\n";
    
    echo "\nStep 4: Check if tbl_manage_icon exists\n";
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_manage_icon'");
    if ($stmt->rowCount() > 0) {
        echo "   ✅ Table tbl_manage_icon EXISTS\n";
    } else {
        echo "   ❌ Table tbl_manage_icon does NOT exist\n";
        echo "   This is why you're getting 400 error!\n";
        exit();
    }
    
    echo "\nStep 5: Check table structure\n";
    $stmt = $pdo->query("DESCRIBE tbl_manage_icon");
    echo "   Table columns:\n";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "     - " . $row['Field'] . " (" . $row['Type'] . ")\n";
    }
    
    echo "\nStep 6: Check if any of the IDs exist\n";
    $placeholders = str_repeat('?,', count($idArray) - 1) . '?';
    $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM tbl_manage_icon WHERE id IN ($placeholders)");
    $stmt->execute($idArray);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "   IDs found in table: " . $result['count'] . " out of " . count($idArray) . "\n";
    
    if ($result['count'] == 0) {
        echo "   ❌ None of the user's icon IDs exist in tbl_manage_icon!\n";
        echo "   This is why you're getting 400 error!\n";
    } else {
        echo "   ✅ Some IDs exist, let's see which ones\n";
        
        // Show which IDs exist
        foreach ($idArray as $id) {
            $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM tbl_manage_icon WHERE id = ?");
            $stmt->execute([$id]);
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            if ($result['count'] > 0) {
                echo "     ✅ ID $id: EXISTS\n";
            } else {
                echo "     ❌ ID $id: NOT FOUND\n";
            }
        }
    }
    
    echo "\nStep 7: Execute the final query\n";
    $stmt = $pdo->prepare("SELECT id, icon_name, icon_url, icon_image, icon_description, status FROM tbl_manage_icon WHERE id IN ($placeholders) AND status = 'active' ORDER BY icon_name");
    $stmt->execute($idArray);
    
    $icons = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo "   Final query result: " . count($icons) . " icons\n";
    
    if ($icons) {
        echo "   ✅ Query successful! Sample data:\n";
        echo "   " . json_encode($icons[0], JSON_PRETTY_PRINT) . "\n";
    } else {
        echo "   ⚠️ Query returned no results (empty array)\n";
    }
    
} catch (PDOException $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "❌ Server error: " . $e->getMessage() . "\n";
}
?>
