<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array('success' => false, 'error' => 'Method not allowed'));
    exit();
}

try {
    // Create connection using PDO
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $input = $_POST;
    }
    
    $subLocationId = $input['id'] ?? null;
    $newStatus = $input['status'] ?? null; // 'Active' or 'Inactive'
    
    // Validate input
    if (!$subLocationId) {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Sub location ID is required'));
        exit();
    }
    
    // Convert text status to numeric (Active = 1, Inactive = 0)
    $statusNumeric = null;
    if (strtolower($newStatus) === 'active') {
        $statusNumeric = 1;
    } elseif (strtolower($newStatus) === 'inactive') {
        $statusNumeric = 0;
    } else {
        http_response_code(400);
        echo json_encode(array('success' => false, 'error' => 'Status must be either "Active" or "Inactive"'));
        exit();
    }
    
    // Update the status
    $updateStmt = $conn->prepare("UPDATE tbl_sub_location SET status = ? WHERE id = ?");
    $result = $updateStmt->execute([$statusNumeric, $subLocationId]);
    
    if ($result && $updateStmt->rowCount() > 0) {
        // Get the updated sub location data
        $selectStmt = $conn->prepare("
            SELECT 
                sl.id,
                sl.sub_location,
                sl.state_id,
                sl.location_id,
                sl.status,
                s.state_name,
                l.location as location_name
            FROM tbl_sub_location sl 
            LEFT JOIN tbl_state s ON sl.state_id = s.id 
            LEFT JOIN tbl_location l ON sl.location_id = l.id 
            WHERE sl.id = ?
        ");
        $selectStmt->execute([$subLocationId]);
        $subLocation = $selectStmt->fetch(PDO::FETCH_ASSOC);
        
        if ($subLocation) {
            // Convert numeric status to text (1 = Active, 0 = Inactive)
            $statusText = ($subLocation['status'] == 1) ? 'Active' : 'Inactive';
            
            echo json_encode(array(
                'success' => true,
                'message' => 'Sub location status updated successfully',
                'data' => array(
                    'id' => $subLocation['id'],
                    'sub_location' => $subLocation['sub_location'],
                    'state_id' => $subLocation['state_id'],
                    'location_id' => $subLocation['location_id'],
                    'status' => $statusText,
                    'status_value' => $subLocation['status'], // Keep the numeric value for reference
                    'state_name' => $subLocation['state_name'] ?: 'Unknown',
                    'location_name' => $subLocation['location_name'] ?: 'Unknown'
                )
            ));
        } else {
            echo json_encode(array(
                'success' => true,
                'message' => 'Sub location status updated successfully'
            ));
        }
    } else {
        http_response_code(404);
        echo json_encode(array('success' => false, 'error' => 'Sub location not found or no changes made'));
    }
    
    $conn = null;
    
} catch (Exception $e) {
    error_log("Toggle sub location status error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'error' => 'An error occurred while updating sub location status: ' . $e->getMessage()
    ));
}
?> 