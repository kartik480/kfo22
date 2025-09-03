<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Return sample data for testing
$sampleAgents = [
    [
        'id' => '1',
        'full_name' => 'Agent One',
        'company_name' => 'ABC Corp',
        'Phone_number' => '1111111111',
        'alternative_Phone_number' => '2222222222',
        'email_id' => 'agent1@abc.com',
        'partnerType' => 'Business',
        'state' => 'Maharashtra',
        'location' => 'Mumbai',
        'address' => 'Mumbai Address',
        'visiting_card' => '',
        'created_user' => 'cbo_user1',
        'createdBy' => 'cbo_user1',
        'status' => 'active',
        'created_at' => '2024-01-01 10:00:00',
        'updated_at' => '2024-01-01 10:00:00',
        'creator_first_name' => 'John',
        'creator_last_name' => 'Smith',
        'creator_username' => 'cbo_user1',
        'creator_full_name' => 'John Smith'
    ],
    [
        'id' => '2',
        'full_name' => 'Agent Two',
        'company_name' => 'XYZ Ltd',
        'Phone_number' => '3333333333',
        'alternative_Phone_number' => '4444444444',
        'email_id' => 'agent2@xyz.com',
        'partnerType' => 'Individual',
        'state' => 'Delhi',
        'location' => 'New Delhi',
        'address' => 'Delhi Address',
        'visiting_card' => '',
        'created_user' => 'rbh_user1',
        'createdBy' => 'rbh_user1',
        'status' => 'active',
        'created_at' => '2024-01-02 11:00:00',
        'updated_at' => '2024-01-02 11:00:00',
        'creator_first_name' => 'Jane',
        'creator_last_name' => 'Doe',
        'creator_username' => 'rbh_user1',
        'creator_full_name' => 'Jane Doe'
    ],
    [
        'id' => '3',
        'full_name' => 'Agent Three',
        'company_name' => 'DEF Inc',
        'Phone_number' => '5555555555',
        'alternative_Phone_number' => '6666666666',
        'email_id' => 'agent3@def.com',
        'partnerType' => 'Business',
        'state' => 'Karnataka',
        'location' => 'Bangalore',
        'address' => 'Bangalore Address',
        'visiting_card' => '',
        'created_user' => 'director_user1',
        'createdBy' => 'director_user1',
        'status' => 'active',
        'created_at' => '2024-01-03 12:00:00',
        'updated_at' => '2024-01-03 12:00:00',
        'creator_first_name' => 'Mike',
        'creator_last_name' => 'Johnson',
        'creator_username' => 'director_user1',
        'creator_full_name' => 'Mike Johnson'
    ]
];

echo json_encode($sampleAgents, JSON_PRETTY_PRINT);
?>
