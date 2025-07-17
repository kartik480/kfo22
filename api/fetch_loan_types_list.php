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
    
    // Check if tbl_loan_type table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_loan_type'");
    if ($stmt->rowCount() == 0) {
        echo json_encode([
            'success' => false,
            'message' => 'Table tbl_loan_type does not exist',
            'data' => []
        ]);
        exit;
    }
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE tbl_loan_type");
    $columns = $stmt->fetchAll(PDO::FETCH_COLUMN);
    
    $requiredColumns = ['loan_type', 'status'];
    $missingColumns = array_diff($requiredColumns, $columns);
    
    if (!empty($missingColumns)) {
        echo json_encode([
            'success' => false,
            'message' => 'Missing required columns: ' . implode(', ', $missingColumns),
            'available_columns' => $columns,
            'data' => []
        ]);
        exit;
    }
    
    // Get filter parameters
    $status = isset($_GET['status']) ? trim($_GET['status']) : '';
    
    // Build the query
    $query = "SELECT loan_type, status FROM tbl_loan_type WHERE 1=1";
    $params = [];
    
    // Add status filter if provided
    if (!empty($status)) {
        $query .= " AND status = ?";
        $params[] = $status;
    }
    
    $query .= " ORDER BY loan_type ASC";
    
    // Prepare and execute the query
    $stmt = $pdo->prepare($query);
    $stmt->execute($params);
    $loanTypes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count
    $countQuery = "SELECT COUNT(*) as total FROM tbl_loan_type WHERE 1=1";
    $countParams = [];
    
    if (!empty($status)) {
        $countQuery .= " AND status = ?";
        $countParams[] = $status;
    }
    
    $countStmt = $pdo->prepare($countQuery);
    $countStmt->execute($countParams);
    $countResult = $countStmt->fetch(PDO::FETCH_ASSOC);
    $totalCount = $countResult['total'];
    
    // Get status distribution for info
    $statusQuery = "SELECT status, COUNT(*) as count FROM tbl_loan_type GROUP BY status";
    $statusStmt = $pdo->query($statusQuery);
    $statusDistribution = $statusStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Build response
    $response = [
        'success' => true,
        'message' => 'Loan types fetched successfully',
        'filter_applied' => [
            'status' => $status
        ],
        'total_count' => $totalCount,
        'filtered_count' => count($loanTypes),
        'status_distribution' => $statusDistribution,
        'data' => $loanTypes
    ];
    
    // If no status filter applied, add a note
    if (empty($status)) {
        $response['message'] = 'All loan types fetched (no status filter applied)';
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