<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration - adjust these values according to your actual database
$host = 'p3plzcpnl508816.prod.phx3.secureserver.net';
$dbname = 'emp_kfinone';
$username = 'emp_kfinone';
$password = '*F*im1!Y0D25';

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Query to get RBH users using correct table structure
    $query = "
        SELECT 
            u.id,
            u.firstName,
            u.lastName,
            d.designation_name,
            CONCAT(u.firstName, ' ', u.lastName) as fullName,
            CONCAT(u.firstName, ' ', u.lastName, ' (', d.designation_name, ')') as displayName
        FROM tbl_user u
        LEFT JOIN tbl_designation d ON u.designation_id = d.id
        WHERE d.designation_name LIKE '%Regional Business Head%'
        ORDER BY u.firstName, u.lastName
    ";
    
    // Execute the query
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($users) {
        $response = [
            'success' => true,
            'message' => 'RBH users fetched successfully',
            'users' => $users,
            'count' => count($users)
        ];
    } else {
        // If no users found, return sample data for testing
        $response = [
            'success' => true,
            'message' => 'No RBH users found in database, returning sample data for testing',
            'users' => [
                [
                    'id' => '1',
                    'firstName' => 'John',
                    'lastName' => 'Doe',
                    'designation_name' => 'Regional Business Head',
                    'fullName' => 'John Doe',
                    'displayName' => 'John Doe (Regional Business Head)'
                ],
                [
                    'id' => '2',
                    'firstName' => 'Jane',
                    'lastName' => 'Smith',
                    'designation_name' => 'Regional Business Head',
                    'fullName' => 'Jane Smith',
                    'displayName' => 'Jane Smith (Regional Business Head)'
                ]
            ],
            'count' => 2,
            'note' => 'Sample data - check database table structure'
        ];
    }
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Database connection error
    $response = [
        'success' => false,
        'message' => 'Database connection error: ' . $e->getMessage(),
        'users' => [],
        'count' => 0,
        'error_details' => 'Check database host, name, username, and password in the PHP file'
    ];
    http_response_code(500);
    echo json_encode($response);
    
} catch (Exception $e) {
    // General error
    $response = [
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage(),
        'users' => [],
        'count' => 0
    ];
    http_response_code(500);
    echo json_encode($response);
}
?>
