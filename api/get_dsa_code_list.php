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
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    // Build the query with optional filters
    $query = "SELECT 
                vendor_bank,
                dsa_code,
                bsa_name,
                loan_type,
                state,
                location
              FROM tbl_dsa_code 
              WHERE 1=1";
    
    $params = array();
    
    // Add filters if provided
    if (!empty($input['vendor_bank']) && $input['vendor_bank'] !== 'All Vendor Banks') {
        $query .= " AND vendor_bank = ?";
        $params[] = $input['vendor_bank'];
    }
    
    if (!empty($input['loan_type']) && $input['loan_type'] !== 'All Loan Types') {
        $query .= " AND loan_type = ?";
        $params[] = $input['loan_type'];
    }
    
    if (!empty($input['state']) && $input['state'] !== 'All States') {
        $query .= " AND state = ?";
        $params[] = $input['state'];
    }
    
    if (!empty($input['location']) && $input['location'] !== 'All Locations') {
        $query .= " AND location = ?";
        $params[] = $input['location'];
    }
    
    $query .= " ORDER BY vendor_bank, dsa_code";
    
    // Prepare and execute the statement
    $stmt = $conn->prepare($query);
    
    if (!empty($params)) {
        $stmt->bind_param(str_repeat('s', count($params)), ...$params);
    }
    
    $stmt->execute();
    $result = $stmt->get_result();
    
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $data[] = array(
            'vendor_bank' => $row['vendor_bank'],
            'dsa_code' => $row['dsa_code'],
            'bsa_name' => $row['bsa_name'],
            'loan_type' => $row['loan_type'],
            'state' => $row['state'],
            'location' => $row['location']
        );
    }
    
    $stmt->close();
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'data' => $data,
        'count' => count($data)
    ));
    
} catch (Exception $e) {
    // Return error response
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 