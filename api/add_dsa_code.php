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

    // Extract data from input
    $vendorBank = $input['vendor_bank'] ?? '';
    $dsaCode = $input['dsa_code'] ?? '';
    $bsaName = $input['bsa_name'] ?? '';
    $loanType = $input['loan_type'] ?? '';
    $state = $input['state'] ?? '';
    $location = $input['location'] ?? '';
    $status = 'Active'; // Default status

    // Validate required fields
    if (empty($vendorBank) || empty($dsaCode) || empty($bsaName) || empty($loanType) || empty($state) || empty($location)) {
        throw new Exception("All fields are required");
    }

    // Check if DSA code already exists
    $stmt = $conn->prepare("SELECT id FROM tbl_dsa_code WHERE dsa_code = ?");
    $stmt->bind_param("s", $dsaCode);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows > 0) {
        throw new Exception("DSA code already exists: " . $dsaCode);
    }
    $stmt->close();

    // Insert new DSA code into tbl_dsa_code
    $stmt = $conn->prepare("INSERT INTO tbl_dsa_code (vendor_bank, dsa_code, bsa_name, loan_type, state, location, status) VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sssssss", $vendorBank, $dsaCode, $bsaName, $loanType, $state, $location, $status);
    
    if (!$stmt->execute()) {
        throw new Exception("Failed to insert DSA code: " . $stmt->error);
    }
    
    $insertId = $stmt->insert_id;
    $stmt->close();

    echo json_encode([
        'success' => true,
        'message' => 'DSA code added successfully',
        'insert_id' => $insertId,
        'data' => [
            'vendor_bank' => $vendorBank,
            'dsa_code' => $dsaCode,
            'bsa_name' => $bsaName,
            'loan_type' => $loanType,
            'state' => $state,
            'location' => $location,
            'status' => $status
        ]
    ]);

} catch (Exception $e) {
    error_log("Error in add_dsa_code.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => $e->getMessage()
    ]);
}

if (isset($conn) && $conn) {
    $conn->close();
}
?> 