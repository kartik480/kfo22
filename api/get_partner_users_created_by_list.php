<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Query to get all unique values from createdBy column in tbl_partner_users
    $query = "SELECT DISTINCT 
                createdBy,
                COUNT(*) as count
              FROM tbl_partner_users 
              WHERE createdBy IS NOT NULL 
              AND createdBy != ''
              GROUP BY createdBy
              ORDER BY createdBy ASC";
    
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $createdByList = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Get total count of partner users
    $totalQuery = "SELECT COUNT(*) as total_partner_users FROM tbl_partner_users";
    $totalStmt = $pdo->prepare($totalQuery);
    $totalStmt->execute();
    $totalResult = $totalStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get count of partner users with createdBy
    $withCreatedByQuery = "SELECT COUNT(*) as with_created_by FROM tbl_partner_users WHERE createdBy IS NOT NULL AND createdBy != ''";
    $withCreatedByStmt = $pdo->prepare($withCreatedByQuery);
    $withCreatedByStmt->execute();
    $withCreatedByResult = $withCreatedByStmt->fetch(PDO::FETCH_ASSOC);
    
    // Get count of partner users without createdBy
    $withoutCreatedByQuery = "SELECT COUNT(*) as without_created_by FROM tbl_partner_users WHERE createdBy IS NULL OR createdBy = ''";
    $withoutCreatedByStmt = $pdo->prepare($withoutCreatedByQuery);
    $withoutCreatedByStmt->execute();
    $withoutCreatedByResult = $withoutCreatedByStmt->fetch(PDO::FETCH_ASSOC);
    
    // Format the response
    $formattedList = [];
    foreach ($createdByList as $item) {
        $formattedList[] = [
            'createdBy' => $item['createdBy'],
            'count' => (int)$item['count']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'CreatedBy list from tbl_partner_users fetched successfully',
        'data' => $formattedList,
        'statistics' => [
            'total_unique_creators' => count($formattedList),
            'total_partner_users' => (int)$totalResult['total_partner_users'],
            'with_created_by' => (int)$withCreatedByResult['with_created_by'],
            'without_created_by' => (int)$withoutCreatedByResult['without_created_by']
        ],
        'api_info' => [
            'version' => '1.0',
            'timestamp' => date('Y-m-d H:i:s'),
            'table' => 'tbl_partner_users',
            'column' => 'createdBy'
        ]
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 