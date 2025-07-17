<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    global $conn;
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    if (!$data) {
        throw new Exception('Invalid JSON data received');
    }
    
    // Validate required fields
    $requiredFields = ['phone_number', 'full_name', 'company_name', 'email', 'partner_type', 'branch_state', 'branch_location', 'address'];
    foreach ($requiredFields as $field) {
        if (empty($data[$field])) {
            throw new Exception("Required field '$field' is missing or empty");
        }
    }
    
    // Extract data
    $phoneNumber = $data['phone_number'];
    $fullName = $data['full_name'];
    $companyName = $data['company_name'];
    $alternativeNumber = $data['alternative_number'] ?? '';
    $email = $data['email'];
    $partnerType = $data['partner_type'];
    $branchState = $data['branch_state'];
    $branchLocation = $data['branch_location'];
    $address = $data['address'];
    $visitingCard = $data['visiting_card'] ?? '';
    $fileName = $data['file_name'] ?? '';
    
    // Check if tbl_agents table exists, if not create it
    $sql = "SHOW TABLES LIKE 'tbl_agents'";
    $result = $conn->query($sql);
    
    if ($result->num_rows == 0) {
        // Create tbl_agents table
        $createTableSQL = "CREATE TABLE tbl_agents (
            id INT AUTO_INCREMENT PRIMARY KEY,
            phone_number VARCHAR(20) NOT NULL,
            full_name VARCHAR(100) NOT NULL,
            company_name VARCHAR(100) NOT NULL,
            alternative_number VARCHAR(20),
            email VARCHAR(100) NOT NULL,
            partner_type VARCHAR(50) NOT NULL,
            branch_state VARCHAR(50) NOT NULL,
            branch_location VARCHAR(100) NOT NULL,
            address TEXT NOT NULL,
            visiting_card LONGTEXT,
            file_name VARCHAR(255),
            created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            status ENUM('Active', 'Inactive') DEFAULT 'Active'
        )";
        
        if (!$conn->query($createTableSQL)) {
            throw new Exception("Error creating table: " . $conn->error);
        }
    }
    
    // Check if agent with same phone number already exists
    $checkSQL = "SELECT id FROM tbl_agents WHERE phone_number = ?";
    $checkStmt = $conn->prepare($checkSQL);
    $checkStmt->bind_param("s", $phoneNumber);
    $checkStmt->execute();
    $checkResult = $checkStmt->get_result();
    
    if ($checkResult->num_rows > 0) {
        throw new Exception("Agent with phone number '$phoneNumber' already exists");
    }
    
    // Insert agent data
    $insertSQL = "INSERT INTO tbl_agents (
        phone_number, full_name, company_name, alternative_number, email, 
        partner_type, branch_state, branch_location, address, visiting_card, file_name
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    $insertStmt = $conn->prepare($insertSQL);
    $insertStmt->bind_param("sssssssssss", 
        $phoneNumber, $fullName, $companyName, $alternativeNumber, $email,
        $partnerType, $branchState, $branchLocation, $address, $visitingCard, $fileName
    );
    
    if (!$insertStmt->execute()) {
        throw new Exception("Error inserting agent data: " . $insertStmt->error);
    }
    
    $agentId = $conn->insert_id;
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Agent added successfully',
        'data' => [
            'id' => $agentId,
            'phone_number' => $phoneNumber,
            'full_name' => $fullName,
            'company_name' => $companyName
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 