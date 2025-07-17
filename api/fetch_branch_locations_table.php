<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Include database configuration
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Fetch all branch locations with related data
    $query = "
        SELECT 
            bl.id,
            bl.branch_location,
            bl.branch_state_id,
            bl.created_date,
            bl.created_user,
            bl.status,
            bs.branch_state_name
        FROM tbl_branch_location bl
        LEFT JOIN tbl_branch_state bs ON bl.branch_state_id = bs.id
        ORDER BY bl.created_date DESC
    ";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format the data for table display
    $formattedData = [];
    foreach ($results as $row) {
        $formattedData[] = [
            'id' => $row['id'],
            'branch_location' => $row['branch_location'],
            'branch_state_name' => $row['branch_state_name'] ?? 'N/A',
            'created_date' => $row['created_date'],
            'created_user' => $row['created_user'] ?? 'N/A',
            'status' => $row['status'] ?? 'Active'
        ];
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Branch locations fetched successfully',
        'data' => $formattedData,
        'total_count' => count($formattedData)
    ]);
    
} catch (Exception $e) {
    error_log("Error fetching branch locations: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'Error fetching branch locations: ' . $e->getMessage(),
        'data' => [],
        'total_count' => 0
    ]);
} finally {
    if (isset($conn)) {
        closeConnection($conn);
    }
}
?> 