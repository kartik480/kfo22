<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database connection failed: ' . $e->getMessage()]);
    exit;
}

try {
    // Get agents with creator information using JOIN
    $query = "SELECT 
                a.id,
                a.full_name,
                a.company_name,
                a.Phone_number,
                a.alternative_Phone_number,
                a.email_id,
                a.partnerType,
                a.state,
                a.location,
                a.address,
                a.visiting_card,
                a.created_user,
                a.createdBy,
                a.status,
                a.created_at,
                a.updated_at,
                u.firstName as creator_first_name,
                u.lastName as creator_last_name,
                u.username as creator_username,
                CONCAT(u.firstName, ' ', u.lastName) as creator_full_name,
                COALESCE(d.designation_name, 'User') as creator_designation_name
              FROM tbl_agent_data a
              LEFT JOIN tbl_user u ON a.createdBy = u.username
              LEFT JOIN tbl_designation d ON u.designation_id = d.id
              WHERE u.status = 'active' OR u.status IS NULL
              ORDER BY a.created_at DESC
              LIMIT 200";

    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Return direct JSON array
    echo json_encode($agents, JSON_PRETTY_PRINT);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Server error: ' . $e->getMessage()
    ]);
}
?>
