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
    // Check if this is a GET request (for loading all records) or POST request (for filtered data)
    $isGetRequest = $_SERVER['REQUEST_METHOD'] === 'GET';
    
    if ($isGetRequest) {
        // GET request - return all DSA records with proper joins using ID columns
        $query = "SELECT 
                    vb.vendor_bank_name,
                    dc.dsa_code,
                    bn.bsa_name,
                    lt.loan_type,
                    bs.branch_state_name,
                    bl.branch_location,
                    dc.status
                  FROM tbl_dsa_code dc
                  LEFT JOIN tbl_vendor_bank vb ON dc.vendor_bank = vb.id
                  LEFT JOIN tbl_bsa_name bn ON dc.bsa_name = bn.id
                  LEFT JOIN tbl_loan_type lt ON dc.loan_type = lt.id
                  LEFT JOIN tbl_branch_state bs ON dc.state = bs.id
                  LEFT JOIN tbl_branch_location bl ON dc.location = bl.id
                  ORDER BY dc.dsa_code ASC";
        
        $stmt = $conn->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        
    } else {
        // POST request - return filtered data with proper joins using ID columns
        $input = json_decode(file_get_contents('php://input'), true);
        
        // Build the query with optional filters and proper joins using ID columns
        $query = "SELECT 
                    vb.vendor_bank_name,
                    dc.dsa_code,
                    bn.bsa_name,
                    lt.loan_type,
                    bs.branch_state_name,
                    bl.branch_location,
                    dc.status
                  FROM tbl_dsa_code dc
                  LEFT JOIN tbl_vendor_bank vb ON dc.vendor_bank = vb.id
                  LEFT JOIN tbl_bsa_name bn ON dc.bsa_name = bn.id
                  LEFT JOIN tbl_loan_type lt ON dc.loan_type = lt.id
                  LEFT JOIN tbl_branch_state bs ON dc.state = bs.id
                  LEFT JOIN tbl_branch_location bl ON dc.location = bl.id
                  WHERE 1=1";
        
        $params = array();
        
        // Add filters if provided
        if (!empty($input['vendor_bank']) && $input['vendor_bank'] !== 'Select Vendor Bank') {
            $query .= " AND vb.vendor_bank_name = ?";
            $params[] = $input['vendor_bank'];
        }
        
        if (!empty($input['loan_type']) && $input['loan_type'] !== 'Select Loan Type') {
            $query .= " AND lt.loan_type = ?";
            $params[] = $input['loan_type'];
        }
        
        if (!empty($input['state']) && $input['state'] !== 'Select State') {
            $query .= " AND bs.branch_state_name = ?";
            $params[] = $input['state'];
        }
        
        if (!empty($input['location']) && $input['location'] !== 'Select Location') {
            $query .= " AND bl.branch_location = ?";
            $params[] = $input['location'];
        }
        
        if (!empty($input['dsa_code'])) {
            $query .= " AND dc.dsa_code LIKE ?";
            $params[] = '%' . $input['dsa_code'] . '%';
        }
        
        $query .= " ORDER BY dc.dsa_code ASC";
        
        // Prepare and execute the statement
        $stmt = $conn->prepare($query);
        
        if (!empty($params)) {
            $stmt->bind_param(str_repeat('s', count($params)), ...$params);
        }
        
        $stmt->execute();
        $result = $stmt->get_result();
    }
    
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $data[] = array(
            'vendor_bank_name' => $row['vendor_bank_name'] ?? 'N/A',
            'dsa_code' => $row['dsa_code'] ?? 'N/A',
            'bsa_name' => $row['bsa_name'] ?? 'N/A',
            'loan_type' => $row['loan_type'] ?? 'N/A',
            'branch_state_name' => $row['branch_state_name'] ?? 'N/A',
            'branch_location' => $row['branch_location'] ?? 'N/A',
            'status' => $row['status'] ?? 'N/A'
        );
    }
    
    echo json_encode(array(
        'success' => true,
        'data' => $data,
        'count' => count($data)
    ));
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 