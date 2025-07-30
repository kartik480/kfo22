<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        throw new Exception('No input data received');
    }
    
    // Extract form data
    $employeeId = trim($input['employee_id'] ?? '');
    $firstName = trim($input['first_name'] ?? '');
    $lastName = trim($input['last_name'] ?? '');
    $aliasName = trim($input['alias_name'] ?? '');
    $phone = trim($input['phone'] ?? '');
    $alternativePhone = trim($input['alternative_phone'] ?? '');
    $email = trim($input['email'] ?? '');
    $password = trim($input['password'] ?? '');
    $branchState = trim($input['branch_state'] ?? '');
    $branchLocation = trim($input['branch_location'] ?? '');
    $officeAddress = trim($input['office_address'] ?? '');
    $residentialAddress = trim($input['residential_address'] ?? '');
    $companyName = trim($input['company_name'] ?? '');
    $panNumber = trim($input['pan_number'] ?? '');
    $aadhaarNumber = trim($input['aadhaar_number'] ?? '');
    $accountType = trim($input['account_type'] ?? '');
    $bankName = trim($input['bank_name'] ?? '');
    $accountNumber = trim($input['account_number'] ?? '');
    $ifscCode = trim($input['ifsc_code'] ?? '');
    $reportingTo = trim($input['reporting_to'] ?? '');
    
    // File uploads (for now, store file names)
    $panCardFile = trim($input['pan_card_file'] ?? '');
    $aadhaarFile = trim($input['aadhaar_file'] ?? '');
    $bankProofFile = trim($input['bank_proof_file'] ?? '');
    $photoFile = trim($input['photo_file'] ?? '');
    $resumeFile = trim($input['resume_file'] ?? '');
    
    // Get current user info (from session or request)
    $createdBy = trim($input['created_by'] ?? '1'); // Default to admin user
    
    // Validate required fields
    $requiredFields = [
        'employee_id' => $employeeId,
        'first_name' => $firstName,
        'last_name' => $lastName,
        'phone' => $phone,
        'email' => $email,
        'password' => $password,
        'branch_state' => $branchState,
        'branch_location' => $branchLocation,
        'office_address' => $officeAddress,
        'residential_address' => $residentialAddress,
        'company_name' => $companyName,
        'pan_number' => $panNumber,
        'aadhaar_number' => $aadhaarNumber,
        'account_type' => $accountType,
        'bank_name' => $bankName,
        'account_number' => $accountNumber,
        'ifsc_code' => $ifscCode,
        'reporting_to' => $reportingTo
    ];
    
    $missingFields = [];
    foreach ($requiredFields as $field => $value) {
        if (empty($value)) {
            $missingFields[] = $field;
        }
    }
    
    if (!empty($missingFields)) {
        throw new Exception('Missing required fields: ' . implode(', ', $missingFields));
    }
    
    // Get IDs from dropdown selections
    $branchStateId = null;
    $branchLocationId = null;
    $bankNameId = null;
    $accountTypeId = null;
    $reportingToId = null;
    
    // Get Branch State ID
    if (!empty($branchState)) {
        $stmt = $pdo->prepare("SELECT id FROM tbl_branch_state WHERE branch_state_name = ?");
        $stmt->execute([$branchState]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $branchStateId = $result ? $result['id'] : null;
    }
    
    // Get Branch Location ID
    if (!empty($branchLocation)) {
        $stmt = $pdo->prepare("SELECT id FROM tbl_branch_location WHERE branch_location = ?");
        $stmt->execute([$branchLocation]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $branchLocationId = $result ? $result['id'] : null;
    }
    
    // Get Bank Name ID
    if (!empty($bankName)) {
        $stmt = $pdo->prepare("SELECT id FROM tbl_bank WHERE bank_name = ?");
        $stmt->execute([$bankName]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $bankNameId = $result ? $result['id'] : null;
    }
    
    // Get Account Type ID
    if (!empty($accountType)) {
        $stmt = $pdo->prepare("SELECT id FROM tbl_account_type WHERE account_type = ?");
        $stmt->execute([$accountType]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $accountTypeId = $result ? $result['id'] : null;
    }
    
    // Get Reporting To ID (extract from display name like "John Doe (Business Head)")
    if (!empty($reportingTo)) {
        $reportingName = preg_replace('/\s*\(Business Head\)$/', '', $reportingTo);
        $stmt = $pdo->prepare("SELECT id FROM tbl_user WHERE CONCAT(firstName, ' ', lastName) = ? AND designation_id IN (SELECT id FROM tbl_designation WHERE designation_name = 'Business Head')");
        $stmt->execute([trim($reportingName)]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $reportingToId = $result ? $result['id'] : null;
    }
    
    // Set default values
    $departmentId = 4; // Marketing department (from your data)
    $designationId = 15; // Business Head designation (from your data)
    $status = 1; // Active status
    $currentTimestamp = date('Y-m-d H:i:s');
    
    // Insert into tbl_user
    $stmt = $pdo->prepare("
        INSERT INTO tbl_user (
            username, firstName, lastName, mobile, email_id, password, 
            employee_no, department_id, designation_id, branch_state_name_id, 
            branch_location_id, present_address, permanent_address, status,
            acc_holder_name, bank_name, account_number, ifsc_code,
            pancard_document, aadhar_document, resume_document,
            reportingTo, official_phone, work_state, work_location,
            alias_name, residential_address, office_address, pan_number,
            aadhaar_number, alternative_mobile_number, company_name,
            createdBy, created_at, updated_at
        ) VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        )
    ");
    
    $stmt->execute([
        $employeeId, // username
        $firstName,
        $lastName,
        $phone, // mobile
        $email, // email_id
        $password,
        $employeeId, // employee_no
        $departmentId,
        $designationId,
        $branchStateId, // branch_state_name_id
        $branchLocationId, // branch_location_id
        $officeAddress, // present_address
        $residentialAddress, // permanent_address
        $status,
        $firstName . ' ' . $lastName, // acc_holder_name
        $bankName, // bank_name (store name, not ID for now)
        $accountNumber,
        $ifscCode,
        $panCardFile, // pancard_document
        $aadhaarFile, // aadhar_document
        $resumeFile, // resume_document
        $reportingToId, // reportingTo
        $phone, // official_phone
        $branchState, // work_state
        $branchLocation, // work_location
        $aliasName,
        $residentialAddress,
        $officeAddress,
        $panNumber,
        $aadhaarNumber,
        $alternativePhone, // alternative_mobile_number
        $companyName,
        $createdBy,
        $currentTimestamp, // created_at
        $currentTimestamp  // updated_at
    ]);
    
    $userId = $pdo->lastInsertId();
    
    // Success response
    echo json_encode([
        'status' => 'success',
        'message' => 'Employee added successfully',
        'data' => [
            'user_id' => $userId,
            'employee_id' => $employeeId,
            'name' => $firstName . ' ' . $lastName,
            'email' => $email,
            'phone' => $phone,
            'department' => 'Marketing',
            'designation' => 'Business Head',
            'branch_state' => $branchState,
            'branch_location' => $branchLocation,
            'reporting_to' => $reportingTo,
            'created_at' => $currentTimestamp
        ],
        'timestamp' => time()
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error adding employee: ' . $e->getMessage(),
        'data' => [],
        'timestamp' => time()
    ]);
}
?> 