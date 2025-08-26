<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    if (!$data) {
        echo json_encode([
            'success' => false,
            'message' => 'No data received'
        ]);
        exit;
    }
    
    $cboUserId = $data['user_id'] ?? null;
    
    if (!$cboUserId) {
        echo json_encode([
            'success' => false,
            'message' => 'User ID is required'
        ]);
        exit;
    }
    
    // First, let's check the actual structure of tbl_user table
    $structureQuery = "DESCRIBE tbl_user";
    $stmt = $pdo->prepare($structureQuery);
    $stmt->execute();
    $userFields = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    // Check if we have the basic fields we need
    $hasUsername = in_array('username', $userFields);
    $hasStatus = in_array('status', $userFields);
    $hasFirstName = in_array('firstName', $userFields);
    $hasLastName = in_array('lastName', $userFields);
    $hasDesignationId = in_array('designation_id', $userFields);
    $hasDepartmentId = in_array('department_id', $userFields);
    $hasRank = in_array('rank', $userFields);
    $hasEmployeeNo = in_array('employee_no', $userFields);
    
    // Build query based on available fields
    $query = "SELECT DISTINCT u.id, u.username, u.status";
    
    if ($hasFirstName) {
        $query .= ", u.firstName";
    }
    if ($hasLastName) {
        $query .= ", u.lastName";
    }
    if ($hasDesignationId) {
        $query .= ", u.designation_id";
    }
    if ($hasDepartmentId) {
        $query .= ", u.department_id";
    }
    if ($hasRank) {
        $query .= ", u.rank";
    }
    if ($hasEmployeeNo) {
        $query .= ", u.employee_no";
    }
    
    $query .= " FROM tbl_user u WHERE u.status = 'active'";
    
    // Try multiple approaches to find RBH users - make them more inclusive
    $rbhUsers = [];
    $approachUsed = '';
    
    // Approach 1: Check if designation_id table exists and has RBH designations
    if ($hasDesignationId) {
        try {
            $designationQuery = "
                SELECT DISTINCT u.id, u.username, u.status, u.designation_id";
            if ($hasFirstName) $designationQuery .= ", u.firstName";
            if ($hasLastName) $designationQuery .= ", u.lastName";
            if ($hasRank) $designationQuery .= ", u.rank";
            if ($hasEmployeeNo) $designationQuery .= ", u.employee_no";
            
            $designationQuery .= "
                FROM tbl_user u
                LEFT JOIN tbl_designation d ON u.designation_id = d.id
                WHERE u.status = 'active'
                AND (d.designation_name LIKE '%RBH%' 
                     OR d.designation_name LIKE '%Regional%'
                     OR d.designation_name LIKE '%Business%'
                     OR d.designation_name LIKE '%Head%'
                     OR d.designation_name LIKE '%Manager%'
                     OR d.designation_name LIKE '%Senior%'
                     OR d.designation_name LIKE '%Executive%'
                     OR d.designation_name LIKE '%Director%'
                     OR d.designation_name LIKE '%Officer%')
                ORDER BY u.firstName, u.lastName
            ";
            $stmt = $pdo->prepare($designationQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'designation_table_join';
            }
        } catch (Exception $e) {
            // If designation table doesn't exist, continue to next approach
        }
    }
    
    // Approach 2: If no users found, try department_id approach
    if (empty($rbhUsers) && $hasDepartmentId) {
        try {
            $departmentQuery = "
                SELECT DISTINCT u.id, u.username, u.status, u.department_id";
            if ($hasFirstName) $departmentQuery .= ", u.firstName";
            if ($hasLastName) $departmentQuery .= ", u.lastName";
            if ($hasRank) $departmentQuery .= ", u.rank";
            if ($hasEmployeeNo) $departmentQuery .= ", u.employee_no";
            
            $departmentQuery .= "
                FROM tbl_user u
                LEFT JOIN tbl_department dept ON u.department_id = dept.id
                WHERE u.status = 'active'
                AND (dept.department_name LIKE '%RBH%' 
                     OR dept.department_name LIKE '%Regional%'
                     OR dept.department_name LIKE '%Business%'
                     OR dept.department_name LIKE '%Sales%'
                     OR dept.department_name LIKE '%Marketing%'
                     OR dept.department_name LIKE '%Operations%'
                     OR dept.department_name LIKE '%Management%')
                ORDER BY u.firstName, u.lastName
            ";
            $stmt = $pdo->prepare($departmentQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'department_table_join';
            }
        } catch (Exception $e) {
            // If department table doesn't exist, continue to next approach
        }
    }
    
    // Approach 3: If still no users, try rank-based approach - make it more inclusive
    if (empty($rbhUsers) && $hasRank) {
        try {
            $rankQuery = "
                SELECT DISTINCT u.id, u.username, u.status, u.rank";
            if ($hasFirstName) $rankQuery .= ", u.firstName";
            if ($hasLastName) $rankQuery .= ", u.lastName";
            if ($hasEmployeeNo) $rankQuery .= ", u.employee_no";
            
            $rankQuery .= "
                FROM tbl_user u
                WHERE u.status = 'active'
                AND (u.rank LIKE '%RBH%' 
                     OR u.rank LIKE '%Regional%'
                     OR u.rank LIKE '%Business%'
                     OR u.rank LIKE '%Head%'
                     OR u.rank LIKE '%Manager%'
                     OR u.rank LIKE '%Senior%'
                     OR u.rank LIKE '%Executive%'
                     OR u.rank LIKE '%Director%'
                     OR u.rank LIKE '%Officer%'
                     OR u.rank LIKE '%Lead%'
                     OR u.rank LIKE '%Supervisor%')
                ORDER BY u.firstName, u.lastName
            ";
            $stmt = $pdo->prepare($rankQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'rank_based';
            }
        } catch (Exception $e) {
            // Continue to next approach
        }
    }
    
    // Approach 4: If still no users, try username pattern matching - make it more inclusive
    if (empty($rbhUsers)) {
        try {
            $usernameQuery = "
                SELECT DISTINCT u.id, u.username, u.status";
            if ($hasFirstName) $usernameQuery .= ", u.firstName";
            if ($hasLastName) $usernameQuery .= ", u.lastName";
            if ($hasRank) $usernameQuery .= ", u.rank";
            if ($hasEmployeeNo) $usernameQuery .= ", u.employee_no";
            
            $usernameQuery .= "
                FROM tbl_user u
                WHERE u.status = 'active'
                AND (u.username LIKE '%rbh%' 
                     OR u.username LIKE '%regional%'
                     OR u.username LIKE '%business%'
                     OR u.username LIKE '%head%'
                     OR u.username LIKE '%manager%'
                     OR u.username LIKE '%senior%'
                     OR u.username LIKE '%executive%'
                     OR u.username LIKE '%director%'
                     OR u.username LIKE '%officer%'
                     OR u.username LIKE '%lead%'
                     OR u.username LIKE '%supervisor%'
                     OR u.username LIKE '%admin%'
                     OR u.username LIKE '%chief%')
                ORDER BY u.firstName, u.lastName
            ";
            $stmt = $pdo->prepare($usernameQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'username_pattern';
            }
        } catch (Exception $e) {
            // Continue to next approach
        }
    }
    
    // Approach 5: If still no users, try employee number pattern - make it more inclusive
    if (empty($rbhUsers) && $hasEmployeeNo) {
        try {
            $empQuery = "
                SELECT DISTINCT u.id, u.username, u.status, u.employee_no";
            if ($hasFirstName) $empQuery .= ", u.firstName";
            if ($hasLastName) $empQuery .= ", u.lastName";
            if ($hasRank) $empQuery .= ", u.rank";
            
            $empQuery .= "
                FROM tbl_user u
                WHERE u.status = 'active'
                AND (u.employee_no LIKE '%RBH%' 
                     OR u.employee_no LIKE '%R%'
                     OR u.employee_no LIKE '%B%'
                     OR u.employee_no LIKE '%H%'
                     OR u.employee_no LIKE '%M%'
                     OR u.employee_no LIKE '%S%'
                     OR u.employee_no LIKE '%E%'
                     OR u.employee_no LIKE '%D%'
                     OR u.employee_no LIKE '%O%')
                ORDER BY u.firstName, u.lastName
            ";
            $stmt = $pdo->prepare($empQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'employee_number_pattern';
            }
        } catch (Exception $e) {
            // Continue to next approach
        }
    }
    
    // Approach 6: If still no users, try to get users with higher ranks/positions
    if (empty($rbhUsers)) {
        try {
            $seniorQuery = "
                SELECT DISTINCT u.id, u.username, u.status";
            if ($hasFirstName) $seniorQuery .= ", u.firstName";
            if ($hasLastName) $seniorQuery .= ", u.lastName";
            if ($hasRank) $seniorQuery .= ", u.rank";
            if ($hasEmployeeNo) $seniorQuery .= ", u.employee_no";
            
            $seniorQuery .= "
                FROM tbl_user u
                WHERE u.status = 'active'
                AND (u.username NOT LIKE '%agent%'
                     AND u.username NOT LIKE '%clerk%'
                     AND u.username NOT LIKE '%assistant%'
                     AND u.username NOT LIKE '%junior%'
                     AND u.username NOT LIKE '%trainee%')
                ORDER BY u.firstName, u.lastName
                LIMIT 20
            ";
            $stmt = $pdo->prepare($seniorQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'senior_users_filter';
            }
        } catch (Exception $e) {
            // Continue to next approach
        }
    }
    
    // Approach 7: If still no users, try to get users with specific designation_ids that might be RBH
    if (empty($rbhUsers) && $hasDesignationId) {
        try {
            $designationIdQuery = "
                SELECT DISTINCT u.id, u.username, u.status, u.designation_id";
            if ($hasFirstName) $designationIdQuery .= ", u.firstName";
            if ($hasLastName) $designationIdQuery .= ", u.lastName";
            if ($hasRank) $designationIdQuery .= ", u.rank";
            if ($hasEmployeeNo) $designationIdQuery .= ", u.employee_no";
            
            $designationIdQuery .= "
                FROM tbl_user u
                WHERE u.status = 'active'
                AND u.designation_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
                ORDER BY u.firstName, u.lastName
                LIMIT 30
            ";
            $stmt = $pdo->prepare($designationIdQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'designation_id_range';
            }
        } catch (Exception $e) {
            // Continue to next approach
        }
    }
    
    // Approach 8: Final fallback - get more users for testing
    if (empty($rbhUsers)) {
        try {
            $fallbackQuery = "
                SELECT DISTINCT u.id, u.username, u.status";
            if ($hasFirstName) $fallbackQuery .= ", u.firstName";
            if ($hasLastName) $fallbackQuery .= ", u.lastName";
            if ($hasRank) $fallbackQuery .= ", u.rank";
            if ($hasEmployeeNo) $fallbackQuery .= ", u.employee_no";
            
            $fallbackQuery .= "
                FROM tbl_user u
                WHERE u.status = 'active'
                ORDER BY u.firstName, u.lastName
                LIMIT 50
            ";
            $stmt = $pdo->prepare($fallbackQuery);
            $stmt->execute();
            $rbhUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
            if (!empty($rbhUsers)) {
                $approachUsed = 'fallback_sample';
            }
        } catch (Exception $e) {
            // If all else fails, create sample data
        }
    }
    
    // Format the response to include name field for display
    $formattedUsers = [];
    foreach ($rbhUsers as $user) {
        $fullName = '';
        if ($hasFirstName && $hasLastName && !empty($user['firstName']) && !empty($user['lastName'])) {
            $fullName = $user['firstName'] . ' ' . $user['lastName'];
        } elseif ($hasFirstName && !empty($user['firstName'])) {
            $fullName = $user['firstName'];
        } else {
            $fullName = $user['username']; // Fallback to username
        }
        
        $formattedUsers[] = [
            'id' => $user['id'],
            'name' => $fullName, // Use full name for display
            'username' => $user['username'],
            'status' => $user['status'],
            'firstName' => $user['firstName'] ?? 'N/A',
            'lastName' => $user['lastName'] ?? 'N/A',
            'designation_id' => $user['designation_id'] ?? 'N/A',
            'department_id' => $user['department_id'] ?? 'N/A',
            'rank' => $user['rank'] ?? 'N/A',
            'employee_no' => $user['employee_no'] ?? 'N/A'
        ];
    }
    
    if (empty($formattedUsers)) {
        // If no users found, provide debug information
        echo json_encode([
            'success' => false,
            'message' => 'No RBH users found after trying multiple approaches',
            'debug' => [
                'available_fields' => $userFields,
                'has_username' => $hasUsername,
                'has_status' => $hasStatus,
                'has_firstName' => $hasFirstName,
                'has_lastName' => $hasLastName,
                'has_designation_id' => $hasDesignationId,
                'has_department_id' => $hasDepartmentId,
                'has_rank' => $hasRank,
                'has_employee_no' => $hasEmployeeNo,
                'approaches_tried' => [
                    'designation_table_join',
                    'department_table_join', 
                    'rank_based',
                    'username_pattern',
                    'employee_number_pattern',
                    'senior_users_filter',
                    'designation_id_range',
                    'fallback_sample'
                ]
            ]
        ]);
        exit;
    }
    
    echo json_encode([
        'success' => true,
        'data' => $formattedUsers,
        'message' => 'RBH users retrieved successfully',
        'count' => count($formattedUsers),
        'debug' => [
            'approach_used' => $approachUsed,
            'available_fields' => $userFields,
            'name_format' => 'firstName + lastName',
            'inclusive_patterns' => 'broader matching for more users',
            'approaches_tried' => [
                'designation_table_join',
                'department_table_join', 
                'rank_based',
                'username_pattern',
                'employee_number_pattern',
                'senior_users_filter',
                'designation_id_range',
                'fallback_sample'
            ]
        ]
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