<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
$servername = "p3plzcpnl508816.prod.phx3.secureserver.net";
$db_username = "emp_kfinone";
$db_password = "*F*im1!Y0D25";
$dbname = "emp_kfinone";

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $db_username, $db_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response = array();
    
    // Test 1: Search for exact name "DUBEY SATYA SAIBABA"
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE CONCAT(firstName, ' ', lastName) = 'DUBEY SATYA SAIBABA'");
        $stmt->execute();
        $exactMatch = $stmt->fetch(PDO::FETCH_ASSOC);
        $response['exact_match'] = $exactMatch;
    } catch (Exception $e) {
        $response['exact_match_error'] = $e->getMessage();
    }
    
    // Test 2: Search for partial matches with "DUBEY"
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE firstName LIKE '%DUBEY%' OR lastName LIKE '%DUBEY%' OR CONCAT(firstName, ' ', lastName) LIKE '%DUBEY%'");
        $stmt->execute();
        $dubeyMatches = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['dubey_matches'] = $dubeyMatches;
    } catch (Exception $e) {
        $response['dubey_matches_error'] = $e->getMessage();
    }
    
    // Test 3: Search for partial matches with "SATYA"
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE firstName LIKE '%SATYA%' OR lastName LIKE '%SATYA%' OR CONCAT(firstName, ' ', lastName) LIKE '%SATYA%'");
        $stmt->execute();
        $satyaMatches = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['satya_matches'] = $satyaMatches;
    } catch (Exception $e) {
        $response['satya_matches_error'] = $e->getMessage();
    }
    
    // Test 4: Search for partial matches with "SAIBABA"
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE firstName LIKE '%SAIBABA%' OR lastName LIKE '%SAIBABA%' OR CONCAT(firstName, ' ', lastName) LIKE '%SAIBABA%'");
        $stmt->execute();
        $saibabaMatches = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['saibaba_matches'] = $saibabaMatches;
    } catch (Exception $e) {
        $response['saibaba_matches_error'] = $e->getMessage();
    }
    
    // Test 5: Get all users with "DUBEY" in firstName
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE firstName LIKE '%DUBEY%'");
        $stmt->execute();
        $dubeyFirstName = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['dubey_firstname'] = $dubeyFirstName;
    } catch (Exception $e) {
        $response['dubey_firstname_error'] = $e->getMessage();
    }
    
    // Test 6: Get all users with "SATYA" in firstName
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE firstName LIKE '%SATYA%'");
        $stmt->execute();
        $satyaFirstName = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['satya_firstname'] = $satyaFirstName;
    } catch (Exception $e) {
        $response['satya_firstname_error'] = $e->getMessage();
    }
    
    // Test 7: Get all users with "SAIBABA" in lastName
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE lastName LIKE '%SAIBABA%'");
        $stmt->execute();
        $saibabaLastName = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['saibaba_lastname'] = $saibabaLastName;
    } catch (Exception $e) {
        $response['saibaba_lastname_error'] = $e->getMessage();
    }
    
    // Test 8: Get all users with "SAI" in lastName
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE lastName LIKE '%SAI%'");
        $stmt->execute();
        $saiLastName = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['sai_lastname'] = $saiLastName;
    } catch (Exception $e) {
        $response['sai_lastname_error'] = $e->getMessage();
    }
    
    // Test 9: Get all users with "BABA" in lastName
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE lastName LIKE '%BABA%'");
        $stmt->execute();
        $babaLastName = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['baba_lastname'] = $babaLastName;
    } catch (Exception $e) {
        $response['baba_lastname_error'] = $e->getMessage();
    }
    
    // Test 10: Get all users with "DUBEY" in lastName
    try {
        $stmt = $pdo->prepare("SELECT id, firstName, lastName, CONCAT(firstName, ' ', lastName) as fullName FROM tbl_user WHERE lastName LIKE '%DUBEY%'");
        $stmt->execute();
        $dubeyLastName = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['dubey_lastname'] = $dubeyLastName;
    } catch (Exception $e) {
        $response['dubey_lastname_error'] = $e->getMessage();
    }
    
    echo json_encode([
        'status' => 'success',
        'message' => 'Debug search for DUBEY SATYA SAIBABA completed',
        'data' => $response,
        'timestamp' => date('Y-m-d H:i:s')
    ], JSON_PRETTY_PRINT);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage(),
        'data' => []
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Server error: ' . $e->getMessage(),
        'data' => []
    ]);
}
?> 