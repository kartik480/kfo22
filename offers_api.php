<?php
// Set headers for API response
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$username = "emp_kfinone";
$password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database connection failed: ' . $conn->connect_error
    ]);
    exit();
}

// Set charset to utf8
$conn->set_charset("utf8");

try {
    // SQL query to fetch offers
    $sql = "SELECT id, name, image, status FROM tbl_latest_offers ORDER BY id DESC";
    $result = $conn->query($sql);
    
    if ($result === false) {
        throw new Exception("Query failed: " . $conn->error);
    }
    
    $offers = [];
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $offers[] = [
                'id' => (int)$row['id'],
                'name' => $row['name'],
                'image' => $row['image'],
                'status' => $row['status']
            ];
        }
    }
    
    // Return success response
    echo json_encode([
        'success' => true,
        'message' => 'Offers fetched successfully',
        'data' => $offers,
        'count' => count($offers)
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
} finally {
    // Close connection
    $conn->close();
}
?>
