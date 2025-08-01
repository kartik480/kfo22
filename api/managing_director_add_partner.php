<?php
require_once 'db_config.php';
header('Content-Type: application/json');

try {
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) throw new Exception('Invalid JSON');

    // Collect all required fields from the form matching the exact column names for tbl_partner_users
    $fields = [
        'username', 'alias_name', 'first_name', 'last_name', 'password',
        'Phone_number', 'email_id', 'alternative_mobile_number', 'company_name',
        'branch_state_name_id', 'branch_location_id', 'bank_id', 'account_type_id',
        'office_address', 'residential_address', 'aadhaar_number', 'pan_number',
        'account_number', 'ifsc_code', 'rank', 'status', 'reportingTo',
        'employee_no', 'department', 'designation', 'branchstate', 'branchloaction',
        'bank_name', 'account_type', 'partner_type_id', 'pan_img', 'aadhaar_img',
        'photo_img', 'bankproof_img', 'user_id', 'created_at', 'createdBy', 'updated_at'
    ];

    $values = [];
    $placeholders = [];
    $types = '';

    foreach ($fields as $field) {
        $values[] = isset($input[$field]) ? $input[$field] : null;
        $placeholders[] = '?';
        $types .= 's'; // treat all as string for now
    }

    $sql = "INSERT INTO tbl_partner_users (" . implode(',', $fields) . ") VALUES (" . implode(',', $placeholders) . ")";
    $stmt = $conn->prepare($sql);
    if (!$stmt) throw new Exception('Prepare failed: ' . $conn->error);

    $stmt->bind_param($types, ...$values);

    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Partner added successfully by Managing Director']);
    } else {
        throw new Exception('Insert failed: ' . $stmt->error);
    }
    $stmt->close();
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?> 