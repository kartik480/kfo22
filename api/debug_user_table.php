<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // 1. Show table structure
    $structureQuery = "DESCRIBE tbl_user";
    $stmt = $pdo->prepare($structureQuery);
    $stmt->execute();
    $tableStructure = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // 2. Show sample user data (first 5 users)
    $sampleQuery = "SELECT * FROM tbl_user LIMIT 5";
    $stmt = $pdo->prepare($sampleQuery);
    $stmt->execute();
    $sampleUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // 3. Count total users
    $countQuery = "SELECT COUNT(*) as total_users FROM tbl_user";
    $stmt = $pdo->prepare($countQuery);
    $stmt->execute();
    $userCount = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // 4. Show unique values in key columns (to see what designations/roles exist)
    $uniqueQuery = "
        SELECT 
            'designation' as column_name,
            GROUP_CONCAT(DISTINCT designation ORDER BY designation SEPARATOR ', ') as unique_values
        FROM tbl_user 
        WHERE designation IS NOT NULL AND designation != ''
        UNION ALL
        SELECT 
            'role' as column_name,
            GROUP_CONCAT(DISTINCT role ORDER BY role SEPARATOR ', ') as unique_values
        FROM tbl_user 
        WHERE role IS NOT NULL AND role != ''
        UNION ALL
        SELECT 
            'status' as column_name,
            GROUP_CONCAT(DISTINCT status ORDER BY status SEPARATOR ', ') as unique_values
        FROM tbl_user 
        WHERE status IS NOT NULL AND status != ''
    ";
    
    $stmt = $pdo->prepare($uniqueQuery);
    $stmt->execute();
    $uniqueValues = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'message' => 'User table structure and data retrieved successfully',
        'data' => [
            'table_structure' => $tableStructure,
            'sample_users' => $sampleUsers,
            'user_count' => $userCount,
            'unique_values' => $uniqueValues
        ]
    ], JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?>
