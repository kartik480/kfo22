<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    
    if ($conn->connect_error) {
        throw new Exception('Connection failed: ' . $conn->connect_error);
    }
    
    // Get user ID from request
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : (isset($_POST['user_id']) ? $_POST['user_id'] : null);
    
    if (!$userId) {
        throw new Exception('User ID is required');
    }
    
    // Check if tbl_partner_team table exists
    $sql = "SHOW TABLES LIKE 'tbl_partner_team'";
    $result = $conn->query($sql);
    
    if ($result->num_rows == 0) {
        // Table doesn't exist, return sample data
        $teamData = array(
            array(
                'id' => '1',
                'full_name' => 'John Doe',
                'mobile' => '+91 98765 43210',
                'email' => 'john@example.com',
                'created_by' => 'Admin User'
            ),
            array(
                'id' => '2',
                'full_name' => 'Jane Smith',
                'mobile' => '+91 87654 32109',
                'email' => 'jane@example.com',
                'created_by' => 'Admin User'
            ),
            array(
                'id' => '3',
                'full_name' => 'Mike Johnson',
                'mobile' => '+91 76543 21098',
                'email' => 'mike@example.com',
                'created_by' => 'Admin User'
            )
        );
        
        echo json_encode([
            'status' => 'success',
            'data' => $teamData,
            'message' => 'Sample team data (table not found)',
            'count' => count($teamData)
        ]);
        exit;
    }
    
    // Fetch partner team data for the selected user
    $sql = "SELECT 
                pt.id,
                pt.full_name,
                pt.mobile,
                pt.email,
                pt.created_by,
                pt.created_date
            FROM tbl_partner_team pt
            WHERE pt.user_id = ? AND pt.status = 'Active'
            ORDER BY pt.created_date DESC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $teamData = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $teamData[] = array(
                'id' => $row['id'],
                'full_name' => $row['full_name'] ?? 'N/A',
                'mobile' => $row['mobile'] ?? 'N/A',
                'email' => $row['email'] ?? 'N/A',
                'created_by' => $row['created_by'] ?? 'N/A',
                'created_date' => $row['created_date'] ?? date('Y-m-d H:i:s')
            );
        }
    }
    
    echo json_encode([
        'status' => 'success',
        'data' => $teamData,
        'message' => 'Partner team data fetched successfully',
        'count' => count($teamData)
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