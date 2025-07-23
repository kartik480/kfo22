<?php
header('Content-Type: application/json');
// Use your DB connection file or credentials
require_once 'db_connect.php'; // or use your DB connection code

$response = array();

// Adjust the table name and filter as per your schema
// If SDSA team is in tbl_user with a designation or role, filter accordingly
$sql = "
    SELECT fullName, phoneNumber, emailId, password, id, reportingTo
    FROM tbl_sdsa_team
    WHERE status = 'active'";

$result = $conn->query($sql);

if ($result) {
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
    $response['status'] = 'success';
    $response['data'] = $data;
} else {
    $response['status'] = 'error';
    $response['message'] = 'Query failed: ' . $conn->error;
}

echo json_encode($response);
$conn->close(); 