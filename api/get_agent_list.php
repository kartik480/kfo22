<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration - Production Server
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check if tbl_agent_data table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'tbl_agent_data'");
    if ($stmt->rowCount() == 0) {
        // Table doesn't exist, return empty data
        echo json_encode([
            'status' => 'success',
            'message' => 'No agents found - tbl_agent_data table does not exist',
            'data' => []
        ]);
        exit();
    }
    
    // Fetch all agents with their details using JOINs to get readable names
    $selectSQL = "SELECT 
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
    WHERE ad.status = 'Active' OR ad.status IS NULL
    ORDER BY ad.created_at DESC";
    
    $result = $pdo->query($selectSQL);
    
    if (!$result) {
        throw new Exception("Error fetching agent data");
    }
    
    $agents = [];
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $agents[] = [
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
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Agent data fetched successfully',
        'data' => $agents
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 