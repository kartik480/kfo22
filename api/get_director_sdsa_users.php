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
    
    // Get the director ID from the request (default to 11 if not provided)
    $directorId = isset($_GET['director_id']) ? $_GET['director_id'] : '11';
    
    // Query to get all SDSA users from tbl_sdsa_users who report to the specified director
    $sql = "SELECT 
                id,
                username,
                alias_name,
                first_name,
                last_name,
                password,
                Phone_number,
                email_id,
                alternative_mobile_number,
                company_name,
                branch_state_name_id,
                branch_location_id,
                bank_id,
                account_type_id,
                office_address,
                residential_address,
                aadhaar_number,
                pan_number,
                account_number,
                ifsc_code,
                rank,
                status,
                reportingTo,
                employee_no,
                department,
                designation,
                branchstate,
                branchloaction,
                bank_name,
                account_type,
                pan_img,
                aadhaar_img,
                photo_img,
                bankproof_img,
                user_id,
                createdBy,
                created_at,
                updated_at
            FROM tbl_sdsa_users 
            WHERE reportingTo = ?
            AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
            AND first_name IS NOT NULL 
            AND first_name != ''
            ORDER BY first_name ASC, last_name ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $directorId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Query failed: " . $stmt->error);
    }
    
    $sdsaUsers = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                // Get manage icons for this user
                $manageIcons = array();
                $iconSql = "SELECT mi.icon_name 
                           FROM tbl_manage_icon mi 
                           INNER JOIN tbl_user_icon_permissions uip ON mi.id = uip.icon_id 
                           WHERE uip.user_id = ? AND (mi.status = 1 OR mi.status = 'Active' OR mi.status IS NULL OR mi.status = '')
                           ORDER BY mi.icon_name ASC";
                
                $iconStmt = $conn->prepare($iconSql);
                if ($iconStmt) {
                    $iconStmt->bind_param("s", $row['id']);
                    $iconStmt->execute();
                    $iconResult = $iconStmt->get_result();
                    
                    while ($iconRow = $iconResult->fetch_assoc()) {
                        $manageIcons[] = $iconRow['icon_name'];
                    }
                    $iconStmt->close();
                }
                
                $sdsaUsers[] = array(
                    'id' => $row['id'],
                    'username' => $row['username'],
                    'alias_name' => $row['alias_name'],
                    'first_name' => $row['first_name'],
                    'last_name' => $row['last_name'],
                    'full_name' => $fullName,
                    'password' => $row['password'],
                    'phone_number' => $row['Phone_number'],
                    'email_id' => $row['email_id'],
                    'alternative_mobile_number' => $row['alternative_mobile_number'],
                    'company_name' => $row['company_name'],
                    'branch_state_name_id' => $row['branch_state_name_id'],
                    'branch_location_id' => $row['branch_location_id'],
                    'bank_id' => $row['bank_id'],
                    'account_type_id' => $row['account_type_id'],
                    'office_address' => $row['office_address'],
                    'residential_address' => $row['residential_address'],
                    'aadhaar_number' => $row['aadhaar_number'],
                    'pan_number' => $row['pan_number'],
                    'account_number' => $row['account_number'],
                    'ifsc_code' => $row['ifsc_code'],
                    'rank' => $row['rank'],
                    'status' => $row['status'],
                    'reportingTo' => $row['reportingTo'],
                    'employee_no' => $row['employee_no'],
                    'department' => $row['department'],
                    'designation' => $row['designation'],
                    'branchstate' => $row['branchstate'],
                    'branchloaction' => $row['branchloaction'],
                    'bank_name' => $row['bank_name'],
                    'account_type' => $row['account_type'],
                    'pan_img' => $row['pan_img'],
                    'aadhaar_img' => $row['aadhaar_img'],
                    'photo_img' => $row['photo_img'],
                    'bankproof_img' => $row['bankproof_img'],
                    'user_id' => $row['user_id'],
                    'createdBy' => $row['createdBy'],
                    'created_at' => $row['created_at'],
                    'updated_at' => $row['updated_at'],
                    'manage_icons' => $manageIcons
                );
            }
        }
    }
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $sdsaUsers,
        'message' => 'SDSA users reporting to director fetched successfully',
        'count' => count($sdsaUsers),
        'director_id' => $directorId
    ));
    
} catch (Exception $e) {
    error_log("Error in get_director_sdsa_users.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch SDSA users: ' . $e->getMessage()
    ));
}
?> 