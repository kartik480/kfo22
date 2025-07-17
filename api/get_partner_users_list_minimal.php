<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

try {
    // Include database configuration
    require_once 'db_config.php';
    
    // Simple query to get basic partner user data
    $sql = "SELECT 
                id,
                username,
                first_name,
                last_name,
                Phone_number,
                email_id,
                company_name,
                department,
                designation,
                status,
                created_at
            FROM tbl_partner_users
            ORDER BY created_at DESC
            LIMIT 50";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $partnerUsers = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine first name and last name for display
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            
            $partnerUsers[] = array(
                'id' => $row['id'],
                'username' => $row['username'] ?? 'N/A',
                'first_name' => $row['first_name'] ?? 'N/A',
                'last_name' => $row['last_name'] ?? 'N/A',
                'full_name' => $fullName,
                'phone_number' => $row['Phone_number'] ?? 'N/A',
                'email_id' => $row['email_id'] ?? 'N/A',
                'company_name' => $row['company_name'] ?? 'N/A',
                'department' => $row['department'] ?? 'N/A',
                'designation' => $row['designation'] ?? 'N/A',
                'status' => $row['status'] ?? 'Active',
                'created_at' => $row['created_at'] ?? date('Y-m-d H:i:s')
            );
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $partnerUsers,
        'message' => 'Partner users fetched successfully (minimal version)',
        'count' => count($partnerUsers)
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage(),
        'data' => [],
        'count' => 0
    ]);
}
?> 