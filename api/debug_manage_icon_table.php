<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        'success' => false,
        'message' => 'Only POST method is allowed'
    ]);
    exit();
}

// Get JSON input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validate input
if (!$data || !isset($data['icon_ids']) || !is_array($data['icon_ids']) || empty($data['icon_ids'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Icon IDs array is required'
    ]);
    exit();
}

$iconIds = $data['icon_ids'];

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Create placeholders for IN clause
    $placeholders = str_repeat('?,', count($iconIds) - 1) . '?';
    
    // First, let's see what's in the table without any filters
    $stmt1 = $pdo->prepare("SELECT id, icon_name, icon_url, icon_image, icon_description, status FROM tbl_manage_icon WHERE id IN ($placeholders)");
    $stmt1->execute($iconIds);
    $allResults = $stmt1->fetchAll(PDO::FETCH_ASSOC);
    
    // Now let's see what's in the table with status filter
    $stmt2 = $pdo->prepare("SELECT id, icon_name, icon_url, icon_image, icon_description, status FROM tbl_manage_icon WHERE id IN ($placeholders) AND status = 'active'");
    $stmt2->execute($iconIds);
    $activeResults = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    
    // Let's also check the table structure
    $stmt3 = $pdo->prepare("DESCRIBE tbl_manage_icon");
    $stmt3->execute();
    $tableStructure = $stmt3->fetchAll(PDO::FETCH_ASSOC);
    
    // Let's see a few sample records from the table
    $stmt4 = $pdo->prepare("SELECT * FROM tbl_manage_icon LIMIT 5");
    $stmt4->execute();
    $sampleRecords = $stmt4->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'debug_info' => [
            'requested_icon_ids' => $iconIds,
            'all_results_without_status_filter' => $allResults,
            'active_results_with_status_filter' => $activeResults,
            'table_structure' => $tableStructure,
            'sample_records' => $sampleRecords,
            'count_all_results' => count($allResults),
            'count_active_results' => count($activeResults)
        ],
        'message' => 'Debug information retrieved successfully'
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred: ' . $e->getMessage()
    ]);
}
?>
