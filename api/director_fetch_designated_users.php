<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    // Get CBO and RBH designation IDs
    $designationSql = "SELECT id, designation_name FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head')";
    $designationResult = $conn->query($designationSql);
    $targetDesignationIds = array();
    $designationIdToName = array();
    if ($designationResult && $designationResult->num_rows > 0) {
        while($row = $designationResult->fetch_assoc()) {
            $targetDesignationIds[] = $row['id'];
            $designationIdToName[$row['id']] = $row['designation_name'];
        }
    }
    if (empty($targetDesignationIds)) {
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No CBO or RBH designations found',
            'count' => 0
        ));
        exit;
    }
    $designationPlaceholders = implode(',', $targetDesignationIds);
    $userSql = "SELECT u.id, u.firstName, u.lastName, u.designation_id, d.designation_name FROM tbl_user u INNER JOIN tbl_designation d ON u.designation_id = d.id WHERE u.designation_id IN ($designationPlaceholders) AND (u.status = 'Active' OR u.status = 1) AND u.firstName IS NOT NULL AND u.firstName != '' ORDER BY u.firstName ASC, u.lastName ASC";
    $userResult = $conn->query($userSql);
    $users = array();
    if ($userResult && $userResult->num_rows > 0) {
        while($row = $userResult->fetch_assoc()) {
            $users[] = array(
                'id' => $row['id'],
                'fullName' => trim($row['firstName'] . ' ' . $row['lastName']),
                'designation_name' => $row['designation_name']
            );
        }
    }
    echo json_encode(array(
        'status' => 'success',
        'data' => $users,
        'message' => 'CBO and RBH users fetched successfully',
        'count' => count($users)
    ));
} catch (Exception $e) {
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ));
}
?> 