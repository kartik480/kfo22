<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Include database configuration
require_once 'db_config.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $response['success'] = false;
        $response['message'] = 'Invalid JSON input.';
        echo json_encode($response);
        exit;
    }
    
    $username = isset($input['username']) ? trim($input['username']) : '';
    $password = isset($input['password']) ? trim($input['password']) : '';

    if (empty($username) || empty($password)) {
        $response['success'] = false;
        $response['message'] = 'Username and password are required.';
        echo json_encode($response);
        exit;
    }

    try {
        $conn = getConnection();
        
        // First, let's check if the user exists with designation information
        $stmt = $conn->prepare('
            SELECT u.*, d.designation_name 
            FROM tbl_user u 
            LEFT JOIN tbl_designation d ON u.designation_id = d.id 
            WHERE u.username = :username 
            LIMIT 1
        ');
        $stmt->bindParam(':username', $username);
        $stmt->execute();
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($user) {
            // User exists, now check password
            if ($user['password'] === $password) {
                $response['success'] = true;
                $response['message'] = 'Login successful.';
                $response['user'] = array(
                    'id' => $user['id'],
                    'username' => $user['username'],
                    'firstName' => $user['firstName'] ?? '',
                    'lastName' => $user['lastName'] ?? '',
                    'designation_name' => $user['designation_name'] ?? ''
                );
                
                // Check if this is the special user (ID 8)
                if ($user['id'] == 8) {
                    $response['is_special_user'] = true;
                    $response['special_message'] = 'Welcome to your special panel!';
                } else {
                    $response['is_special_user'] = false;
                }
                
                // Check if user is Chief Business Officer
                if ($user['designation_name'] === 'Chief Business Officer') {
                    $response['is_chief_business_officer'] = true;
                } else {
                    $response['is_chief_business_officer'] = false;
                }
                
                // Check if user is Regional Business Head
                if ($user['designation_name'] === 'Regional Business Head') {
                    $response['is_regional_business_head'] = true;
                } else {
                    $response['is_regional_business_head'] = false;
                }
                
                // Check if user is Business Head
                if ($user['designation_name'] === 'Business Head') {
                    $response['is_business_head'] = true;
                } else {
                    $response['is_business_head'] = false;
                }
            } else {
                $response['success'] = false;
                $response['message'] = 'Invalid password.';
            }
        } else {
            $response['success'] = false;
            $response['message'] = 'User not found.';
        }
        
        closeConnection($conn);
    } catch (Exception $e) {
        $response['success'] = false;
        $response['message'] = 'Database error: ' . $e->getMessage();
    }
} else {
    $response['success'] = false;
    $response['message'] = 'Invalid request method.';
}

echo json_encode($response);
?> 