<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response = array();
    
    // 1. Fetch Branch States from tbl_branch_state
    try {
        $stmt = $pdo->query("SELECT id, branch_state_name FROM tbl_branch_state ORDER BY branch_state_name ASC");
        $branchStates = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $branchStates[] = array(
                'id' => $row['id'],
                'name' => $row['branch_state_name']
            );
        }
        $response['branch_states'] = $branchStates;
    } catch (Exception $e) {
        $response['branch_states'] = array();
        $response['branch_states_error'] = $e->getMessage();
    }
    
    // 2. Fetch Account Types from tbl_account_type
    try {
        $stmt = $pdo->query("SELECT id, account_type FROM tbl_account_type ORDER BY account_type ASC");
        $accountTypes = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $accountTypes[] = array(
                'id' => $row['id'],
                'name' => $row['account_type']
            );
        }
        $response['account_types'] = $accountTypes;
    } catch (Exception $e) {
        $response['account_types'] = array();
        $response['account_types_error'] = $e->getMessage();
    }
    
    // 3. Fetch Branch Locations from tbl_branch_location
    try {
        $stmt = $pdo->query("SELECT id, branch_location FROM tbl_branch_location ORDER BY branch_location ASC");
        $branchLocations = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $branchLocations[] = array(
                'id' => $row['id'],
                'name' => $row['branch_location']
            );
        }
        $response['branch_locations'] = $branchLocations;
    } catch (Exception $e) {
        $response['branch_locations'] = array();
        $response['branch_locations_error'] = $e->getMessage();
    }
    
    // 4. Fetch Bank Names from tbl_bank
    try {
        $stmt = $pdo->query("SELECT id, bank_name FROM tbl_bank ORDER BY bank_name ASC");
        $bankNames = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $bankNames[] = array(
                'id' => $row['id'],
                'name' => $row['bank_name']
            );
        }
        $response['bank_names'] = $bankNames;
    } catch (Exception $e) {
        $response['bank_names'] = array();
        $response['bank_names_error'] = $e->getMessage();
    }
    
    // 5. Fetch Reporting To Users (Business Head users)
    try {
        // Get Business Head users with proper status conditions
        $stmt = $pdo->prepare("
            SELECT 
                u.id,
                u.firstName,
                u.lastName,
                u.username,
                u.employee_no,
                d.designation_name,
                CONCAT(u.firstName, ' ', u.lastName) as fullName,
                CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
            FROM tbl_user u
            INNER JOIN tbl_designation d ON u.designation_id = d.id
            WHERE d.designation_name = 'Business Head'
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName ASC, u.lastName ASC
        ");
        $stmt->execute();
        
        $reportingUsers = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $reportingUsers[] = array(
                'id' => $row['id'],
                'firstName' => trim($row['firstName']),
                'lastName' => trim($row['lastName']),
                'username' => $row['username'],
                'employee_no' => $row['employee_no'],
                'designation_name' => $row['designation_name'],
                'fullName' => trim($row['fullName']),
                'displayName' => trim($row['displayName'])
            );
        }
        $response['reporting_users'] = $reportingUsers;
        
        // Log the count for debugging
        $response['reporting_users_count'] = count($reportingUsers);
        
    } catch (Exception $e) {
        $response['reporting_users'] = array();
        $response['reporting_users_error'] = $e->getMessage();
        $response['reporting_users_count'] = 0;
    }
    
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Dropdown data fetched successfully',
        'data' => $response
    ));
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database connection error: ' . $e->getMessage()
    ));
}
?> 