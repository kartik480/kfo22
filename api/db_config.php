<?php
/**
 * Database Configuration File
 * Updated with actual KfinOne production database credentials
 */

// Database connection parameters
$DB_HOST = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$DB_NAME = 'emp_kfinone';
$DB_USER = 'emp_kfinone';
$DB_PASS = '*F*im1!Y0D25';
$DB_CHARSET = 'utf8';

/**
 * Get database connection using PDO
 * @return PDO
 * @throws Exception
 */
function getConnection() {
    global $DB_HOST, $DB_NAME, $DB_USER, $DB_PASS, $DB_CHARSET;
    
    try {
        $dsn = "mysql:host=$DB_HOST;dbname=$DB_NAME;charset=$DB_CHARSET";
        $pdo = new PDO($dsn, $DB_USER, $DB_PASS);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
        return $pdo;
    } catch (PDOException $e) {
        throw new Exception("Database connection failed: " . $e->getMessage());
    }
}

/**
 * Get database connection using MySQLi
 * @return mysqli
 * @throws Exception
 */
function getMysqliConnection() {
    global $DB_HOST, $DB_NAME, $DB_USER, $DB_PASS;
    
    $mysqli = new mysqli($DB_HOST, $DB_USER, $DB_PASS, $DB_NAME);
    
    if ($mysqli->connect_error) {
        throw new Exception("Database connection failed: " . $mysqli->connect_error);
    }
    
    $mysqli->set_charset("utf8");
    return $mysqli;
}

/**
 * Test database connection
 * @return array
 */
function testConnection() {
    try {
        $pdo = getConnection();
        $stmt = $pdo->query("SELECT 1 as test");
        $result = $stmt->fetch();
        
        return [
            'success' => true,
            'message' => 'Database connection successful',
            'connection_type' => 'PDO',
            'test_result' => $result
        ];
    } catch (Exception $e) {
        try {
            $mysqli = getMysqliConnection();
            $result = $mysqli->query("SELECT 1 as test");
            $row = $result->fetch_assoc();
            $mysqli->close();
            
            return [
                'success' => true,
                'message' => 'Database connection successful',
                'connection_type' => 'MySQLi',
                'test_result' => $row
            ];
        } catch (Exception $e2) {
            return [
                'success' => false,
                'message' => 'Database connection failed',
                'pdo_error' => $e->getMessage(),
                'mysqli_error' => $e2->getMessage()
            ];
        }
    }
}

// Uncomment the line below to test database connection
// echo json_encode(testConnection());
?> 