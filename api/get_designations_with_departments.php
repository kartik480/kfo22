<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
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
    
    // Query to fetch designations with department names
    $sql = "SELECT 
                d.id,
                d.designation_name,
                d.department_id,
                d.status,
                dept.department_name
            FROM tbl_designation d
            LEFT JOIN tbl_department dept ON d.department_id = dept.id
            ORDER BY d.designation_name ASC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $designations = array();
    $totalCount = 0;
    
    foreach ($result as $row) {
        $totalCount++;
        
        $designations[] = array(
            'id' => $row['id'],
            'designation_name' => $row['designation_name'],
            'department_id' => $row['department_id'],
            'department_name' => $row['department_name'] ? $row['department_name'] : 'N/A',
            'status' => $row['status'] ? $row['status'] : 'Active'
        );
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Designations with departments fetched successfully',
        'data' => $designations,
        'summary' => [
            'total_designations' => $totalCount
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'data' => [],
        'summary' => [
            'total_designations' => 0
        ]
    ]);
}
?> 