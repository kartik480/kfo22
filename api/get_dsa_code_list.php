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
    
    // Build the query with JOINs to get actual names instead of IDs
    $query = "SELECT 
                vb.vendor_bank_name as vendor_bank,
                dc.dsa_code,
                bn.bsa_name,
                lt.loan_type,
                bs.branch_state_name as state,
                bl.branch_location as location
              FROM tbl_dsa_code dc
              LEFT JOIN tbl_vendor_bank vb ON dc.vendor_bank = vb.id
              LEFT JOIN tbl_bsa_name bn ON dc.bsa_name = bn.id
              LEFT JOIN tbl_loan_type lt ON dc.loan_type = lt.id
              LEFT JOIN tbl_branch_state bs ON dc.state = bs.id
              LEFT JOIN tbl_branch_location bl ON dc.location = bl.id
              WHERE 1=1";
    
    $params = array();
    
    // Add filters if provided - now filter by names instead of IDs
    if (!empty($input['vendor_bank']) && $input['vendor_bank'] !== 'All Vendor Banks') {
        $query .= " AND vb.vendor_bank_name = ?";
        $params[] = $input['vendor_bank'];
    }
    
    if (!empty($input['loan_type']) && $input['loan_type'] !== 'All Loan Types') {
        $query .= " AND lt.loan_type = ?";
        $params[] = $input['loan_type'];
    }
    
    if (!empty($input['state']) && $input['state'] !== 'All States') {
        $query .= " AND bs.branch_state_name = ?";
        $params[] = $input['state'];
    }
    
    if (!empty($input['location']) && $input['location'] !== 'All Locations') {
        $query .= " AND bl.branch_location = ?";
        $params[] = $input['location'];
    }
    
    $query .= " ORDER BY vb.vendor_bank_name, dc.dsa_code";
    
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
            'vendor_bank' => $row['vendor_bank'] ?? 'N/A',
            'dsa_code' => $row['dsa_code'] ?? 'N/A',
            'bsa_name' => $row['bsa_name'] ?? 'N/A',
            'loan_type' => $row['loan_type'] ?? 'N/A',
            'state' => $row['state'] ?? 'N/A',
            'location' => $row['location'] ?? 'N/A'
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