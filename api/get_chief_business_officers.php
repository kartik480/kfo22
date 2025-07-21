<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

header('Content-Type: application/json');

// Database connection (GoDaddy hosting credentials)
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

$conn = new mysqli($servername, $db_username, $db_password, $dbname);
if ($conn->connect_error) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $conn->connect_error
    ]);
    exit();
}

$response = array();

$sql = "
    SELECT u.id, u.firstName, u.lastName, d.designation_name
    FROM tbl_user u
    JOIN tbl_designation d ON u.designation_id = d.id
    WHERE d.designation_name = 'Chief Business Officer'";

$result = $conn->query($sql);

if ($result === false) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Query failed: ' . $conn->error
    ]);
    $conn->close();
    exit();
}

$users = array();
while ($row = $result->fetch_assoc()) {
    $users[] = array(
        'id' => $row['id'],
        'firstName' => $row['firstName'],
        'lastName' => $row['lastName'],
        'designation_name' => $row['designation_name']
    );
}

$response['status'] = 'success';
$response['data'] = $users;

echo json_encode($response);
$conn->close(); 