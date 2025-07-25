<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$response = ['success' => false, 'error' => null];

try {
    $pdo = getConnection();

    // Collect POST data (add all required fields from the form)
    $username = $_POST['username'] ?? '';
    $firstName = $_POST['firstName'] ?? '';
    $lastName = $_POST['lastName'] ?? '';
    $mobile = $_POST['mobile'] ?? '';
    $email_id = $_POST['email_id'] ?? '';
    $password = $_POST['password'] ?? '';
    $employee_no = $_POST['employee_no'] ?? '';
    $alias_name = $_POST['alias_name'] ?? '';
    $residential_address = $_POST['residential_address'] ?? '';
    $office_address = $_POST['office_address'] ?? '';
    $pan_number = $_POST['pan_number'] ?? '';
    $aadhaar_number = $_POST['aadhaar_number'] ?? '';
    $alternative_mobile_number = $_POST['alternative_mobile_number'] ?? '';
    $company_name = $_POST['company_name'] ?? '';
    $account_number = $_POST['account_number'] ?? '';
    $bank_name = $_POST['bank_name'] ?? '';
    $ifsc_code = $_POST['ifsc_code'] ?? '';
    $account_type = $_POST['account_type'] ?? '';
    $branch_state_name_id = $_POST['branch_state_name_id'] ?? '';
    $branch_location_id = $_POST['branch_location_id'] ?? '';
    $reportingTo = $_POST['reportingTo'] ?? '';
    $createdBy = $_POST['createdBy'] ?? '';

    // Prepare SQL (add all columns you want to insert)
    $sql = "INSERT INTO tbl_user 
        (username, firstName, lastName, mobile, email_id, password, employee_no, alias_name, residential_address, office_address, pan_number, aadhaar_number, alternative_mobile_number, company_name, account_number, bank_name, ifsc_code, account_type, branch_state_name_id, branch_location_id, reportingTo, createdBy, created_at) 
        VALUES 
        (:username, :firstName, :lastName, :mobile, :email_id, :password, :employee_no, :alias_name, :residential_address, :office_address, :pan_number, :aadhaar_number, :alternative_mobile_number, :company_name, :account_number, :bank_name, :ifsc_code, :account_type, :branch_state_name_id, :branch_location_id, :reportingTo, :createdBy, NOW())";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':username' => $username,
        ':firstName' => $firstName,
        ':lastName' => $lastName,
        ':mobile' => $mobile,
        ':email_id' => $email_id,
        ':password' => $password,
        ':employee_no' => $employee_no,
        ':alias_name' => $alias_name,
        ':residential_address' => $residential_address,
        ':office_address' => $office_address,
        ':pan_number' => $pan_number,
        ':aadhaar_number' => $aadhaar_number,
        ':alternative_mobile_number' => $alternative_mobile_number,
        ':company_name' => $company_name,
        ':account_number' => $account_number,
        ':bank_name' => $bank_name,
        ':ifsc_code' => $ifsc_code,
        ':account_type' => $account_type,
        ':branch_state_name_id' => $branch_state_name_id,
        ':branch_location_id' => $branch_location_id,
        ':reportingTo' => $reportingTo,
        ':createdBy' => $createdBy
    ]);

    $response['success'] = true;
} catch (Exception $e) {
    $response['error'] = $e->getMessage();
}

echo json_encode($response); 