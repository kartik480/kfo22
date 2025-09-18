<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'db_config.php';

try {
    $pdo = getConnection();

    // Get pagination parameters
    $page = isset($_GET['page']) ? max(1, intval($_GET['page'])) : 1;
    $limit = isset($_GET['limit']) ? min(100, max(10, intval($_GET['limit']))) : 50; // Default 50, max 100
    $offset = ($page - 1) * $limit;

    // First, get total count
    $countSql = "SELECT COUNT(*) as total FROM tbl_agent_data";
    $countStmt = $pdo->prepare($countSql);
    $countStmt->execute();
    $totalCount = $countStmt->fetch(PDO::FETCH_ASSOC)['total'];

    // Fetch agents with pagination - ONLY the exact columns from tbl_agent_data
    $sql = "
        SELECT
            id,
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
            createdBy,
            status,
            created_at,
            updated_at
        FROM tbl_agent_data
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :offset
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
    $stmt->bindParam(':offset', $offset, PDO::PARAM_INT);
    $stmt->execute();
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $totalPages = ceil($totalCount / $limit);

    echo json_encode([
        'success' => true,
        'message' => 'Agents fetched successfully',
        'agents' => $agents,
        'pagination' => [
            'current_page' => $page,
            'total_pages' => $totalPages,
            'total_count' => $totalCount,
            'limit' => $limit,
            'has_next' => $page < $totalPages,
            'has_prev' => $page > 1
        ]
    ]);

} catch (PDOException $e) {
    error_log("Database error in get_all_agents_data.php: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'Database error occurred',
        'error' => $e->getMessage()
    ]);
} catch (Exception $e) {
    error_log("General error in get_all_agents_data.php: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'An error occurred while fetching agents',
        'error' => $e->getMessage()
    ]);
}
?>
