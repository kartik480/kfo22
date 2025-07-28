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
    $designationId = $_POST['designation_id'] ?? '';
    
    if (empty($designationId)) {
        throw new Exception("Designation ID is required");
    }
    
    // Check if designation exists
    $checkSql = "SELECT id, designation_name FROM tbl_designation WHERE id = ?";
    $checkStmt = $pdo->prepare($checkSql);
    $checkStmt->execute([$designationId]);
    
    if ($checkStmt->rowCount() == 0) {
        throw new Exception("Designation not found");
    }
    
    $designation = $checkStmt->fetch(PDO::FETCH_ASSOC);
    $designationName = $designation['designation_name'];
    
    // Check if designation is being used by any users
    $userCheckSql = "SELECT COUNT(*) as user_count FROM tbl_user WHERE designation_id = ?";
    $userCheckStmt = $pdo->prepare($userCheckSql);
    $userCheckStmt->execute([$designationId]);
    $userCount = $userCheckStmt->fetch(PDO::FETCH_ASSOC)['user_count'];
    
    if ($userCount > 0) {
        throw new Exception("Cannot delete designation '$designationName' - it is assigned to $userCount user(s). Please reassign users first.");
    }
    
    // Delete the designation
    $deleteSql = "DELETE FROM tbl_designation WHERE id = ?";
    $deleteStmt = $pdo->prepare($deleteSql);
    $deleteStmt->execute([$designationId]);
    
    if ($deleteStmt->rowCount() > 0) {
        echo json_encode([
            'status' => 'success',
            'message' => "Designation '$designationName' deleted successfully",
            'data' => [
                'deleted_id' => $designationId,
                'deleted_name' => $designationName
            ]
        ]);
    } else {
        throw new Exception("Failed to delete designation");
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?> 