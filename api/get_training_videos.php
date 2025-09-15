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

    // Get optional filter parameters
    $categoryId = isset($_GET['category_id']) ? (int)$_GET['category_id'] : null;
    
    $sql = "SELECT tv.name, vc.category_name as category, tv.video_url 
            FROM tbl_training_videos tv 
            LEFT JOIN tbl_training_video_category vc ON tv.category_id = vc.id";
    
    $params = [];
    if ($categoryId && $categoryId > 0) {
        $sql .= " WHERE tv.category_id = ?";
        $params[] = $categoryId;
    }
    
    $sql .= " ORDER BY tv.name";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $videos = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($videos);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>
