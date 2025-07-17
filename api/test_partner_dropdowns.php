<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    $results = array();
    
    // Test 1: Check if tables exist
    $tables = array('tbl_partner_type', 'tbl_branch_state', 'tbl_branch_location', 'tbl_bank', 'tbl_account_type');
    
    foreach ($tables as $table) {
        $sql = "SHOW TABLES LIKE '$table'";
        $result = $conn->query($sql);
        $results['table_exists'][$table] = $result->num_rows > 0;
    }
    
    // Test 2: Check data in each table
    if ($results['table_exists']['tbl_partner_type']) {
        $sql = "SELECT COUNT(*) as count FROM tbl_partner_type";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        $results['partner_type_count'] = $row['count'];
        
        if ($row['count'] > 0) {
            $sql = "SELECT id, partner_type FROM tbl_partner_type LIMIT 5";
            $result = $conn->query($sql);
            $results['partner_type_sample'] = array();
            while ($row = $result->fetch_assoc()) {
                $results['partner_type_sample'][] = $row;
            }
        }
    }
    
    if ($results['table_exists']['tbl_branch_state']) {
        $sql = "SELECT COUNT(*) as count FROM tbl_branch_state";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        $results['branch_state_count'] = $row['count'];
        
        if ($row['count'] > 0) {
            $sql = "SELECT id, branch_state_name FROM tbl_branch_state LIMIT 5";
            $result = $conn->query($sql);
            $results['branch_state_sample'] = array();
            while ($row = $result->fetch_assoc()) {
                $results['branch_state_sample'][] = $row;
            }
        }
    }
    
    if ($results['table_exists']['tbl_branch_location']) {
        $sql = "SELECT COUNT(*) as count FROM tbl_branch_location";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        $results['branch_location_count'] = $row['count'];
        
        if ($row['count'] > 0) {
            $sql = "SELECT id, branch_location FROM tbl_branch_location LIMIT 5";
            $result = $conn->query($sql);
            $results['branch_location_sample'] = array();
            while ($row = $result->fetch_assoc()) {
                $results['branch_location_sample'][] = $row;
            }
        }
    }
    
    if ($results['table_exists']['tbl_bank']) {
        $sql = "SELECT COUNT(*) as count FROM tbl_bank";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        $results['bank_count'] = $row['count'];
        
        if ($row['count'] > 0) {
            $sql = "SELECT id, bank_name FROM tbl_bank LIMIT 5";
            $result = $conn->query($sql);
            $results['bank_sample'] = array();
            while ($row = $result->fetch_assoc()) {
                $results['bank_sample'][] = $row;
            }
        }
    }
    
    if ($results['table_exists']['tbl_account_type']) {
        $sql = "SELECT COUNT(*) as count FROM tbl_account_type";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        $results['account_type_count'] = $row['count'];
        
        if ($row['count'] > 0) {
            $sql = "SELECT id, account_type FROM tbl_account_type LIMIT 5";
            $result = $conn->query($sql);
            $results['account_type_sample'] = array();
            while ($row = $result->fetch_assoc()) {
                $results['account_type_sample'][] = $row;
            }
        }
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Database check completed',
        'data' => $results
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => null
    ]);
}
?> 