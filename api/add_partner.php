<?php
require_once 'db_config.php';
header('Content-Type: application/json');

try {
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) throw new Exception('Invalid JSON');

    // Collect only necessary fields from the form
    $fields = [
        'email_id', 'password', 'first_name', 'last_name', 'alias_name',
        'Phone_number', 'alternative_mobile_number', 'partner_type_id',
        'branch_state_name_id', 'branch_location_id', 'office_address',
        'residential_address', 'aadhaar_number', 'pan_number', 'account_number',
        'ifsc_code', 'bank_id', 'account_type_id', 'pan_img', 'aadhaar_img',
        'photo_img', 'bankproof_img'
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
        echo json_encode(['status' => 'success', 'message' => 'Partner added successfully']);
    } else {
        throw new Exception('Insert failed: ' . $stmt->error);
    }
    $stmt->close();
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?> 