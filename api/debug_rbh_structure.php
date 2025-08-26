<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
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
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $debugInfo = [];
    
    // 1. Check if tbl_designation exists and what's in it
    try {
        $designationCheck = "SHOW TABLES LIKE 'tbl_designation'";
        $stmt = $pdo->prepare($designationCheck);
        $stmt->execute();
        $designationTableExists = $stmt->fetch();
        
        if ($designationTableExists) {
            $designationQuery = "SELECT * FROM tbl_designation ORDER BY id";
            $stmt = $pdo->prepare($designationQuery);
            $stmt->execute();
            $designations = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $debugInfo['designation_table'] = [
                'exists' => true,
                'count' => count($designations),
                'data' => $designations
            ];
        } else {
            $debugInfo['designation_table'] = ['exists' => false];
        }
    } catch (Exception $e) {
        $debugInfo['designation_table'] = ['exists' => false, 'error' => $e->getMessage()];
    }
    
    // 2. Check if tbl_department exists and what's in it
    try {
        $departmentCheck = "SHOW TABLES LIKE 'tbl_department'";
        $stmt = $pdo->prepare($departmentCheck);
        $stmt->execute();
        $departmentTableExists = $stmt->fetch();
        
        if ($departmentTableExists) {
            $departmentQuery = "SELECT * FROM tbl_department ORDER BY id";
            $stmt = $pdo->prepare($departmentQuery);
            $stmt->execute();
            $departments = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $debugInfo['department_table'] = [
                'exists' => true,
                'count' => count($departments),
                'data' => $departments
            ];
        } else {
            $debugInfo['department_table'] = ['exists' => false];
        }
    } catch (Exception $e) {
        $debugInfo['department_table'] = ['exists' => false, 'error' => $e->getMessage()];
    }
    
    // 3. Check all unique ranks in tbl_user
    try {
        $rankQuery = "SELECT DISTINCT rank, COUNT(*) as count FROM tbl_user WHERE status = 'active' GROUP BY rank ORDER BY count DESC";
        $stmt = $pdo->prepare($rankQuery);
        $stmt->execute();
        $ranks = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['user_ranks'] = $ranks;
    } catch (Exception $e) {
        $debugInfo['user_ranks'] = ['error' => $e->getMessage()];
    }
    
    // 4. Check all unique designation_ids in tbl_user
    try {
        $designationIdQuery = "SELECT DISTINCT designation_id, COUNT(*) as count FROM tbl_user WHERE status = 'active' AND designation_id IS NOT NULL GROUP BY designation_id ORDER BY count DESC";
        $stmt = $pdo->prepare($designationIdQuery);
        $stmt->execute();
        $designationIds = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['user_designation_ids'] = $designationIds;
    } catch (Exception $e) {
        $debugInfo['user_designation_ids'] = ['error' => $e->getMessage()];
    }
    
    // 5. Check all unique department_ids in tbl_user
    try {
        $departmentIdQuery = "SELECT DISTINCT department_id, COUNT(*) as count FROM tbl_user WHERE status = 'active' AND department_id IS NOT NULL GROUP BY department_id ORDER BY count DESC";
        $stmt = $pdo->prepare($departmentIdQuery);
        $stmt->execute();
        $departmentIds = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['user_department_ids'] = $departmentIds;
    } catch (Exception $e) {
        $debugInfo['user_department_ids'] = ['error' => $e->getMessage()];
    }
    
    // 6. Sample of users with different ranks
    try {
        $sampleUsersQuery = "SELECT id, username, firstName, lastName, rank, designation_id, department_id, employee_no FROM tbl_user WHERE status = 'active' ORDER BY rank, designation_id LIMIT 20";
        $stmt = $pdo->prepare($sampleUsersQuery);
        $stmt->execute();
        $sampleUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['sample_users'] = $sampleUsers;
    } catch (Exception $e) {
        $debugInfo['sample_users'] = ['error' => $e->getMessage()];
    }
    
    // 7. Check for users with specific patterns that might be RBH
    try {
        $patternQuery = "SELECT id, username, firstName, lastName, rank, designation_id, department_id, employee_no FROM tbl_user WHERE status = 'active' AND (username LIKE '%rbh%' OR username LIKE '%regional%' OR username LIKE '%business%' OR username LIKE '%head%' OR username LIKE '%manager%' OR username LIKE '%senior%' OR username LIKE '%executive%' OR username LIKE '%director%' OR username LIKE '%officer%' OR username LIKE '%lead%' OR username LIKE '%supervisor%' OR username LIKE '%admin%' OR username LIKE '%chief%') ORDER BY username";
        $stmt = $pdo->prepare($patternQuery);
        $stmt->execute();
        $patternUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $debugInfo['pattern_matched_users'] = $patternUsers;
    } catch (Exception $e) {
        $debugInfo['pattern_matched_users'] = ['error' => $e->getMessage()];
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Debug information for RBH users structure',
        'debug_info' => $debugInfo,
        'timestamp' => date('Y-m-d H:i:s')
    ]);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?>
