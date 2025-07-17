<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Use the existing connection from db_config.php
    global $conn;
    
    // Check if connection exists and is valid
    if (!$conn || !$conn->ping()) {
        throw new Exception("Database connection not available");
    }
    
    // Query to fetch SDSA users who report to ID 8
    $sql = "SELECT 
                s.id,
                s.first_name,
                s.last_name,
                s.Phone_number,
                s.email_id,
                s.password,
                s.reportingTo
            FROM tbl_sdsa_users s
            WHERE s.reportingTo = '8'
            ORDER BY s.first_name ASC";
    
    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $data = array();
    
    if ($result->num_rows > 0) {
                                while($row = $result->fetch_assoc()) {
                            $data[] = array(
                                'id' => $row['id'],
                                'fullName' => $row['first_name'] . ' ' . $row['last_name'],
                                'phoneNumber' => $row['Phone_number'],
                                'emailId' => $row['email_id'],
                                'password' => $row['password'],
                                'reportingTo' => $row['reportingTo']
                            );
                        }
        
        echo json_encode(array(
            'status' => 'success',
            'message' => 'Users reporting to ID 8 fetched successfully',
            'data' => $data,
            'count' => count($data)
        ));
    } else {
        echo json_encode(array(
            'status' => 'success',
            'message' => 'No users found reporting to ID 8',
            'data' => array(),
            'count' => 0
        ));
    }
    
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 