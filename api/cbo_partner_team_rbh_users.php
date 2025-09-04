<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db_config.php';

try {
    $conn = getConnection();
    
    // Query to fetch partner users created by Regional Business Head users
    $query = "
        SELECT DISTINCT
            pu.id,
            pu.username,
            pu.first_name,
            pu.last_name,
            pu.createdBy,
            creator.firstName,
            creator.lastName,
            d.designation_name
        FROM tbl_partner_users pu
        LEFT JOIN tbl_user creator ON pu.createdBy = creator.username
        LEFT JOIN tbl_designation d ON creator.designation_id = d.id
        WHERE d.designation_name = 'Regional Business Head'
        ORDER BY pu.first_name, pu.last_name
    ";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (empty($result)) {
        echo json_encode([
            'success' => true,
            'message' => 'No partner users found created by Regional Business Head users',
            'data' => []
        ]);
        exit;
    }
    
    // Format the response with partner users created by RBH
    $formattedData = [];
    foreach ($result as $row) {
        $formattedData[] = [
            'id' => $row['id'],
            'username' => $row['username'],
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'full_name' => trim($row['first_name'] . ' ' . $row['last_name']),
            'createdBy' => $row['createdBy'],
            'creator_name' => trim($row['firstName'] . ' ' . $row['lastName']),
            'creator_designation' => $row['designation_name']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Partner users created by Regional Business Head users fetched successfully',
        'data' => $formattedData,
        'count' => count($formattedData)
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>
