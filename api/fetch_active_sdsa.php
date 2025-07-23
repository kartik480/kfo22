<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn || !$conn->ping()) {
        throw new Exception("Database connection not available");
    }

    // Get CBO and RBH designation IDs
    $designationSql = "SELECT id FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head')";
    $designationResult = $conn->query($designationSql);
    $targetDesignationIds = array();
    if ($designationResult && $designationResult->num_rows > 0) {
        while($row = $designationResult->fetch_assoc()) {
            $targetDesignationIds[] = $row['id'];
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

    // Main query: fetch all SDSA users reporting to CBO or RBH
    $sql = "SELECT s.*, u.firstName AS reporting_firstName, u.lastName AS reporting_lastName, d.designation_name AS reporting_designation_name
            FROM tbl_sdsa_users s
            LEFT JOIN tbl_user u ON s.reportingTo = u.id
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE u.designation_id IN ($designationPlaceholders)
            AND (s.status = 'Active' OR s.status = 1 OR s.status IS NULL OR s.status = '')
            ORDER BY s.first_name ASC, s.last_name ASC";

    $result = $conn->query($sql);
    if (!$result) {
        throw new Exception("Query failed: " . $conn->error);
    }

    $data = array();
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $data[] = $row;
        }
        echo json_encode(array(
            'status' => 'success',
            'message' => 'SDSA users reporting to CBO or RBH fetched successfully',
            'data' => $data,
            'count' => count($data)
        ));
    } else {
        echo json_encode(array(
            'status' => 'success',
            'message' => 'No SDSA users found reporting to CBO or RBH',
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