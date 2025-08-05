<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // Step 1: Find designation IDs for Chief Business Officer and Regional Business Head
    // Using a more comprehensive search to catch all variations
    $designationSql = "SELECT id, designation_name FROM tbl_designation 
                       WHERE LOWER(designation_name) LIKE '%chief%' 
                       AND LOWER(designation_name) LIKE '%business%' 
                       AND LOWER(designation_name) LIKE '%officer%'
                       OR LOWER(designation_name) LIKE '%cbo%'
                       OR (LOWER(designation_name) LIKE '%regional%' 
                           AND LOWER(designation_name) LIKE '%business%' 
                           AND LOWER(designation_name) LIKE '%head%')
                       OR LOWER(designation_name) LIKE '%rbh%'
                       ORDER BY designation_name ASC";
    
    $designationResult = $conn->query($designationSql);
    
    if ($designationResult === false) {
        throw new Exception("Designation query failed: " . $conn->error);
    }
    
    $designationIds = array();
    $foundDesignations = array();
    
    if ($designationResult->num_rows > 0) {
        while($row = $designationResult->fetch_assoc()) {
            $designationIds[] = $row['id'];
            $foundDesignations[] = array(
                'id' => $row['id'],
                'name' => $row['designation_name']
            );
        }
    }
    
    // If no designations found, try a broader search
    if (empty($designationIds)) {
        $broadDesignationSql = "SELECT id, designation_name FROM tbl_designation 
                               WHERE LOWER(designation_name) LIKE '%chief%' 
                               OR LOWER(designation_name) LIKE '%business%' 
                               OR LOWER(designation_name) LIKE '%officer%'
                               OR LOWER(designation_name) LIKE '%regional%' 
                               OR LOWER(designation_name) LIKE '%head%'
                               OR LOWER(designation_name) LIKE '%cbo%'
                               OR LOWER(designation_name) LIKE '%rbh%'
                               ORDER BY designation_name ASC";
        
        $broadResult = $conn->query($broadDesignationSql);
        if ($broadResult && $broadResult->num_rows > 0) {
            while($row = $broadResult->fetch_assoc()) {
                $designationIds[] = $row['id'];
                $foundDesignations[] = array(
                    'id' => $row['id'],
                    'name' => $row['designation_name']
                );
            }
        }
    }
    
    // If still no designations found, return empty result with debug info
    if (empty($designationIds)) {
        // Get all designations for debugging
        $allDesignationsSql = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name ASC";
        $allResult = $conn->query($allDesignationsSql);
        $allDesignations = array();
        if ($allResult && $allResult->num_rows > 0) {
            while($row = $allResult->fetch_assoc()) {
                $allDesignations[] = array(
                    'id' => $row['id'],
                    'name' => $row['designation_name']
                );
            }
        }
        
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No Chief Business Officer or Regional Business Head designations found',
            'count' => 0,
            'debug' => array(
                'found_designations' => $foundDesignations,
                'all_designations' => $allDesignations,
                'designation_ids' => $designationIds,
                'note' => 'Please check if designations exist in tbl_designation table'
            )
        ));
        exit;
    }
    
    // Step 2: Find all users who have these designations
    $designationIdList = implode(',', $designationIds);
    $userSql = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.designation_id,
                    d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE u.designation_id IN ($designationIdList)
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND u.firstName IS NOT NULL 
                AND u.firstName != ''
                AND u.firstName != 'null'
                ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    
    $userResult = $conn->query($userSql);
    
    if ($userResult === false) {
        throw new Exception("User query failed: " . $conn->error);
    }
    
    $designatedUsers = array();
    
    if ($userResult->num_rows > 0) {
        while($row = $userResult->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Only add if full name is not empty and not just whitespace
            if (!empty($fullName) && strlen(trim($fullName)) > 0) {
                $designatedUsers[] = array(
                    'id' => $row['id'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'full_name' => $fullName,
                    'designation_id' => $row['designation_id'],
                    'designation_name' => $row['designation_name']
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $designatedUsers,
        'message' => 'Chief Business Officer and Regional Business Head users fetched successfully',
        'count' => count($designatedUsers),
        'debug' => array(
            'found_designations' => $foundDesignations,
            'designation_ids' => $designationIds,
            'sql_used' => $userSql,
            'note' => 'Filtered for CBO and RBH designations only'
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_director_designated_users_fixed.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch designated users: ' . $e->getMessage(),
        'debug' => array(
            'error_details' => $e->getMessage(),
            'file' => __FILE__,
            'line' => __LINE__
        )
    ));
}
?> 