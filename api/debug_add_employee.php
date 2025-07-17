<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') exit(0);

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Log the raw input
$raw_input = file_get_contents('php://input');
error_log("Raw input received: " . $raw_input);

try {
    require_once 'db_config.php';
    
    global $conn;
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }

    $input = json_decode($raw_input, true);
    if (!$input) {
        throw new Exception('Invalid JSON input: ' . json_last_error_msg());
    }

    // Log the decoded input
    error_log("Decoded input: " . print_r($input, true));

    // List all columns you want to support
    $columns = [
        "username","firstName","lastName","mobile","email_id","password","dob","employee_no","father_name","joining_date",
        "department_id","designation_id","branch_state_name_id","branch_location_id","present_address","permanent_address",
        "status","rank","avatar","height","weight","passport_no","passport_valid","languages","hobbies","blood_group",
        "emergency_no","emergency_address","reference_name","reference_relation","reference_mobile","reference_address",
        "reference_name2","reference_relation2","reference_mobile2","reference_address2","acc_holder_name","bank_name",
        "branch_name","account_number","ifsc_code","school_marksCard","intermediate_marksCard","degree_certificate",
        "pg_certificate","experience_letter","relieving_letter","bank_passbook","passport_document","aadhar_document",
        "pancard_document","resume_document","joiningKit_document","reportingTo","official_phone","official_email",
        "work_state","work_location","alias_name","residential_address","office_address","pan_number","aadhaar_number",
        "alternative_mobile_number","company_name","manage_icons","data_icons","work_icons"
    ];

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
        'accountType' => 'acc_holder_name', // This might need adjustment
        'presentAddress' => 'present_address',
        'permanentAddress' => 'permanent_address',
        'reportingTo' => 'reportingTo'
    ];

    $fields = [];
    $placeholders = [];
    $values = [];

    foreach ($columns as $col) {
        $fields[] = $col;
        $placeholders[] = '?';
        
        // Check if we have a mapping for this field
        $android_field = array_search($col, $field_mapping);
        if ($android_field !== false && isset($input[$android_field])) {
            $values[] = $input[$android_field];
        } else if (isset($input[$col])) {
            $values[] = $input[$col];
        } else {
            // Set default values for required fields
            if ($col === 'status') {
                $values[] = 'Active';
            } else if ($col === 'username') {
                $values[] = isset($input['employeeId']) ? $input['employeeId'] : '';
            } else {
                $values[] = null;
            }
        }
    }

    // Log the SQL and values
    $sql = "INSERT INTO tbl_user (" . implode(',', $fields) . ") VALUES (" . implode(',', $placeholders) . ")";
    error_log("SQL Query: " . $sql);
    error_log("Values: " . print_r($values, true));

    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception('Prepare failed: ' . $conn->error);
    }
    
    $types = str_repeat('s', count($values));
    $stmt->bind_param($types, ...$values);

    if ($stmt->execute()) {
        $result = ['status' => 'success', 'message' => 'Employee added successfully', 'user_id' => $conn->insert_id];
        error_log("Success: " . json_encode($result));
        echo json_encode($result);
    } else {
        throw new Exception('Failed to insert employee: ' . $stmt->error);
    }

    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Error in debug_add_employee.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?> 