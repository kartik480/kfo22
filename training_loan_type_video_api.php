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
    echo json_encode(["success" => false, "message" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

// Set character set to UTF-8
$conn->set_charset("utf8mb4");

// Fetch data from tbl_training_loan_type_video with JOINs to get vendor bank name and loan type name
$sql = "SELECT 
    tltv.id,
    tltv.video_name,
    tltv.video_image,
    tltv.video,
    tltv.vendor_bank_id,
    tltv.loan_type_id,
    vb.vendor_bank_name,
    lt.loan_type
FROM tbl_training_loan_type_video tltv
LEFT JOIN tbl_vendor_bank vb ON tltv.vendor_bank_id = vb.id
LEFT JOIN tbl_loan_type lt ON tltv.loan_type_id = lt.id
ORDER BY tltv.id DESC";

$result = $conn->query($sql);

$videos = [];
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $videos[] = $row;
    }
}

$conn->close();

echo json_encode([
    "success" => true,
    "message" => "Training loan type videos fetched successfully",
    "data" => $videos,
    "count" => count($videos)
]);
?>
