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

    $fields = [];
    $placeholders = [];
    $values = [];

    foreach ($columns as $col) {
        $fields[] = $col;
        $placeholders[] = '?';
        $values[] = isset($input[$col]) ? $input[$col] : null;
    }

    $sql = "INSERT INTO tbl_user (" . implode(',', $fields) . ") VALUES (" . implode(',', $placeholders) . ")";
    $stmt = $conn->prepare($sql);
    if (!$stmt) throw new Exception('Prepare failed: ' . $conn->error);
    $types = str_repeat('s', count($values));
    $stmt->bind_param($types, ...$values);

    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Employee added successfully', 'user_id' => $conn->insert_id]);
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