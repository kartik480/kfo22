<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

require_once 'db_config.php';

try {
    global $conn;
    if ($conn->connect_error) throw new Exception('Connection failed: ' . $conn->connect_error);

    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) throw new Exception('Invalid JSON input');

    // Map Android field names to database field names
    $field_mapping = [
        'firstName' => 'firstName',
        'lastName' => 'lastName',
        'employeeId' => 'employee_no',
        'password' => 'password',
        'personalPhone' => 'mobile',
        'personalEmail' => 'email_id',
        'officialPhone' => 'official_phone',
        'officialEmail' => 'official_email',
        'branchState' => 'work_state',
        'branchLocation' => 'work_location',
        'department' => 'department_id',
        'designation' => 'designation_id',
        'aadhaarNumber' => 'aadhaar_number',
        'panNumber' => 'pan_number',
        'accountNumber' => 'account_number',
        'ifscCode' => 'ifsc_code',
        'bankName' => 'bank_name',
        'accountType' => 'acc_holder_name',
        'presentAddress' => 'present_address',
        'permanentAddress' => 'permanent_address',
        'reportingTo' => 'reportingTo'
    ];

    // Define the database columns and their default values
    $columns = [
        "username" => null,
        "firstName" => null,
        "lastName" => null,
        "mobile" => null,
        "email_id" => null,
        "password" => null,
        "dob" => null,
        "employee_no" => null,
        "father_name" => null,
        "joining_date" => null,
        "department_id" => null,
        "designation_id" => null,
        "branch_state_name_id" => null,
        "branch_location_id" => null,
        "present_address" => null,
        "permanent_address" => null,
        "status" => 'Active',
        "rank" => null,
        "avatar" => null,
        "height" => null,
        "weight" => null,
        "passport_no" => null,
        "passport_valid" => null,
        "languages" => null,
        "hobbies" => null,
        "blood_group" => null,
        "emergency_no" => null,
        "emergency_address" => null,
        "reference_name" => null,
        "reference_relation" => null,
        "reference_mobile" => null,
        "reference_address" => null,
        "reference_name2" => null,
        "reference_relation2" => null,
        "reference_mobile2" => null,
        "reference_address2" => null,
        "acc_holder_name" => null,
        "bank_name" => null,
        "branch_name" => null,
        "account_number" => null,
        "ifsc_code" => null,
        "school_marksCard" => null,
        "intermediate_marksCard" => null,
        "degree_certificate" => null,
        "pg_certificate" => null,
        "experience_letter" => null,
        "relieving_letter" => null,
        "bank_passbook" => null,
        "passport_document" => null,
        "aadhar_document" => null,
        "pancard_document" => null,
        "resume_document" => null,
        "joiningKit_document" => null,
        "reportingTo" => null,
        "official_phone" => null,
        "official_email" => null,
        "work_state" => null,
        "work_location" => null,
        "alias_name" => null,
        "residential_address" => null,
        "office_address" => null,
        "pan_number" => null,
        "aadhaar_number" => null,
        "alternative_mobile_number" => null,
        "company_name" => null,
        "manage_icons" => null,
        "data_icons" => null,
        "work_icons" => null
    ];

    // Map input data to database columns
    foreach ($field_mapping as $android_field => $db_field) {
        if (isset($input[$android_field]) && !empty($input[$android_field])) {
            $columns[$db_field] = $input[$android_field];
        }
    }

    // Set username to employee ID if not provided
    if (empty($columns['username']) && !empty($columns['employee_no'])) {
        $columns['username'] = $columns['employee_no'];
    }

    // Build SQL query
    $fields = array_keys($columns);
    $placeholders = array_fill(0, count($fields), '?');
    $values = array_values($columns);

    $sql = "INSERT INTO tbl_user (" . implode(',', $fields) . ") VALUES (" . implode(',', $placeholders) . ")";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        throw new Exception('Prepare failed: ' . $conn->error);
    }
    
    $types = str_repeat('s', count($values));
    $stmt->bind_param($types, ...$values);

    if ($stmt->execute()) {
        echo json_encode([
            'status' => 'success', 
            'message' => 'Employee added successfully', 
            'user_id' => $conn->insert_id
        ]);
    } else {
        throw new Exception('Failed to insert employee: ' . $stmt->error);
    }

    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?> 