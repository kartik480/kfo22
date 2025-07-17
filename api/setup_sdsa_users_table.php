<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Include database configuration
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Check if table exists
    $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sdsa_users'");
    $stmt->execute();
    $tableExists = $stmt->rowCount() > 0;
    
    if ($tableExists) {
        echo json_encode(array(
            'success' => true,
            'message' => 'Table tbl_sdsa_users already exists.',
            'action' => 'none'
        ));
        exit();
    }
    
    // Create the table
    $createTableSQL = "
        CREATE TABLE tbl_sdsa_users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) UNIQUE NOT NULL,
            phone VARCHAR(20) NOT NULL,
            status TINYINT(1) DEFAULT 1 COMMENT '1=Active, 0=Inactive',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    ";
    
    $stmt = $conn->prepare($createTableSQL);
    $stmt->execute();
    
    // Add sample data
    $sampleUsers = array(
        array('name' => 'John Doe', 'email' => 'john.doe@example.com', 'phone' => '1234567890', 'status' => 0),
        array('name' => 'Jane Smith', 'email' => 'jane.smith@example.com', 'phone' => '9876543210', 'status' => 0),
        array('name' => 'Mike Johnson', 'email' => 'mike.johnson@example.com', 'phone' => '5551234567', 'status' => 1),
        array('name' => 'Sarah Wilson', 'email' => 'sarah.wilson@example.com', 'phone' => '5559876543', 'status' => 0),
        array('name' => 'David Brown', 'email' => 'david.brown@example.com', 'phone' => '5554567890', 'status' => 1),
        array('name' => 'Lisa Davis', 'email' => 'lisa.davis@example.com', 'phone' => '5557890123', 'status' => 0),
        array('name' => 'Robert Miller', 'email' => 'robert.miller@example.com', 'phone' => '5553210987', 'status' => 1),
        array('name' => 'Emily Garcia', 'email' => 'emily.garcia@example.com', 'phone' => '5556543210', 'status' => 0),
        array('name' => 'James Rodriguez', 'email' => 'james.rodriguez@example.com', 'phone' => '5550987654', 'status' => 1),
        array('name' => 'Maria Martinez', 'email' => 'maria.martinez@example.com', 'phone' => '5555432109', 'status' => 0)
    );
    
    $insertStmt = $conn->prepare("INSERT INTO tbl_sdsa_users (name, email, phone, status) VALUES (?, ?, ?, ?)");
    $insertedCount = 0;
    
    foreach ($sampleUsers as $user) {
        try {
            $insertStmt->execute(array($user['name'], $user['email'], $user['phone'], $user['status']));
            $insertedCount++;
        } catch (PDOException $e) {
            // Ignore duplicate key errors
            if ($e->getCode() != 23000) {
                throw $e;
            }
        }
    }
    
    // Get final counts
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sdsa_users");
    $stmt->execute();
    $totalResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $totalResult['total'];
    
    $stmt = $conn->prepare("SELECT COUNT(*) as inactive FROM tbl_sdsa_users WHERE status = 0");
    $stmt->execute();
    $inactiveResult = $stmt->fetch(PDO::FETCH_ASSOC);
    $inactiveCount = $inactiveResult['inactive'];
    
    echo json_encode(array(
        'success' => true,
        'message' => "Table tbl_sdsa_users created successfully with $insertedCount sample users.",
        'action' => 'created',
        'sample_data_added' => $insertedCount,
        'total_users' => $totalCount,
        'inactive_users' => $inactiveCount
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Setup SDSA users table error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while setting up SDSA users table: ' . $e->getMessage()
    ));
}
?> 