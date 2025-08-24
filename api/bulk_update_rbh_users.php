<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_config.php';
    $conn = getConnection();
    
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Only POST method is allowed');
    }
    
    // Get POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        throw new Exception('Invalid JSON input');
    }
    
    // Validate required fields
    if (empty($input['user_ids']) || !is_array($input['user_ids'])) {
        throw new Exception('User IDs array is required');
    }
    
    if (!isset($input['status'])) {
        throw new Exception('Status is required');
    }
    
    if (empty($input['reportingTo'])) {
        throw new Exception('Reporting To is required');
    }
    
    // Validate status values
    $allowedStatuses = ['Active', 'Inactive', 'Suspended', 'Terminated'];
    if (!in_array($input['status'], $allowedStatuses)) {
        throw new Exception('Invalid status. Allowed values: ' . implode(', ', $allowedStatuses));
    }
    
    // Validate user IDs are numeric
    foreach ($input['user_ids'] as $userId) {
        if (!is_numeric($userId)) {
            throw new Exception('Invalid user ID: ' . $userId);
        }
    }
    
    // Check if all users exist and belong to the reporting manager
    $placeholders = str_repeat('?,', count($input['user_ids']) - 1) . '?';
    $checkSql = "
        SELECT id, username, status 
        FROM tbl_user 
        WHERE id IN ($placeholders) 
        AND reportingTo = ?
    ";
    
    $checkParams = array_merge($input['user_ids'], [$input['reportingTo']]);
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute($checkParams);
    $existingUsers = $checkStmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($existingUsers) !== count($input['user_ids'])) {
        throw new Exception('Some users not found or do not belong to you');
    }
    
    // Begin transaction
    $conn->beginTransaction();
    
    try {
        // Update all users
        $updateSql = "UPDATE tbl_user SET status = ?, updated_at = NOW() WHERE id = ?";
        $updateStmt = $conn->prepare($updateSql);
        
        $updatedCount = 0;
        $updateResults = [];
        
        foreach ($input['user_ids'] as $userId) {
            $result = $updateStmt->execute([$input['status'], $userId]);
            if ($result) {
                $updatedCount++;
                $updateResults[] = [
                    'user_id' => $userId,
                    'status' => 'updated'
                ];
            } else {
                $updateResults[] = [
                    'user_id' => $userId,
                    'status' => 'failed'
                ];
            }
        }
        
        // Commit transaction
        $conn->commit();
        
        $response = [
            'status' => 'success',
            'message' => "Bulk update completed. $updatedCount out of " . count($input['user_ids']) . " users updated.",
            'data' => [
                'total_requested' => count($input['user_ids']),
                'successfully_updated' => $updatedCount,
                'failed_updates' => count($input['user_ids']) - $updatedCount,
                'new_status' => $input['status'],
                'update_results' => $updateResults,
                'updated_at' => date('Y-m-d H:i:s')
            ]
        ];
        
        echo json_encode($response, JSON_PRETTY_PRINT);
        
    } catch (Exception $e) {
        // Rollback transaction on error
        $conn->rollback();
        throw $e;
    }
    
} catch (Exception $e) {
    error_log("RBH Bulk Update Users Error: " . $e->getMessage());
    
    $response = [
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ];
    
    http_response_code(500);
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
