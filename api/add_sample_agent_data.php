<?php
// Include database configuration
include 'db_config.php';

try {
    // Create connection
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    // Check connection
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    // Sample agent data
    $sampleAgents = [
        [
            'full_name' => 'John Smith',
            'company_name' => 'ABC Finance Ltd',
            'Phone_number' => '9876543210',
            'alternative_Phone_number' => '9876543211',
            'email_id' => 'john.smith@abcfinance.com',
            'partnerType' => 'DSA',
            'state' => 'Maharashtra',
            'location' => 'Mumbai',
            'address' => '123 Main Street, Andheri West, Mumbai - 400058',
            'visiting_card' => 'john_smith_card.jpg',
            'created_user' => 'admin',
            'createdBy' => '2024-01-15 10:30:00'
        ],
        [
            'full_name' => 'Priya Patel',
            'company_name' => 'XYZ Financial Services',
            'Phone_number' => '8765432109',
            'alternative_Phone_number' => '8765432108',
            'email_id' => 'priya.patel@xyzfinance.com',
            'partnerType' => 'Agent',
            'state' => 'Gujarat',
            'location' => 'Ahmedabad',
            'address' => '456 Business Park, Satellite, Ahmedabad - 380015',
            'visiting_card' => 'priya_patel_card.jpg',
            'created_user' => 'admin',
            'createdBy' => '2024-01-16 14:45:00'
        ],
        [
            'full_name' => 'Rajesh Kumar',
            'company_name' => 'KLM Investment Solutions',
            'Phone_number' => '7654321098',
            'alternative_Phone_number' => '7654321097',
            'email_id' => 'rajesh.kumar@klminvest.com',
            'partnerType' => 'Banker',
            'state' => 'Karnataka',
            'location' => 'Bangalore',
            'address' => '789 Tech Hub, Koramangala, Bangalore - 560034',
            'visiting_card' => 'rajesh_kumar_card.jpg',
            'created_user' => 'admin',
            'createdBy' => '2024-01-17 09:15:00'
        ],
        [
            'full_name' => 'Sneha Sharma',
            'company_name' => 'PQR Capital Ltd',
            'Phone_number' => '6543210987',
            'alternative_Phone_number' => '6543210986',
            'email_id' => 'sneha.sharma@pqrcapital.com',
            'partnerType' => 'DSA',
            'state' => 'Delhi',
            'location' => 'New Delhi',
            'address' => '321 Connaught Place, New Delhi - 110001',
            'visiting_card' => 'sneha_sharma_card.jpg',
            'created_user' => 'admin',
            'createdBy' => '2024-01-18 16:20:00'
        ],
        [
            'full_name' => 'Amit Singh',
            'company_name' => 'DEF Financial Group',
            'Phone_number' => '5432109876',
            'alternative_Phone_number' => '5432109875',
            'email_id' => 'amit.singh@deffinancial.com',
            'partnerType' => 'Agent',
            'state' => 'Punjab',
            'location' => 'Chandigarh',
            'address' => '654 Sector 17, Chandigarh - 160017',
            'visiting_card' => 'amit_singh_card.jpg',
            'created_user' => 'admin',
            'createdBy' => '2024-01-19 11:30:00'
        ]
    ];
    
    // Insert sample data
    $insertedCount = 0;
    foreach ($sampleAgents as $agent) {
        $sql = "INSERT INTO tbl_agent_data (
                    full_name, company_name, Phone_number, alternative_Phone_number,
                    email_id, partnerType, state, location, address, visiting_card,
                    created_user, createdBy
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ssssssssssss", 
            $agent['full_name'], $agent['company_name'], $agent['Phone_number'],
            $agent['alternative_Phone_number'], $agent['email_id'], $agent['partnerType'],
            $agent['state'], $agent['location'], $agent['address'], $agent['visiting_card'],
            $agent['created_user'], $agent['createdBy']
        );
        
        if ($stmt->execute()) {
            $insertedCount++;
        }
        $stmt->close();
    }
    
    echo "Successfully inserted $insertedCount sample agent records into tbl_agent_data table.";
    
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?> 