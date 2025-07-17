<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
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
    
    // Check if tbl_bankers table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_bankers'");
    if ($stmt->rowCount() == 0) {
        echo json_encode([
            'success' => false,
            'message' => 'Table tbl_bankers does not exist',
            'data' => []
        ]);
        exit;
    }
    
    // Get filter parameters
    $vendorBank = isset($_GET['vendor_bank']) ? trim($_GET['vendor_bank']) : '';
    $loanType = isset($_GET['loan_type']) ? trim($_GET['loan_type']) : '';
    $state = isset($_GET['state']) ? trim($_GET['state']) : '';
    $location = isset($_GET['location']) ? trim($_GET['location']) : '';
    
    // Build the query with joins
    $query = "
        SELECT 
            b.id,
            b.banker_name,
            b.Phone_number,
            b.email_id,
            b.banker_designation,
            b.visiting_card,
            vb.vendor_bank_name as vendor_bank,
            lt.loan_type,
            bs.branch_state_name as state,
            bl.branch_location as location
        FROM tbl_bankers b
        LEFT JOIN tbl_vendor_bank vb ON b.vendor_bank = vb.id
        LEFT JOIN tbl_loan_type lt ON b.loan_type = lt.id
        LEFT JOIN tbl_branch_state bs ON b.state = bs.id
        LEFT JOIN tbl_branch_location bl ON b.location = bl.id
        WHERE 1=1
    ";
    
    $params = [];
    
    // Add filters if provided
    if (!empty($vendorBank)) {
        $query .= " AND vb.vendor_bank_name = ?";
        $params[] = $vendorBank;
    }
    
    if (!empty($loanType)) {
        $query .= " AND lt.loan_type = ?";
        $params[] = $loanType;
    }
    
    if (!empty($state)) {
        $query .= " AND bs.branch_state_name = ?";
        $params[] = $state;
    }
    
    if (!empty($location)) {
        $query .= " AND bl.branch_location = ?";
        $params[] = $location;
    }
    
    $query .= " ORDER BY b.banker_name ASC";
    
    // Prepare and execute the query
    $stmt = $pdo->prepare($query);
    $stmt->execute($params);
    $bankers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count for showcase
    $countQuery = "
        SELECT COUNT(*) as total
        FROM tbl_bankers b
        LEFT JOIN tbl_vendor_bank vb ON b.vendor_bank = vb.id
        LEFT JOIN tbl_loan_type lt ON b.loan_type = lt.id
        LEFT JOIN tbl_branch_state bs ON b.state = bs.id
        LEFT JOIN tbl_branch_location bl ON b.location = bl.id
        WHERE 1=1
    ";
    
    $countParams = [];
    
    if (!empty($vendorBank)) {
        $countQuery .= " AND vb.vendor_bank_name = ?";
        $countParams[] = $vendorBank;
    }
    
    if (!empty($loanType)) {
        $countQuery .= " AND lt.loan_type = ?";
        $countParams[] = $loanType;
    }
    
    if (!empty($state)) {
        $countQuery .= " AND bs.branch_state_name = ?";
        $countParams[] = $state;
    }
    
    if (!empty($location)) {
        $countQuery .= " AND bl.branch_location = ?";
        $countParams[] = $location;
    }
    
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute($countParams);
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Build response
    $response = [
        'success' => true,
        'message' => 'Bankers fetched successfully',
        'filters_applied' => [
            'vendor_bank' => $vendorBank,
            'loan_type' => $loanType,
            'state' => $state,
            'location' => $location
        ],
        'total_count' => $totalCount,
        'filtered_count' => count($bankers),
        'data' => $bankers
    ];
    
    // If no filters applied, add a note for showcase
    if (empty($vendorBank) && empty($loanType) && empty($state) && empty($location)) {
        $response['message'] = 'All bankers fetched (showcase mode - no filters applied)';
    }
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 