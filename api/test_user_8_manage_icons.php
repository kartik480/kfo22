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
    
    // Check if tbl_user exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_user'");
    if ($stmt->rowCount() > 0) {
        echo "Table tbl_user exists\n";
    } else {
        echo "Table tbl_user does NOT exist\n";
        exit();
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_user");
    echo "Table structure:\n";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "- " . $row['Field'] . " (" . $row['Type'] . ")\n";
    }
    
    // Check if user ID 8 exists
    $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM tbl_user WHERE id = 8");
    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($result['count'] > 0) {
        echo "\nUser ID 8 exists in the database\n";
        
        // Get user details
        $stmt = $pdo->prepare("SELECT id, username, manage_icons FROM tbl_user WHERE id = 8");
        $stmt->execute();
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        echo "User details:\n";
        echo "- ID: " . $user['id'] . "\n";
        echo "- Username: " . $user['username'] . "\n";
        echo "- manage_icons: " . ($user['manage_icons'] ?: 'NULL/EMPTY') . "\n";
        
        // Check if manage_icons column has data
        if ($user['manage_icons'] && !empty(trim($user['manage_icons']))) {
            echo "- manage_icons has data\n";
        } else {
            echo "- manage_icons is empty or NULL\n";
        }
        
    } else {
        echo "\nUser ID 8 does NOT exist in the database\n";
    }
    
    // Check total users
    $stmt = $pdo->query("SELECT COUNT(*) as total FROM tbl_user");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "\nTotal users in tbl_user: " . $result['total'] . "\n";
    
    // Check if manage_icons column has any data
    $stmt = $pdo->query("SELECT COUNT(*) as count FROM tbl_user WHERE manage_icons IS NOT NULL AND manage_icons != ''");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "Users with manage_icons data: " . $result['count'] . "\n";
    
    // Show sample users with manage_icons
    $stmt = $pdo->query("SELECT id, username, manage_icons FROM tbl_user WHERE manage_icons IS NOT NULL AND manage_icons != '' LIMIT 5");
    echo "\nSample users with manage_icons:\n";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "- ID: " . $row['id'] . ", Username: " . $row['username'] . ", Icons: " . $row['manage_icons'] . "\n";
    }
    
} catch (PDOException $e) {
    echo "Database error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "Server error: " . $e->getMessage() . "\n";
}
?>
