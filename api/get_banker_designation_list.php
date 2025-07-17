<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    // Query to get banker designations
    $query = "SELECT id, designation_name FROM tbl_banker_designation ORDER BY designation_name ASC";
    $result = $conn->query($query);
    
    if ($result) {
        $data = array();
        while ($row = $result->fetch_assoc()) {
            $data[] = array(
                'id' => $row['id'],
                'designation_name' => $row['designation_name']
            );
        }
        
        echo json_encode(array(
            'success' => true,
            'data' => $data,
            'count' => count($data)
        ));
    } else {
        throw new Exception('Failed to fetch banker designations: ' . $conn->error);
    }
    
} catch (Exception $e) {
    echo json_encode(array(
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ));
}

$conn->close();
?> 