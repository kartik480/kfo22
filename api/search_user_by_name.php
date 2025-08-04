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
    
    // Get the search name from the request
    $searchName = isset($_GET['name']) ? $_GET['name'] : null;
    
    if (!$searchName) {
        throw new Exception("Search name is required");
    }
    
    // Query to search for users by name
    $sql = "SELECT 
                u.id,
                u.username,
                u.firstName,
                u.lastName,
                u.employee_no,
                u.mobile,
                u.email_id,
                u.reportingTo,
                d.designation_name
            FROM tbl_user u
            LEFT JOIN tbl_designation d ON u.designation_id = d.id
            WHERE (u.firstName LIKE ? OR u.lastName LIKE ? OR CONCAT(u.firstName, ' ', u.lastName) LIKE ?)
            AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
            AND u.firstName IS NOT NULL 
            AND u.firstName != ''
            ORDER BY u.firstName ASC, u.lastName ASC
            LIMIT 20";
    
    $searchTerm = '%' . $searchName . '%';
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sss", $searchTerm, $searchTerm, $searchTerm);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result === false) {
        throw new Exception("Query failed: " . $stmt->error);
    }
    
    // Debug: Log the query and parameters
    error_log("Search query for name: " . $searchName);
    error_log("Search query returned " . $result->num_rows . " rows");
    
    $users = array();
    
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Combine firstName and lastName into full name
            $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
            
            // Debug: Log each user being processed
            error_log("Processing search result ID " . $row['id'] . ": " . $fullName);
            
            // Only add if full name is not empty
            if (!empty($fullName)) {
                $users[] = array(
                    'id' => $row['id'],
                    'username' => $row['username'],
                    'firstName' => $row['firstName'],
                    'lastName' => $row['lastName'],
                    'fullName' => $fullName,
                    'employee_no' => $row['employee_no'] ?? '',
                    'mobile' => $row['mobile'] ?? '',
                    'email_id' => $row['email_id'] ?? '',
                    'reportingTo' => $row['reportingTo'],
                    'designation_name' => $row['designation_name'] ?? 'Unknown'
                );
                error_log("Added search result: " . $fullName . " (reports to: " . $row['reportingTo'] . ")");
            } else {
                error_log("Skipped search result ID " . $row['id'] . " due to empty full name");
            }
        }
    }
    
    // Log the number of users found
    error_log("Found " . count($users) . " users matching search term: " . $searchName);
    
    echo json_encode(array(
        'status' => 'success',
        'data' => $users,
        'message' => 'Users found successfully',
        'count' => count($users),
        'search_term' => $searchName
    ));
    
} catch (Exception $e) {
    error_log("Error in search_user_by_name.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        'status' => 'error',
        'message' => 'Failed to search users: ' . $e->getMessage()
    ));
}
?> 