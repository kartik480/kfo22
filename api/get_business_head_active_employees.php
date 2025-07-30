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
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, find the user ID for "DUBEY SATYA SAIBABA"
    $managerQuery = "SELECT id FROM tbl_user WHERE CONCAT(firstName, ' ', lastName) = 'DUBEY SATYA SAIBABA'";
    $managerStmt = $pdo->prepare($managerQuery);
    $managerStmt->execute();
    $managerResult = $managerStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$managerResult) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Manager "DUBEY SATYA SAIBABA" not found in the system',
            'data' => []
        ]);
        exit();
    }
    
    $managerId = $managerResult['id'];
    
    // Query to fetch all users reporting to DUBEY SATYA SAI
    $query = "SELECT 
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
                last_working_date,
                leaving_reason,
                re_joining_date,
                createdBy,
                created_at,
                updated_at
              FROM tbl_user 
              WHERE reportingTo = ?
              AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '')
              AND firstName IS NOT NULL 
              AND firstName != ''
              ORDER BY firstName ASC, lastName ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute([$managerId]);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the response
    $formattedUsers = [];
    foreach ($users as $user) {
        $formattedUsers[] = [
            'id' => $user['id'],
            'username' => $user['username'],
            'firstName' => $user['firstName'],
            'lastName' => $user['lastName'],
            'fullName' => trim($user['firstName'] . ' ' . $user['lastName']),
            'mobile' => $user['mobile'],
            'email_id' => $user['email_id'],
            'password' => $user['password'],
            'dob' => $user['dob'],
            'employee_no' => $user['employee_no'],
            'father_name' => $user['father_name'],
            'joining_date' => $user['joining_date'],
            'department_id' => $user['department_id'],
            'designation_id' => $user['designation_id'],
            'branch_state_name_id' => $user['branch_state_name_id'],
            'branch_location_id' => $user['branch_location_id'],
            'present_address' => $user['present_address'],
            'permanent_address' => $user['permanent_address'],
            'status' => $user['status'],
            'rank' => $user['rank'],
            'avatar' => $user['avatar'],
            'height' => $user['height'],
            'weight' => $user['weight'],
            'passport_no' => $user['passport_no'],
            'passport_valid' => $user['passport_valid'],
            'languages' => $user['languages'],
            'hobbies' => $user['hobbies'],
            'blood_group' => $user['blood_group'],
            'emergency_no' => $user['emergency_no'],
            'emergency_address' => $user['emergency_address'],
            'reference_name' => $user['reference_name'],
            'reference_relation' => $user['reference_relation'],
            'reference_mobile' => $user['reference_mobile'],
            'reference_address' => $user['reference_address'],
            'reference_name2' => $user['reference_name2'],
            'reference_relation2' => $user['reference_relation2'],
            'reference_mobile2' => $user['reference_mobile2'],
            'reference_address2' => $user['reference_address2'],
            'acc_holder_name' => $user['acc_holder_name'],
            'bank_name' => $user['bank_name'],
            'branch_name' => $user['branch_name'],
            'account_number' => $user['account_number'],
            'ifsc_code' => $user['ifsc_code'],
            'school_marksCard' => $user['school_marksCard'],
            'intermediate_marksCard' => $user['intermediate_marksCard'],
            'degree_certificate' => $user['degree_certificate'],
            'pg_certificate' => $user['pg_certificate'],
            'experience_letter' => $user['experience_letter'],
            'relieving_letter' => $user['relieving_letter'],
            'bank_passbook' => $user['bank_passbook'],
            'passport_document' => $user['passport_document'],
            'aadhar_document' => $user['aadhar_document'],
            'pancard_document' => $user['pancard_document'],
            'resume_document' => $user['resume_document'],
            'joiningKit_document' => $user['joiningKit_document'],
            'reportingTo' => $user['reportingTo'],
            'official_phone' => $user['official_phone'],
            'official_email' => $user['official_email'],
            'work_state' => $user['work_state'],
            'work_location' => $user['work_location'],
            'alias_name' => $user['alias_name'],
            'residential_address' => $user['residential_address'],
            'office_address' => $user['office_address'],
            'pan_number' => $user['pan_number'],
            'aadhaar_number' => $user['aadhaar_number'],
            'alternative_mobile_number' => $user['alternative_mobile_number'],
            'company_name' => $user['company_name'],
            'manage_icons' => $user['manage_icons'],
            'data_icons' => $user['data_icons'],
            'work_icons' => $user['work_icons'],
            'last_working_date' => $user['last_working_date'],
            'leaving_reason' => $user['leaving_reason'],
            're_joining_date' => $user['re_joining_date'],
            'createdBy' => $user['createdBy'],
            'created_at' => $user['created_at'],
            'updated_at' => $user['updated_at']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Active employees reporting to DUBEY SATYA SAIBABA fetched successfully',
        'data' => $formattedUsers,
        'total_users' => count($formattedUsers),
        'manager_id' => $managerId
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 