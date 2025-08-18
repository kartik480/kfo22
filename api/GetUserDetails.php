<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // Get the user ID from the request
    $userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;
    
    if (!$userId) {
        throw new Exception("User ID is required");
    }
    
    // First, let's get the table structure to see what columns exist
    $structureQuery = "DESCRIBE tbl_user";
    $structureResult = $conn->query($structureQuery);
    
    if ($structureResult === false) {
        throw new Exception("Could not get table structure: " . $conn->error);
    }
    
    $availableColumns = array();
    while ($row = $structureResult->fetch_assoc()) {
        $availableColumns[] = $row['Field'];
    }
    
    // Build dynamic SQL query with only existing columns
    $sql = "SELECT * FROM tbl_user WHERE id = ?";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Query failed: " . $stmt->error);
    }
    
    if ($result->num_rows === 0) {
        throw new Exception("User not found with ID: " . $userId);
    }
    
    $userData = $result->fetch_assoc();
    
    // Log the query and result
    error_log("GetUserDetails query for user_id: " . $userId);
    error_log("GetUserDetails returned user: " . $userData['firstName'] . " " . $userData['lastName']);
    error_log("Available columns in tbl_user: " . implode(", ", $availableColumns));
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $userData,
        'available_columns' => $availableColumns,
        'message' => 'User details fetched successfully',
        'user_id' => $userId
    ));
    
} catch (Exception $e) {
    error_log("GetUserDetails error: " . $e->getMessage());
    echo json_encode(array(
        'status' => 'error',
        'message' => $e->getMessage(),
        'user_id' => $userId ?? null
    ));
}
?>
