<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Check if it's a POST request
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Only POST method is allowed');
    }
    
    // Get form data
    $vendorBank = $_POST['vendor_bank'] ?? '';
    $bankerName = $_POST['banker_name'] ?? '';
    $phoneNumber = $_POST['phone_number'] ?? '';
    
    $email = $_POST['email'] ?? '';
    $bankerDesignation = $_POST['banker_designation'] ?? '';
    $loanType = $_POST['loan_type'] ?? '';
    $branchState = $_POST['branch_state'] ?? '';
    $branchLocation = $_POST['branch_location'] ?? '';
    $address = $_POST['address'] ?? '';
    
    // Validate required fields
    if (empty($vendorBank) || empty($bankerName) || empty($phoneNumber) || 
        empty($email) || empty($bankerDesignation) || empty($loanType) || 
        empty($branchState) || empty($branchLocation) || empty($address)) {
        throw new Exception('All required fields must be filled');
    }
    
    // Get IDs from names
    $vendorBankId = getVendorBankId($conn, $vendorBank);
    $designationId = getBankerDesignationId($conn, $bankerDesignation);
    $loanTypeId = getLoanTypeId($conn, $loanType);
    $branchStateId = getBranchStateId($conn, $branchState);
    $branchLocationId = getBranchLocationId($conn, $branchLocation);
    
    // Handle file upload
    $visitingCardPath = '';
    if (isset($_FILES['visiting_card']) && $_FILES['visiting_card']['error'] === UPLOAD_ERR_OK) {
        $uploadDir = 'uploads/bankers/';
        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }
        
        $fileName = time() . '_' . $_FILES['visiting_card']['name'];
        $uploadPath = $uploadDir . $fileName;
        
        if (move_uploaded_file($_FILES['visiting_card']['tmp_name'], $uploadPath)) {
            $visitingCardPath = $uploadPath;
        }
    }
    
    // Insert banker data
    $query = "INSERT INTO bankers (
        vendor_bank_id, banker_name, phone_number, email, 
        banker_designation_id, loan_type_id, branch_state_id, 
        branch_location_id, address, visiting_card, created_at
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("isssiiiss", 
        $vendorBankId, $bankerName, $phoneNumber, $email,
        $designationId, $loanTypeId, $branchStateId,
        $branchLocationId, $address, $visitingCardPath
    );
    
    if ($stmt->execute()) {
        echo json_encode(array(
            'success' => true,
            'message' => 'Banker added successfully',
            'banker_id' => $conn->insert_id
        ));
    } else {
        throw new Exception('Failed to insert banker data: ' . $stmt->error);
    }
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => $e->getMessage()
    ));
}

$conn->close();

// Helper functions to get IDs from names
function getVendorBankId($conn, $vendorBankName) {
    $stmt = $conn->prepare("SELECT id FROM vendor_banks WHERE vendor_bank_name = ?");
    $stmt->bind_param("s", $vendorBankName);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        return $row['id'];
    }
    throw new Exception("Vendor bank '$vendorBankName' not found");
}

function getBankerDesignationId($conn, $designationName) {
    $stmt = $conn->prepare("SELECT id FROM tbl_banker_designation WHERE designation_name = ?");
    $stmt->bind_param("s", $designationName);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        return $row['id'];
    }
    throw new Exception("Banker designation '$designationName' not found");
}

function getLoanTypeId($conn, $loanTypeName) {
    $stmt = $conn->prepare("SELECT id FROM loan_types WHERE loan_type_name = ?");
    $stmt->bind_param("s", $loanTypeName);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        return $row['id'];
    }
    throw new Exception("Loan type '$loanTypeName' not found");
}

function getBranchStateId($conn, $stateName) {
    $stmt = $conn->prepare("SELECT id FROM branch_states WHERE state_name = ?");
    $stmt->bind_param("s", $stateName);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        return $row['id'];
    }
    throw new Exception("Branch state '$stateName' not found");
}

function getBranchLocationId($conn, $locationName) {
    $stmt = $conn->prepare("SELECT id FROM branch_locations WHERE branch_location = ?");
    $stmt->bind_param("s", $locationName);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        return $row['id'];
    }
    throw new Exception("Branch location '$locationName' not found");
}
?> 