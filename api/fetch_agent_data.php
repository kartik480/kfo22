<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Include database configuration
require_once 'db_config_fixed.php';

try {
    $conn = getConnection();
    
    // Prepare and execute the query to get all agent data from tbl_agent_data
    $stmt = $conn->prepare("SELECT 
        full_name,
        company_name,
        Phone_number,
        alternative_Phone_number,
        email_id,
        partnerType,
        state,
        location,
        address,
        visiting_card,
        created_user,
        createdBy
        FROM tbl_agent_data 
        ORDER BY full_name ASC");
    $stmt->execute();
    
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Return the list of agents as a JSON response
    echo json_encode(array(
        'success' => true,
        'message' => 'Agent data fetched successfully.',
        'data' => $agents,
        'count' => count($agents)
    ));
    
    closeConnection($conn);
    
} catch (Exception $e) {
    // Log the error and return a generic error message
    error_log("Fetch agent data error: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'success' => false, 
        'message' => 'An error occurred while fetching agent data.'
    ));
}
?> 