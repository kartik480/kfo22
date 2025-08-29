<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
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
    error_log("Inactive Employees API - Attempting database connection");
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    error_log("Inactive Employees API - Database connection successful");
    
    // Query to get inactive users (status = 0) with EXACTLY the columns you provided
    $query = "
        SELECT 
            id,
            username,
            firstName,
            lastName,
            mobile,
            email_id,
            password,
            dob,
            employee_no,
            father_name,
            joining_date,
            department_id,
            designation_id,
            branch_state_name_id,
            branch_location_id,
            present_address,
            permanent_address,
            status,
            rank,
            avatar,
            height,
            weight,
            passport_no,
            passport_valid,
            languages,
            hobbies,
            blood_group,
            emergency_no,
            emergency_address,
            reference_name,
            reference_relation,
            reference_mobile,
            reference_address,
            reference_name2,
            reference_relation2,
            reference_mobile2,
            reference_address2,
            acc_holder_name,
            bank_name,
            branch_name,
            account_number,
            ifsc_code,
            school_marksCard,
            intermediate_marksCard,
            degree_certificate,
            pg_certificate,
            experience_letter,
            relieving_letter,
            bank_passbook,
            passport_document,
            aadhar_document,
            pancard_document,
            resume_document,
            joiningKit_document,
            reportingTo,
            official_phone,
            official_email,
            work_state,
            work_location,
            alias_name,
            residential_address,
            office_address,
            pan_number,
            aadhaar_number,
            alternative_mobile_number,
            company_name,
            manage_icons,
            data_icons,
            work_icons,
            payout_icons,
            last_working_date,
            leaving_reason,
            re_joining_date,
            createdBy,
            created_at,
            updated_at
        FROM tbl_user 
        WHERE status = '0' OR status = 0
        ORDER BY firstName, lastName
    ";
    
    error_log("Inactive Employees API - Executing query on tbl_user");
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    
    $employees = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $count = count($employees);
    error_log("Inactive Employees API - Query executed successfully. Found $count inactive users");
    
    // Prepare response
    $response = [
        'success' => true,
        'message' => 'Inactive employees retrieved successfully',
        'count' => $count,
        'employees' => $employees
    ];
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Database error
    error_log("Inactive Employees API - Database Error: " . $e->getMessage());
    $response = [
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'count' => 0,
        'employees' => []
    ];
    
    http_response_code(500);
    echo json_encode($response);
    
} catch (Exception $e) {
    // General error
    error_log("Inactive Employees API - General Error: " . $e->getMessage());
    $response = [
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'count' => 0,
        'employees' => []
    ];
    
    http_response_code(500);
    echo json_encode($response);
}
?>