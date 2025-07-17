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
    
    // Get manager ID from query parameter
    $managerId = isset($_GET['managerId']) ? $_GET['managerId'] : '';
    
    if (empty($managerId)) {
        throw new Exception("Manager ID is required");
    }
    
    // Simple query to fetch team members from tbl_sdsa_users who report to the selected manager
    $sql = "SELECT 
                s.id,
                s.first_name,
                s.last_name,
                s.Phone_number as mobile,
                s.email_id,
                s.reportingTo
            FROM tbl_sdsa_users s
            WHERE s.reportingTo = ?
            ORDER BY s.first_name ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $managerId);
    
    $stmt->execute();
    $result = $stmt->get_result();
    
    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $data = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $data[] = array(
                'id' => $row['id'],
                'fullName' => $row['first_name'] . ' ' . $row['last_name'],
                'mobile' => $row['mobile'],
                'email' => $row['email_id'],
                'reportingTo' => $row['reportingTo']
            );
        }
        
        echo json_encode(array(
            'status' => 'success',
            'message' => 'SDSA team members fetched successfully',
            'data' => $data,
            'count' => count($data)
        ));
    } else {
        echo json_encode(array(
            'status' => 'success',
            'message' => 'No SDSA team members found',
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