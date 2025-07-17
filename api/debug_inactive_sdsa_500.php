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
    
    $debugInfo = array();
    
    // Step 1: Check database connection
    $debugInfo['database_connected'] = true;
    $debugInfo['connection_error'] = null;
    
    // Step 2: Check if table exists
    try {
        $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_sdsa_users'");
        $stmt->execute();
        $tableExists = $stmt->rowCount() > 0;
        $debugInfo['table_exists'] = $tableExists;
        $debugInfo['table_check_error'] = null;
    } catch (Exception $e) {
        $debugInfo['table_exists'] = false;
        $debugInfo['table_check_error'] = $e->getMessage();
    }
    
    // Step 3: If table doesn't exist, create it
    if (!$debugInfo['table_exists']) {
        try {
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
            $debugInfo['table_created'] = true;
            $debugInfo['table_creation_error'] = null;
        } catch (Exception $e) {
            $debugInfo['table_created'] = false;
            $debugInfo['table_creation_error'] = $e->getMessage();
        }
    } else {
        $debugInfo['table_created'] = false;
        $debugInfo['table_creation_error'] = null;
    }
    
    // Step 4: Check table structure
    try {
        $stmt = $conn->prepare("DESCRIBE tbl_sdsa_users");
        $stmt->execute();
        $structure = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['table_structure'] = $structure;
        $debugInfo['structure_check_error'] = null;
    } catch (Exception $e) {
        $debugInfo['table_structure'] = array();
        $debugInfo['structure_check_error'] = $e->getMessage();
    }
    
    // Step 5: Check if data exists
    try {
        $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sdsa_users");
        $stmt->execute();
        $countResult = $stmt->fetch(PDO::FETCH_ASSOC);
        $totalCount = $countResult['total'];
        $debugInfo['total_count'] = $totalCount;
        $debugInfo['count_error'] = null;
    } catch (Exception $e) {
        $debugInfo['total_count'] = 0;
        $debugInfo['count_error'] = $e->getMessage();
    }
    
    // Step 6: Add sample data if table is empty
    if ($debugInfo['total_count'] == 0) {
        try {
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
            
            $debugInfo['sample_data_added'] = true;
            $debugInfo['inserted_count'] = $insertedCount;
            $debugInfo['sample_data_error'] = null;
        } catch (Exception $e) {
            $debugInfo['sample_data_added'] = false;
            $debugInfo['inserted_count'] = 0;
            $debugInfo['sample_data_error'] = $e->getMessage();
        }
    } else {
        $debugInfo['sample_data_added'] = false;
        $debugInfo['inserted_count'] = 0;
        $debugInfo['sample_data_error'] = null;
    }
    
    // Step 7: Test the exact query from get_inactive_sdsa_users.php
    try {
        $stmt = $conn->prepare("
            SELECT 
                id,
                name,
                email,
                phone,
                status,
                created_at,
                updated_at
            FROM tbl_sdsa_users 
            WHERE status = 0 
            ORDER BY name ASC
        ");
        $stmt->execute();
        $inactiveUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['test_query_success'] = true;
        $debugInfo['inactive_users_count'] = count($inactiveUsers);
        $debugInfo['test_query_error'] = null;
    } catch (Exception $e) {
        $debugInfo['test_query_success'] = false;
        $debugInfo['inactive_users_count'] = 0;
        $debugInfo['test_query_error'] = $e->getMessage();
    }
    
    // Step 8: Get final counts
    try {
        $stmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sdsa_users");
        $stmt->execute();
        $finalCountResult = $stmt->fetch(PDO::FETCH_ASSOC);
        $finalTotal = $finalCountResult['total'];
        
        $stmt = $conn->prepare("SELECT COUNT(*) as inactive FROM tbl_sdsa_users WHERE status = 0");
        $stmt->execute();
        $finalInactiveResult = $stmt->fetch(PDO::FETCH_ASSOC);
        $finalInactive = $finalInactiveResult['inactive'];
        
        $debugInfo['final_total'] = $finalTotal;
        $debugInfo['final_inactive'] = $finalInactive;
        $debugInfo['final_count_error'] = null;
    } catch (Exception $e) {
        $debugInfo['final_total'] = 0;
        $debugInfo['final_inactive'] = 0;
        $debugInfo['final_count_error'] = $e->getMessage();
    }
    
    echo json_encode(array(
        'success' => true,
        'message' => 'Debug information collected successfully',
        'debug_info' => $debugInfo,
        'summary' => array(
            'database_connected' => $debugInfo['database_connected'],
            'table_exists' => $debugInfo['table_exists'],
            'table_created' => $debugInfo['table_created'],
            'sample_data_added' => $debugInfo['sample_data_added'],
            'test_query_working' => $debugInfo['test_query_success'],
            'total_users' => $debugInfo['final_total'],
            'inactive_users' => $debugInfo['final_inactive']
        )
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Debug inactive SDSA 500 error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while debugging: ' . $e->getMessage(),
        'debug_info' => array(
            'database_connected' => false,
            'connection_error' => $e->getMessage()
        )
    ));
}
?> 