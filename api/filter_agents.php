<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get filter parameters from POST data
    $input = json_decode(file_get_contents('php://input'), true);
    
    $stateId = isset($input['state_id']) ? $input['state_id'] : '0';
    $locationId = isset($input['location_id']) ? $input['location_id'] : '0';
    $partnerTypeId = isset($input['partner_type_id']) ? $input['partner_type_id'] : '0';
    
    // Build the SQL query with JOINs to get actual names
    $sql = "SELECT 
                ad.id,
                ad.full_name,
                ad.company_name,
                ad.Phone_number,
                ad.alternative_Phone_number,
                ad.email_id,
                pt.partner_type as partnerType,
                bs.branch_state_name as state,
                bl.branch_location as location,
                ad.address,
                ad.visiting_card,
                ad.created_user,
                ad.createdBy,
                ad.status,
                ad.created_at,
                ad.updated_at
            FROM tbl_agent_data ad
            LEFT JOIN tbl_partner_type pt ON ad.partnerType = pt.id
            LEFT JOIN tbl_branch_state bs ON ad.state = bs.id
            LEFT JOIN tbl_branch_location bl ON ad.location = bl.id
            WHERE 1=1";
    
    $params = array();
    
    // Add filter conditions
    if ($stateId != '0') {
        $sql .= " AND ad.state = ?";
        $params[] = $stateId;
    }
    
    if ($locationId != '0') {
        $sql .= " AND ad.location = ?";
        $params[] = $locationId;
    }
    
    if ($partnerTypeId != '0') {
        $sql .= " AND ad.partnerType = ?";
        $params[] = $partnerTypeId;
    }
    
    $sql .= " ORDER BY ad.full_name ASC";
    
    // Prepare and execute the statement
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    
    $agents = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $agents[] = array(
            'id' => $row['id'],
            'full_name' => $row['full_name'],
            'company_name' => $row['company_name'],
            'Phone_number' => $row['Phone_number'],
            'alternative_Phone_number' => $row['alternative_Phone_number'],
            'email_id' => $row['email_id'],
            'partnerType' => $row['partnerType'],
            'state' => $row['state'],
            'location' => $row['location'],
            'address' => $row['address'],
            'visiting_card' => $row['visiting_card'],
            'created_user' => $row['created_user'],
            'createdBy' => $row['createdBy'],
            'status' => $row['status'],
            'created_at' => $row['created_at'],
            'updated_at' => $row['updated_at']
        );
    }
    
    // Return success response
    echo json_encode(array(
        'status' => 'success',
        'message' => 'Agents fetched successfully',
        'data' => $agents,
        'count' => count($agents)
    ));
    
} catch (Exception $e) {
    error_log("Error in filter_agents.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Error fetching agents: ' . $e->getMessage()
    ));
}
?> 