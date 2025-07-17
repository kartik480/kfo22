<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Simple test response first
echo json_encode([
    'status' => 'success',
    'message' => 'API is working - testing database connection...',
    'data' => [
        [
            'id' => '1',
            'firstName' => 'Test',
            'lastName' => 'User',
            'fullName' => 'Test User',
            'username' => 'testuser',
            'email' => 'test@example.com',
            'mobile' => '1234567890',
            'status' => 'Active',
            'employeeNo' => 'testuser',
            'designation' => 'Manager',
            'department' => 'IT'
        ]
    ],
    'summary' => [
        'total_users' => 1,
        'active_users' => 1,
        'inactive_users' => 0
    ]
]);
?> 