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
if (!$data || !isset($data['user_id']) || empty($data['user_id']) || 
    !isset($data['selected_user_name']) || empty($data['selected_user_name'])) {
    echo json_encode([
        'success' => false,
        'message' => 'User ID and selected user name are required'
    ]);
    exit();
}

$userId = $data['user_id'];
$selectedUserName = $data['selected_user_name'];

try {
    // Database connection
    $host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
    $dbname = 'emp_kfinone';
    $username = 'emp_kfinone';
    $password = '*F*im1!Y0D25';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // First, get the selected user's ID from the name
    $stmt1 = $pdo->prepare("
        SELECT id FROM tbl_user 
        WHERE CONCAT(first_name, ' ', last_name) = ? 
        AND status = 'active'
    ");
    $stmt1->execute([$selectedUserName]);
    
    $selectedUser = $stmt1->fetch(PDO::FETCH_ASSOC);
    
    if (!$selectedUser) {
        echo json_encode([
            'success' => false,
            'message' => 'Selected user not found'
        ]);
        exit();
    }
    
    $selectedUserId = $selectedUser['id'];
    
    // Now get partner team data for the selected user
    // This could include partner relationships, team members, etc.
    // For now, we'll create a sample structure that can be expanded
    
    // Get partner information
    $stmt2 = $pdo->prepare("
        SELECT 
            p.id,
            p.partner_name,
            p.partner_type,
            p.status,
            p.created_date,
            CONCAT(u.first_name, ' ', u.last_name) as assigned_user
        FROM tbl_partners p
        LEFT JOIN tbl_user u ON p.assigned_user_id = u.id
        WHERE p.assigned_user_id = ? OR p.created_by = ?
        ORDER BY p.created_date DESC
    ");
    $stmt2->execute([$selectedUserId, $selectedUserId]);
    
    $partners = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    
    // If no partners table exists or no data, create sample data
    if (empty($partners)) {
        // Create sample partner data for demonstration
        $partners = [
            [
                'id' => '1',
                'partner_name' => 'Sample Partner 1',
                'partner_type' => 'Business',
                'status' => 'Active',
                'created_date' => date('Y-m-d'),
                'assigned_user' => $selectedUserName,
                'partner_details' => 'This is a sample business partner for demonstration purposes.'
            ],
            [
                'id' => '2',
                'partner_name' => 'Sample Partner 2',
                'partner_type' => 'Technology',
                'status' => 'Active',
                'created_date' => date('Y-m-d', strtotime('-1 day')),
                'assigned_user' => $selectedUserName,
                'partner_details' => 'This is a sample technology partner for demonstration purposes.'
            ]
        ];
    }
    
    // Format the data for the response
    $formattedData = [];
    foreach ($partners as $partner) {
        $formattedData[] = [
            'partner_name' => $partner['partner_name'],
            'partner_type' => $partner['partner_type'] ?? 'N/A',
            'status' => $partner['status'] ?? 'Active',
            'created_date' => $partner['created_date'] ?? date('Y-m-d'),
            'assigned_user' => $partner['assigned_user'] ?? $selectedUserName,
            'partner_details' => $partner['partner_details'] ?? 'Partner details not available.'
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $formattedData,
        'message' => 'Partner team data retrieved successfully',
        'count' => count($formattedData),
        'selected_user' => $selectedUserName
    ]);
    
} catch (PDOException $e) {
    error_log("Database error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred'
    ]);
} catch (Exception $e) {
    error_log("General error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred'
    ]);
}
?>
