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
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }

    // Get the IDs for CBO and RBO designations
    $designationSql = "SELECT id, designation_name FROM tbl_designation WHERE designation_name IN ('Chief Business Officer', 'Regional Business Head')";
    $designationResult = $conn->query($designationSql);
    $managerDesignationIds = array();
    $designationIdToName = array();
    if ($designationResult && $designationResult->num_rows > 0) {
        while($row = $designationResult->fetch_assoc()) {
            $managerDesignationIds[] = $row['id'];
            $designationIdToName[$row['id']] = $row['designation_name'];
        }
    }
    if (empty($managerDesignationIds)) {
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No CBO or RBO designations found',
            'count' => 0
        ));
        exit;
    }
    $designationPlaceholders = implode(',', $managerDesignationIds);

    // Get all managers from tbl_sdsa_users with CBO or RBO designation (by id)
    $managerSql = "SELECT id, first_name, last_name, designation FROM tbl_sdsa_users WHERE designation IN ($designationPlaceholders) AND (status = 'Active' OR status = 1 OR status IS NULL OR status = '') AND first_name IS NOT NULL AND first_name != ''";
    $managerResult = $conn->query($managerSql);
    $managerIds = array();
    $managerInfo = array();
    if ($managerResult && $managerResult->num_rows > 0) {
        while($row = $managerResult->fetch_assoc()) {
            $managerIds[] = $row['id'];
            $managerInfo[$row['id']] = array(
                'first_name' => $row['first_name'],
                'last_name' => $row['last_name'],
                'designation_id' => $row['designation'],
                'designation_name' => isset($designationIdToName[$row['designation']]) ? $designationIdToName[$row['designation']] : ''
            );
        }
    }
    if (empty($managerIds)) {
        echo json_encode(array(
            'status' => 'success',
            'data' => array(),
            'message' => 'No CBO or RBO managers found in tbl_sdsa_users',
            'count' => 0
        ));
        exit;
    }
    $managerPlaceholders = implode(',', array_fill(0, count($managerIds), '?'));

    // Prepare statement to get SDSA users who report to these managers, join designation for user
    $sql = "SELECT s.id, s.username, s.alias_name, s.first_name, s.last_name, s.Phone_number, s.email_id, s.alternative_mobile_number, s.company_name, s.branch_state_name_id, s.branch_location_id, s.status, s.reportingTo, s.employee_no, s.department, s.designation, d.designation_name, s.office_address, s.residential_address, s.pan_number, s.aadhaar_number, s.created_at, s.updated_at FROM tbl_sdsa_users s LEFT JOIN tbl_designation d ON s.designation = d.id WHERE s.reportingTo IN ($managerPlaceholders) AND (s.status = 'Active' OR s.status = 1 OR s.status IS NULL OR s.status = '') AND s.first_name IS NOT NULL AND s.first_name != '' ORDER BY s.first_name ASC, s.last_name ASC";
    $stmt = $conn->prepare($sql);
    $types = str_repeat('s', count($managerIds));
    $stmt->bind_param($types, ...$managerIds);
    $stmt->execute();
    $result = $stmt->get_result();

    $users = array();
    if ($result && $result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $managerId = $row['reportingTo'];
            $managerName = isset($managerInfo[$managerId]) ? trim($managerInfo[$managerId]['first_name'] . ' ' . $managerInfo[$managerId]['last_name']) : '';
            $managerDesignation = isset($managerInfo[$managerId]) ? $managerInfo[$managerId]['designation_name'] : '';
            $users[] = array(
                'id' => $row['id'],
                'username' => $row['username'],
                'alias_name' => $row['alias_name'],
                'first_name' => $row['first_name'],
                'last_name' => $row['last_name'],
                'fullName' => trim($row['first_name'] . ' ' . $row['last_name']),
                'mobile' => $row['Phone_number'],
                'email_id' => $row['email_id'],
                'alternative_mobile_number' => $row['alternative_mobile_number'],
                'company_name' => $row['company_name'],
                'branch_state_name_id' => $row['branch_state_name_id'],
                'branch_location_id' => $row['branch_location_id'],
                'status' => $row['status'],
                'reportingTo' => $row['reportingTo'],
                'employee_no' => $row['employee_no'],
                'department' => $row['department'],
                'designation_id' => $row['designation'],
                'designation_name' => $row['designation_name'],
                'office_address' => $row['office_address'],
                'residential_address' => $row['residential_address'],
                'pan_number' => $row['pan_number'],
                'aadhaar_number' => $row['aadhaar_number'],
                'managerName' => $managerName,
                'managerDesignation' => $managerDesignation,
                'created_at' => $row['created_at'],
                'updated_at' => $row['updated_at']
            );
        }
    }
    echo json_encode(array(
        'status' => 'success',
        'data' => $users,
        'message' => 'SDSA users reporting to CBO or RBO (from tbl_sdsa_users) fetched successfully',
        'count' => count($users)
    ));
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to fetch SDSA users: ' . $e->getMessage(),
        'error_type' => get_class($e),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ));
}
?> 