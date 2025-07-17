<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

$debug = array();
$debug[] = "Starting debug process...";

try {
    // Step 1: Check if db_config.php exists
    if (file_exists('db_config.php')) {
        $debug[] = "✅ db_config.php exists";
        require_once 'db_config.php';
        $debug[] = "✅ db_config.php loaded successfully";
    } else {
        throw new Exception("db_config.php not found");
    }

    // Step 2: Check if variables are defined
    if (isset($servername) && isset($username) && isset($password) && isset($dbname)) {
        $debug[] = "✅ Database variables are defined";
        $debug[] = "Server: " . $servername;
        $debug[] = "Database: " . $dbname;
        $debug[] = "Username: " . $username;
    } else {
        throw new Exception("Database variables not properly defined");
    }

    // Step 3: Test database connection
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    $debug[] = "✅ Database connection successful";

    // Step 4: Set charset
    $conn->set_charset("utf8");
    $debug[] = "✅ Charset set to utf8";

    // Step 5: Check if table exists
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_partner_users'");
    if ($tableCheck && $tableCheck->num_rows > 0) {
        $debug[] = "✅ tbl_partner_users table exists";
    } else {
        throw new Exception("tbl_partner_users table does not exist");
    }

    // Step 6: Check table structure
    $structureCheck = $conn->query("DESCRIBE tbl_partner_users");
    if ($structureCheck) {
        $debug[] = "✅ Table structure check successful";
        $columns = array();
        while ($row = $structureCheck->fetch_assoc()) {
            $columns[] = $row['Field'];
        }
        $debug[] = "Columns found: " . implode(", ", $columns);
    } else {
        throw new Exception("Could not check table structure: " . $conn->error);
    }

    // Step 7: Test simple query
    $testQuery = $conn->query("SELECT COUNT(*) as count FROM tbl_partner_users");
    if ($testQuery) {
        $count = $testQuery->fetch_assoc()['count'];
        $debug[] = "✅ Simple query successful. Total records: " . $count;
    } else {
        throw new Exception("Simple query failed: " . $conn->error);
    }

    // Step 8: Test the actual query with limited columns
    $sql = "SELECT id, username, alias_name, first_name, last_name FROM tbl_partner_users LIMIT 1";
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Main query failed: " . $conn->error);
    }
    $debug[] = "✅ Main query successful";

    // Step 9: Test JSON encoding
    $testData = array(
        'status' => 'success',
        'message' => 'Test successful',
        'data' => array(),
        'count' => 0
    );
    
    $jsonTest = json_encode($testData);
    if ($jsonTest === false) {
        throw new Exception("JSON encoding failed: " . json_last_error_msg());
    }
    $debug[] = "✅ JSON encoding test successful";

    // Step 10: Test with actual data
    $partnerUsers = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $partnerUser = array(
                'id' => isset($row['id']) ? $row['id'] : '',
                'username' => isset($row['username']) ? $row['username'] : '',
                'alias_name' => isset($row['alias_name']) ? $row['alias_name'] : '',
                'first_name' => isset($row['first_name']) ? $row['first_name'] : '',
                'last_name' => isset($row['last_name']) ? $row['last_name'] : ''
            );
            $partnerUsers[] = $partnerUser;
        }
    }
    $debug[] = "✅ Data processing successful. Processed " . count($partnerUsers) . " records";

    // Final response
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Debug completed successfully',
        'debug_log' => $debug,
        'data' => $partnerUsers,
        'count' => count($partnerUsers)
    ), JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    $debug[] = "❌ Error: " . $e->getMessage();
    
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Debug failed: ' . $e->getMessage(),
        'debug_log' => $debug
    ));
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 