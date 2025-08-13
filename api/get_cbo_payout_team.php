<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Database connection
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$username = "emp_kfinone";
$password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get user designation from request (you'll need to pass this from frontend)
    $user_designation = isset($_GET['designation']) ? $_GET['designation'] : '';
    $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '';
    
    if (empty($user_designation) || empty($user_id)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'User designation and ID are required'
        ]);
        exit;
    }
    
    // First, get the logged-in user's payout_icons
    $user_sql = "SELECT payout_icons FROM tbl_user WHERE id = :user_id";
    $user_stmt = $conn->prepare($user_sql);
    $user_stmt->bindParam(':user_id', $user_id);
    $user_stmt->execute();
    $user_data = $user_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user_data || empty($user_data['payout_icons']) || $user_data['payout_icons'] == '[]') {
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout types assigned to this user',
            'data' => [],
            'count' => 0,
            'user_designation' => $user_designation
        ]);
        exit;
    }
    
    // Get payout types for the logged-in user
    // Handle JSON format: ["1","4","5","3","8","6","2"]
    $payout_icons_json = $user_data['payout_icons'];
    $payout_ids = json_decode($payout_icons_json, true);
    
    if (!$payout_ids || !is_array($payout_ids)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'Invalid payout_icons format',
            'data' => [],
            'count' => 0,
            'user_designation' => $user_designation
        ]);
        exit;
    }
    
    $payout_names = [];
    
    foreach ($payout_ids as $payout_id) {
        $payout_id = trim($payout_id);
        if (!empty($payout_id)) {
            $payout_sql = "SELECT id, payout_name FROM tbl_payout_type WHERE id = :id";
            $payout_stmt = $conn->prepare($payout_sql);
            $payout_stmt->bindParam(':id', $payout_id);
            $payout_stmt->execute();
            $payout_result = $payout_stmt->fetch(PDO::FETCH_ASSOC);
            if ($payout_result) {
                $payout_names[] = $payout_result;
            }
        }
    }
    
    if (empty($payout_names)) {
        echo json_encode([
            'status' => 'success',
            'message' => 'No payout types found for this user',
            'data' => [],
            'count' => 0,
            'user_designation' => $user_designation
        ]);
        exit;
    }
    
    // Return the payout types for the logged-in user
    echo json_encode([
        'status' => 'success',
        'message' => 'Payout types fetched successfully for logged-in user',
        'data' => $payout_names,
        'count' => count($payout_names),
        'user_designation' => $user_designation,
        'user_id' => $user_id
    ]);
    
} catch(PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $e->getMessage()
    ]);
}

$conn = null;
?>
