<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    // Test 1: Include database configuration
    require_once 'db_config.php';
    
    // Test 2: Check if global $conn exists and works
    $globalConnWorking = false;
    if (isset($conn) && $conn) {
        try {
            $testQuery = $conn->query("SELECT 1 as test");
            if ($testQuery && $testQuery->fetch_assoc()['test'] == 1) {
                $globalConnWorking = true;
            }
        } catch (Exception $e) {
            $globalConnWorking = false;
        }
    }
    
    // Test 3: Create a new connection
    $newConn = new mysqli($db_host, $db_username, $db_password, $db_name);
    $newConnWorking = false;
    
    if (!$newConn->connect_error) {
        $newConn->set_charset("utf8");
        $testQuery2 = $newConn->query("SELECT 1 as test");
        if ($testQuery2 && $testQuery2->fetch_assoc()['test'] == 1) {
            $newConnWorking = true;
        }
    }
    
    // Test 4: Check if tables exist
    $tablesExist = false;
    if ($newConnWorking) {
        $tableCheck = $newConn->query("SHOW TABLES LIKE 'tbl_partner_users'");
        $tablesExist = ($tableCheck && $tableCheck->num_rows > 0);
    }
    
    // Test 5: Try a simple query
    $simpleQueryWorks = false;
    $totalCount = 0;
    if ($tablesExist) {
        $countQuery = $newConn->query("SELECT COUNT(*) as count FROM tbl_partner_users");
        if ($countQuery) {
            $result = $countQuery->fetch_assoc();
            $totalCount = $result['count'];
            $simpleQueryWorks = true;
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'tests' => [
            'global_conn_working' => $globalConnWorking,
            'new_conn_working' => $newConnWorking,
            'tables_exist' => $tablesExist,
            'simple_query_works' => $simpleQueryWorks,
            'total_partner_users' => $totalCount
        ],
        'connection_info' => [
            'host' => $db_host,
            'database' => $db_name,
            'username' => $db_username,
            'password_length' => strlen($db_password)
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Test failed: ' . $e->getMessage(),
        'error_type' => get_class($e),
        'error_file' => $e->getFile(),
        'error_line' => $e->getLine()
    ]);
} finally {
    if (isset($newConn)) {
        $newConn->close();
    }
}
?> 