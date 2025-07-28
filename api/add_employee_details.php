<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Database configuration - Production Server
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
        throw new Exception("No input data received");
    }
    
    // Extract form data
    $firstName = $input['firstName'] ?? '';
    $lastName = $input['lastName'] ?? '';
    $personalPhone = $input['personalPhone'] ?? '';
    $officialPhone = $input['officialPhone'] ?? '';
    $dateOfBirth = $input['dateOfBirth'] ?? '';
    $branchStateName = $input['branchState'] ?? '';
    $aadhaarNumber = $input['aadhaarNumber'] ?? '';
    $presentAddress = $input['presentAddress'] ?? '';
    $personalEmail = $input['personalEmail'] ?? '';
    $officialEmail = $input['officialEmail'] ?? '';
    $anniversaryDate = $input['anniversaryDate'] ?? '';
    $branchLocationName = $input['branchLocation'] ?? '';
    $panNumber = $input['panNumber'] ?? '';
    $permanentAddress = $input['permanentAddress'] ?? '';
    
    // Validate required fields
    if (empty($firstName) || empty($lastName) || empty($personalPhone) || empty($personalEmail)) {
        throw new Exception("Required fields are missing");
    }
    
    // Get branch state ID
    $branchStateId = null;
    if (!empty($branchStateName)) {
        $stmt = $pdo->prepare("SELECT id FROM tbl_branch_state WHERE branch_state_name = ?");
        $stmt->execute([$branchStateName]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $branchStateId = $result ? $result['id'] : null;
    }
    
    // Get branch location ID
    $branchLocationId = null;
    if (!empty($branchLocationName)) {
        $stmt = $pdo->prepare("SELECT id FROM tbl_branch_location WHERE branch_location = ?");
        $stmt->execute([$branchLocationName]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $branchLocationId = $result ? $result['id'] : null;
    }
    
    // Generate username (first letter of first name + last name + random number)
    $username = strtolower(substr($firstName, 0, 1) . $lastName . rand(100, 999));
    
    // Generate default password (first letter of first name + last name + @123)
    $password = strtolower(substr($firstName, 0, 1) . $lastName . '@123');
    
    // Generate employee number (EMP + random 6 digits)
    $employeeNo = 'EMP' . str_pad(rand(1, 999999), 6, '0', STR_PAD_LEFT);
    
    // Convert date formats
    $dobFormatted = null;
    if (!empty($dateOfBirth)) {
        $dobParts = explode('/', $dateOfBirth);
        if (count($dobParts) === 3) {
            $dobFormatted = $dobParts[2] . '-' . $dobParts[1] . '-' . $dobParts[0];
        }
    }
    
    $joiningDateFormatted = null;
    if (!empty($anniversaryDate)) {
        $joiningParts = explode('/', $anniversaryDate);
        if (count($joiningParts) === 3) {
            $joiningDateFormatted = $joiningParts[2] . '-' . $joiningParts[1] . '-' . $joiningParts[0];
        }
    }
    
    // Insert into tbl_user
    $sql = "INSERT INTO tbl_user (
        username, firstName, lastName, mobile, email_id, password, dob, 
        employee_no, branch_state_name_id, branch_location_id, present_address, 
        permanent_address, official_phone, official_email, joining_date, 
        pan_number, aadhaar_number, status, created_at, updated_at
    ) VALUES (
        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'active', NOW(), NOW()
    )";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        $username,
        $firstName,
        $lastName,
        $personalPhone,
        $personalEmail,
        $password,
        $dobFormatted,
        $employeeNo,
        $branchStateId,
        $branchLocationId,
        $presentAddress,
        $permanentAddress,
        $officialPhone,
        $officialEmail,
        $joiningDateFormatted,
        $panNumber,
        $aadhaarNumber
    ]);
    
    $userId = $pdo->lastInsertId();
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Employee details saved successfully',
        'data' => [
            'user_id' => $userId,
            'username' => $username,
            'employee_no' => $employeeNo,
            'password' => $password
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?> 