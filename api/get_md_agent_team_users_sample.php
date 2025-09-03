<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Return sample data for testing
$sampleUsers = [
    [
        'id' => '1',
        'username' => 'cbo_user1',
        'firstName' => 'John',
        'lastName' => 'Smith',
        'designation_id' => '1',
        'designation_name' => 'Chief Business Officer',
        'emailId' => 'john.smith@example.com',
        'mobile' => '1234567890',
        'status' => 'active'
    ],
    [
        'id' => '2',
        'username' => 'rbh_user1',
        'firstName' => 'Jane',
        'lastName' => 'Doe',
        'designation_id' => '2',
        'designation_name' => 'Regional Business Head',
        'emailId' => 'jane.doe@example.com',
        'mobile' => '0987654321',
        'status' => 'active'
    ],
    [
        'id' => '3',
        'username' => 'director_user1',
        'firstName' => 'Mike',
        'lastName' => 'Johnson',
        'designation_id' => '3',
        'designation_name' => 'Director',
        'emailId' => 'mike.johnson@example.com',
        'mobile' => '1122334455',
        'status' => 'active'
    ]
];

echo json_encode($sampleUsers, JSON_PRETTY_PRINT);
?>
