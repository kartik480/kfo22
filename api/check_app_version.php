<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

try {
    // Database connection
    $host = 'localhost';
    $dbname = 'kfinone_db';
    $username = 'root';
    $password = '';
    
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get the latest version information from database
    $query = "SELECT * FROM app_versions ORDER BY version_code DESC LIMIT 1";
    $stmt = $pdo->prepare($query);
    $stmt->execute();
    $version = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($version) {
        $response = [
            'status' => true,
            'message' => 'Version information retrieved successfully',
            'data' => [
                'version_code' => (int)$version['version_code'],
                'version_name' => $version['version_name'],
                'update_message' => $version['update_message'],
                'force_update' => (bool)$version['force_update'],
                'download_url' => $version['download_url'] ?: 'https://play.google.com/store/apps/details?id=com.kfinone.app',
                'release_date' => $version['release_date'],
                'min_required_version' => (int)$version['min_required_version']
            ]
        ];
    } else {
        // Fallback to default version info if no database record
        $response = [
            'status' => true,
            'message' => 'Using default version information',
            'data' => [
                'version_code' => 2, // Increment this when you release a new version
                'version_name' => '1.0.1',
                'update_message' => 'New version available with improved performance and bug fixes.',
                'force_update' => false,
                'download_url' => 'https://play.google.com/store/apps/details?id=com.kfinone.app',
                'release_date' => date('Y-m-d H:i:s'),
                'min_required_version' => 1
            ]
        ];
    }
    
    echo json_encode($response);
    
} catch (PDOException $e) {
    // Fallback response if database connection fails
    $response = [
        'status' => true,
        'message' => 'Using fallback version information',
        'data' => [
            'version_code' => 2, // Increment this when you release a new version
            'version_name' => '1.0.1',
            'update_message' => 'New version available with improved performance and bug fixes.',
            'force_update' => false,
            'download_url' => 'https://play.google.com/store/apps/details?id=com.kfinone.app',
            'release_date' => date('Y-m-d H:i:s'),
            'min_required_version' => 1
        ]
    ];
    
    echo json_encode($response);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => false,
        'message' => 'Internal server error: ' . $e->getMessage(),
        'data' => null
    ]);
}
?> 