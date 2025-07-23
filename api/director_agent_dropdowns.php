<?php
header('Content-Type: application/json');
require_once 'db_config.php'; // Assumes you have a db_connect.php for DB connection

$response = [
    'partner_types' => [],
    'branch_states' => [],
    'branch_locations' => []
];

// Partner Types
$pt_query = "SELECT id, partner_type FROM tbl_partner_type ORDER BY partner_type ASC";
$pt_result = mysqli_query($conn, $pt_query);
if ($pt_result) {
    while ($row = mysqli_fetch_assoc($pt_result)) {
        $response['partner_types'][] = [
            'id' => $row['id'],
            'name' => $row['partner_type']
        ];
    }
}

// Branch States
$bs_query = "SELECT id, branch_state_name FROM tbl_branch_state ORDER BY branch_state_name ASC";
$bs_result = mysqli_query($conn, $bs_query);
if ($bs_result) {
    while ($row = mysqli_fetch_assoc($bs_result)) {
        $response['branch_states'][] = [
            'id' => $row['id'],
            'name' => $row['branch_state_name']
        ];
    }
}

// Branch Locations
$bl_query = "SELECT id, branch_location FROM tbl_branch_location ORDER BY branch_location ASC";
$bl_result = mysqli_query($conn, $bl_query);
if ($bl_result) {
    while ($row = mysqli_fetch_assoc($bl_result)) {
        $response['branch_locations'][] = [
            'id' => $row['id'],
            'name' => $row['branch_location']
        ];
    }
}

// Output JSON
http_response_code(200);
echo json_encode($response); 