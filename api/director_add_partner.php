<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn || !$conn->ping()) {
        throw new Exception("Database connection not available");
    }

    // Collect POST data
    $fields = [
        'username', 'alias_name', 'first_name', 'last_name', 'password', 'Phone_number', 'email_id',
        'alternative_mobile_number', 'company_name', 'branch_state_name_id', 'branch_location_id',
        'bank_id', 'account_type_id', 'office_address', 'residential_address', 'aadhaar_number',
        'pan_number', 'account_number', 'ifsc_code', 'rank', 'status', 'reportingTo', 'employee_no',
        'department', 'designation', 'branchstate', 'branchloaction', 'bank_name', 'account_type',
        'partner_type_id', 'user_id', 'created_at', 'createdBy', 'updated_at'
    ];
    $data = [];
    foreach ($fields as $field) {
        $data[$field] = isset($_POST[$field]) ? $_POST[$field] : '';
    }

    // Handle file uploads
    $fileFields = ['pan_img', 'aadhaar_img', 'photo_img', 'bankproof_img'];
    foreach ($fileFields as $fileField) {
        if (isset($_FILES[$fileField]) && $_FILES[$fileField]['error'] == UPLOAD_ERR_OK) {
            $targetDir = 'uploads/partner_docs/';
            if (!is_dir($targetDir)) mkdir($targetDir, 0777, true);
            $filename = uniqid() . '-' . basename($_FILES[$fileField]['name']);
            $targetFile = $targetDir . $filename;
            if (move_uploaded_file($_FILES[$fileField]['tmp_name'], $targetFile)) {
                $data[$fileField] = $filename;
            } else {
                $data[$fileField] = '';
            }
        } else {
            $data[$fileField] = '';
        }
    }

    // Prepare SQL
    $columns = array_keys($data);
    $placeholders = array_map(function($col) { return '?'; }, $columns);
    $sql = "INSERT INTO tbl_partner_users (" . implode(',', $columns) . ") VALUES (" . implode(',', $placeholders) . ")";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param(str_repeat('s', count($columns)), ...array_values($data));
    $success = $stmt->execute();

    if ($success) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Partner added successfully',
            'partner_id' => $conn->insert_id
        ]);
    } else {
        echo json_encode([
            'status' => 'error',
            'message' => 'Failed to add partner: ' . $stmt->error
        ]);
    }
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 