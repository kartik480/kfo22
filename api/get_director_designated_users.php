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
    
    // Query to get users with specific designations (Chief Business Officer and Regional Business Head)
    // First, let's check what designations actually exist
    $checkSql = "SELECT id, designation_name FROM tbl_designation WHERE designation_name LIKE '%Chief Business Officer%' OR designation_name LIKE '%Regional Business Head%' OR designation_name LIKE '%CBO%' OR designation_name LIKE '%RBH%'";
    $checkResult = $conn->query($checkSql);
    
    $availableDesignations = array();
    if ($checkResult && $checkResult->num_rows > 0) {
        while($row = $checkResult->fetch_assoc()) {
            $availableDesignations[] = $row['designation_name'];
        }
    }
    
    // If no exact matches found, let's get all designations to see what's available
    if (empty($availableDesignations)) {
        $allDesignationsSql = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name ASC";
        $allDesignationsResult = $conn->query($allDesignationsSql);
        $allDesignations = array();
        if ($allDesignationsResult && $allDesignationsResult->num_rows > 0) {
            while($row = $allDesignationsResult->fetch_assoc()) {
                $allDesignations[] = $row['designation_name'];
            }
        }
        
        // For now, let's use a broader search to find similar designations
        $sql = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.designation_id,
                    d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE (d.designation_name LIKE '%Chief%' OR d.designation_name LIKE '%Business%' OR d.designation_name LIKE '%Officer%' OR d.designation_name LIKE '%Regional%' OR d.designation_name LIKE '%Head%' OR d.designation_name LIKE '%CBO%' OR d.designation_name LIKE '%RBH%')
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND u.firstName IS NOT NULL 
                AND u.firstName != ''
                ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    } else {
        // Use the exact designations found
        $designationList = "'" . implode("','", $availableDesignations) . "'";
        $sql = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.designation_id,
                    d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE d.designation_name IN ($designationList)
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND u.firstName IS NOT NULL 
                AND u.firstName != ''
                ORDER BY d.designation_name ASC, u.firstName ASC, u.lastName ASC";
    }
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $designatedUsers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
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
        'message' => 'Designated users fetched successfully',
        'count' => count($designatedUsers),
        'debug' => array(
            'available_designations' => $availableDesignations,
            'all_designations' => isset($allDesignations) ? $allDesignations : array(),
            'sql_used' => $sql
        )
    ));
    
} catch (Exception $e) {
    error_log("Error in get_director_designated_users.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch designated users: ' . $e->getMessage()
    ));
}
?> 