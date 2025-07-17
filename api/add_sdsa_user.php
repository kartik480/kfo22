<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        throw new Exception("Invalid or missing JSON data");
    }

    // List of all columns to insert
    $columns = [
        'username', 'alias_name', 'first_name', 'last_name', 'password', 'Phone_number', 'email_id',
        'alternative_mobile_number', 'company_name', 'branch_state_name_id', 'branch_location_id',
        'bank_id', 'account_type_id', 'office_address', 'residential_address', 'aadhaar_number',
        'pan_number', 'account_number', 'ifsc_code', 'rank', 'status', 'reportingTo', 'employee_no',
        'department', 'designation', 'branchstate', 'branchloaction', 'bank_name', 'account_type',
        'pan_img', 'aadhaar_img', 'photo_img', 'bankproof_img', 'user_id', 'createdBy', 'created_at', 'updated_at'
    ];

    $fields = [];
    $placeholders = [];
    $values = [];
    foreach ($columns as $col) {
        $fields[] = $col;
        $placeholders[] = '?';
        $values[] = isset($input[$col]) ? $input[$col] : null;
    }

    $sql = "INSERT INTO tbl_sdsa_users (" . implode(",", $fields) . ") VALUES (" . implode(",", $placeholders) . ")";
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }
    $types = str_repeat('s', count($values));
    $stmt->bind_param($types, ...$values);
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: " . $stmt->error);
    }
    $insertId = $stmt->insert_id;
    $stmt->close();
    echo json_encode([
        'success' => true,
        'message' => 'SDSA user added successfully',
        'insert_id' => $insertId
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => $e->getMessage()
    ]);
}
if (isset($conn) && $conn) {
    $conn->close();
} 