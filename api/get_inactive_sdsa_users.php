<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Include database configuration
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Fetch inactive SDSA users from tbl_sdsa_users where status = 0
    // Using the correct column names from the actual table structure
    $stmt = $conn->prepare("
        SELECT 
            id,
            username,
            alias_name,
            first_name,
            last_name,
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
            user_id,
            createdBy,
            created_at,
            updated_at
        FROM tbl_sdsa_users 
        WHERE status = 0 
        ORDER BY first_name ASC
    ");
    $stmt->execute();
    $inactiveUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countStmt = $conn->prepare("SELECT COUNT(*) as total FROM tbl_sdsa_users WHERE status = 0");
    $countStmt->execute();
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Return in the format expected by the Android app
    echo json_encode(array(
        'success' => true,
        'message' => 'Inactive SDSA users fetched successfully.',
        'data' => $inactiveUsers,
        'total_count' => $totalCount
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    error_log("Get inactive SDSA users error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while fetching inactive SDSA users: ' . $e->getMessage()
    ));
}
?> 