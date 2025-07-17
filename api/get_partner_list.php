<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    // Check if tbl_partners table exists
    $sql = "SHOW TABLES LIKE 'tbl_partners'";
    $result = $conn->query($sql);
    
    if ($result->num_rows == 0) {
        // Table doesn't exist, return empty list
        echo json_encode([
            'status' => 'success',
            'data' => [],
            'message' => 'No partners table found',
            'count' => 0
        ]);
        exit;
    }
    
    // Fetch partner data with all required fields
    $sql = "SELECT 
                p.id,
                p.first_name,
                p.last_name,
                p.alias_name,
                p.email,
                p.password,
                p.phone_number,
                p.created_by,
                p.created_date,
                pt.partner_type_name,
                p.status
            FROM tbl_partners p
            LEFT JOIN tbl_partner_type pt ON p.partner_type_id = pt.id
            ORDER BY p.created_date DESC";
    
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $partners = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine first name and last name
            $fullName = trim($row['first_name'] . ' ' . $row['last_name']);
            if (!empty($row['alias_name'])) {
                $fullName .= ' (' . $row['alias_name'] . ')';
            }
            
            $partners[] = array(
                'id' => $row['id'],
                'name' => $fullName,
                'phone_number' => $row['phone_number'] ?? 'N/A',
                'email' => $row['email'] ?? 'N/A',
                'password' => $row['password'] ?? 'N/A',
                'created_by' => $row['created_by'] ?? 'N/A',
                'partner_type' => $row['partner_type_name'] ?? 'N/A',
                'status' => $row['status'] ?? 'Active',
                'created_date' => $row['created_date'] ?? date('Y-m-d H:i:s')
            );
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $partners,
        'message' => 'Partners fetched successfully',
        'count' => count($partners)
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