<?php
header('Content-Type: application/json');

$response = array();

// Test 1: Check if db_config.php exists and can be included
if (file_exists('db_config.php')) {
    $response['db_config_exists'] = true;
    
    // Test 2: Try to include the file
    try {
        require_once 'db_config.php';
        $response['db_config_included'] = true;
        
        // Test 3: Check if variables are defined
        if (isset($host) && isset($dbname) && isset($username) && isset($password)) {
            $response['variables_defined'] = true;
            $response['host'] = $host;
            $response['dbname'] = $dbname;
            $response['db_username'] = $username;
            $response['db_password_length'] = strlen($password);
            
            // Test 4: Try to connect to database
            try {
                $conn = getConnection();
                $response['connection_successful'] = true;
                
                // Test 5: Check if tbl_user table exists
                $stmt = $conn->prepare("SHOW TABLES LIKE 'tbl_user'");
                $stmt->execute();
                $tableExists = $stmt->fetch();
                
                if ($tableExists) {
                    $response['tbl_user_exists'] = true;
                    
                    // Test 6: Count users
                    $stmt = $conn->prepare("SELECT COUNT(*) as count FROM tbl_user");
                    $stmt->execute();
                    $userCount = $stmt->fetch(PDO::FETCH_ASSOC);
                    $response['user_count'] = $userCount['count'];
                    
                    // Test 7: Check table structure
                    $stmt = $conn->prepare("DESCRIBE tbl_user");
                    $stmt->execute();
                    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
                    $response['table_structure'] = $columns;
                    
                } else {
                    $response['tbl_user_exists'] = false;
                }
                
                closeConnection($conn);
                
            } catch (Exception $e) {
                $response['connection_successful'] = false;
                $response['connection_error'] = $e->getMessage();
            }
            
        } else {
            $response['variables_defined'] = false;
        }
        
    } catch (Exception $e) {
        $response['db_config_included'] = false;
        $response['include_error'] = $e->getMessage();
    }
    
} else {
    $response['db_config_exists'] = false;
}

echo json_encode($response, JSON_PRETTY_PRINT);
?> 