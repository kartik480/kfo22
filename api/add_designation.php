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
    $designationName = $_POST['designation_name'] ?? '';
    $departmentId = $_POST['department_id'] ?? null;
    
    if (empty($designationName)) {
        throw new Exception("Designation name is required");
    }
    
    // Check if designation already exists
    $checkSql = "SELECT id FROM tbl_designation WHERE designation_name = ?";
    $checkStmt = $pdo->prepare($checkSql);
    $checkStmt->execute([$designationName]);
    
    if ($checkStmt->rowCount() > 0) {
        throw new Exception("Designation '$designationName' already exists");
    }
    
    // If department_id is provided, validate it exists
    if ($departmentId) {
        $deptCheckSql = "SELECT id FROM tbl_department WHERE id = ?";
        $deptCheckStmt = $pdo->prepare($deptCheckSql);
        $deptCheckStmt->execute([$departmentId]);
        
        if ($deptCheckStmt->rowCount() == 0) {
            throw new Exception("Department ID '$departmentId' not found");
        }
    }
    
    // Insert new designation
    $sql = "INSERT INTO tbl_designation (designation_name, department_id, status, created_at, updated_at) VALUES (?, ?, 'Active', NOW(), NOW())";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$designationName, $departmentId]);
    
    $designationId = $pdo->lastInsertId();
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Designation added successfully',
        'data' => [
            'id' => $designationId,
            'designation_name' => $designationName,
            'department_id' => $departmentId,
            'status' => 'Active'
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