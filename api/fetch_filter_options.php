<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Include database configuration
require_once 'db_config.php';

try {
    $conn = getConnection();
    
    $filterOptions = array();
    
    // Fetch states from tbl_branch_state
    $stateSql = "SELECT id, branch_state_name FROM tbl_branch_state ORDER BY branch_state_name ASC";
    $stateStmt = $conn->prepare($stateSql);
    $stateStmt->execute();
    $states = $stateStmt->fetchAll(PDO::FETCH_ASSOC);
    $filterOptions['states'] = $states;
    
    // Fetch locations from tbl_branch_location
    $locationSql = "SELECT id, branch_location FROM tbl_branch_location ORDER BY branch_location ASC";
    $locationStmt = $conn->prepare($locationSql);
    $locationStmt->execute();
    $locations = $locationStmt->fetchAll(PDO::FETCH_ASSOC);
    $filterOptions['locations'] = $locations;
    
    // Fetch partner types from tbl_partner_type
    $partnerTypeSql = "SELECT id, partner_type FROM tbl_partner_type ORDER BY partner_type ASC";
    $partnerTypeStmt = $conn->prepare($partnerTypeSql);
    $partnerTypeStmt->execute();
    $partnerTypes = $partnerTypeStmt->fetchAll(PDO::FETCH_ASSOC);
    $filterOptions['partner_types'] = $partnerTypes;
    
    // Return success response
    echo json_encode(array(
        'success' => true,
        'message' => 'Filter options fetched successfully',
        'data' => $filterOptions
    ));
    
} catch (Exception $e) {
    error_log("Error in fetch_filter_options.php: " . $e->getMessage());
    echo json_encode(array(
        'success' => false,
        'message' => 'Error fetching filter options: ' . $e->getMessage()
    ));
} finally {
    if (isset($conn)) {
        closeConnection($conn);
    }
}
?> 