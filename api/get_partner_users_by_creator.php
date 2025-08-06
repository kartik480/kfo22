<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get the creator ID from request parameters (optional)
    $creatorId = isset($_GET['createdBy']) ? $_GET['createdBy'] : null;
    
    if ($creatorId) {
        // Query to fetch partner users for a specific creator
        $query = "SELECT 
                    id,
                    createdBy
                  FROM tbl_partner_users
                  WHERE createdBy = :createdBy";
        
        $stmt = $pdo->prepare($query);
        $stmt->bindParam(':createdBy', $creatorId, PDO::PARAM_INT);
        $stmt->execute();
        
        $partnerUsers = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Return success response for specific creator
        echo json_encode([
            'success' => true,
            'message' => 'Partner users for creator ID ' . $creatorId . ' fetched successfully',
            'creator_id' => $creatorId,
            'users' => $partnerUsers,
            'count' => count($partnerUsers)
        ]);
        
    } else {
        // Query to fetch all partner users grouped by createdBy
        $query = "SELECT 
                    createdBy,
                    COUNT(id) as user_count
                  FROM tbl_partner_users
                  WHERE createdBy IS NOT NULL
                  GROUP BY createdBy
                  ORDER BY user_count DESC, createdBy ASC";
        
        $stmt = $pdo->prepare($query);
        $stmt->execute();
        
        $creatorGroups = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Get detailed user lists for each creator
        $detailedResults = [];
        foreach ($creatorGroups as $group) {
            $creatorId = $group['createdBy'];
            
                         // Get creator details (simplified - just basic info)
             $creatorQuery = "SELECT 
                               id,
                               username
                             FROM tbl_user
                             WHERE id = :creatorId";
            
            $creatorStmt = $pdo->prepare($creatorQuery);
            $creatorStmt->bindParam(':creatorId', $creatorId, PDO::PARAM_INT);
            $creatorStmt->execute();
            $creatorInfo = $creatorStmt->fetch(PDO::FETCH_ASSOC);
            
            // Get partner users for this creator
            $usersQuery = "SELECT 
                            id,
                            username,
                            firstName,
                            lastName,
                            email,
                            phone,
                            status,
                            created_at,
                            updated_at,
                            CONCAT(firstName, ' ', lastName) as fullName,
                            CONCAT(firstName, ' ', lastName, ' (', status, ')') as displayName
                          FROM tbl_partner_users
                          WHERE createdBy = :createdBy
                          ORDER BY firstName ASC, lastName ASC";
            
            $usersStmt = $pdo->prepare($usersQuery);
            $usersStmt->bindParam(':createdBy', $creatorId, PDO::PARAM_INT);
            $usersStmt->execute();
            $users = $usersStmt->fetchAll(PDO::FETCH_ASSOC);
            
            $detailedResults[] = [
                'creator_id' => $creatorId,
                'creator_info' => $creatorInfo,
                'statistics' => $group,
                'users' => $users
            ];
        }
        
        // Calculate overall statistics
        $totalCreators = count($detailedResults);
        $totalUsers = array_sum(array_column($creatorGroups, 'user_count'));
        $totalActive = array_sum(array_column($creatorGroups, 'active_count'));
        $totalInactive = array_sum(array_column($creatorGroups, 'inactive_count'));
        
        // Return success response for all creators
        echo json_encode([
            'success' => true,
            'message' => 'Partner users grouped by creator fetched successfully',
            'overall_statistics' => [
                'total_creators' => $totalCreators,
                'total_users' => $totalUsers,
                'total_active' => $totalActive,
                'total_inactive' => $totalInactive
            ],
            'creators' => $detailedResults,
            'count' => $totalCreators
        ]);
    }
    
} catch (PDOException $e) {
    // Database error
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
    
} catch (Exception $e) {
    // General error
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}
?> 