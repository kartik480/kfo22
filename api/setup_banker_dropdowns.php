<?php
// Setup script for banker dropdown functionality
// This script will create all the required tables for banker dropdowns

require_once 'db_config.php';

echo "<h2>Setting up Banker Dropdown System</h2>";

try {
    // Test database connection
    $conn = new mysqli($host, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    echo "<p style='color: green;'>✓ Database connection successful!</p>";
    
    // Array of tables to create
    $tables = [
        'tbl_vendor_bank' => [
            'create' => "CREATE TABLE tbl_vendor_bank (
                id INT AUTO_INCREMENT PRIMARY KEY,
                vendor_bank_name VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )",
            'sample_data' => [
                'HDFC Bank',
                'ICICI Bank', 
                'State Bank of India',
                'Axis Bank',
                'Kotak Mahindra Bank',
                'Punjab National Bank',
                'Bank of Baroda',
                'Canara Bank',
                'Union Bank of India',
                'Bank of India'
            ],
            'column' => 'vendor_bank_name'
        ],
        'tbl_banker_designation' => [
            'create' => "CREATE TABLE tbl_banker_designation (
                id INT AUTO_INCREMENT PRIMARY KEY,
                designation_name VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )",
            'sample_data' => [
                'Manager',
                'Assistant Manager',
                'Senior Manager',
                'Branch Manager',
                'Relationship Manager',
                'Deputy Manager',
                'Chief Manager',
                'General Manager',
                'Regional Manager',
                'Zonal Manager'
            ],
            'column' => 'designation_name'
        ],
        'tbl_loan_type' => [
            'create' => "CREATE TABLE tbl_loan_type (
                id INT AUTO_INCREMENT PRIMARY KEY,
                loan_type VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )",
            'sample_data' => [
                'Home Loan',
                'Personal Loan',
                'Business Loan',
                'Car Loan',
                'Education Loan',
                'Gold Loan',
                'Property Loan',
                'Working Capital Loan',
                'Term Loan',
                'Overdraft'
            ],
            'column' => 'loan_type'
        ],
        'tbl_branch_state' => [
            'create' => "CREATE TABLE tbl_branch_state (
                id INT AUTO_INCREMENT PRIMARY KEY,
                branch_state_name VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )",
            'sample_data' => [
                'Maharashtra',
                'Karnataka',
                'Tamil Nadu',
                'Delhi',
                'Gujarat',
                'Andhra Pradesh',
                'Telangana',
                'Kerala',
                'West Bengal',
                'Uttar Pradesh'
            ],
            'column' => 'branch_state_name'
        ],
        'tbl_branch_location' => [
            'create' => "CREATE TABLE tbl_branch_location (
                id INT AUTO_INCREMENT PRIMARY KEY,
                branch_location VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )",
            'sample_data' => [
                'Mumbai',
                'Bangalore',
                'Chennai',
                'New Delhi',
                'Ahmedabad',
                'Hyderabad',
                'Pune',
                'Kolkata',
                'Lucknow',
                'Jaipur'
            ],
            'column' => 'branch_location'
        ]
    ];
    
    // Create tables and add sample data
    foreach ($tables as $tableName => $tableConfig) {
        echo "<h3>Setting up $tableName</h3>";
        
        // Check if table exists
        $result = $conn->query("SHOW TABLES LIKE '$tableName'");
        if ($result->num_rows == 0) {
            echo "<p>Creating $tableName table...</p>";
            
            if ($conn->query($tableConfig['create']) === TRUE) {
                echo "<p style='color: green;'>✓ Table created successfully!</p>";
            } else {
                echo "<p style='color: red;'>✗ Error creating table: " . $conn->error . "</p>";
                continue;
            }
        } else {
            echo "<p style='color: green;'>✓ Table already exists!</p>";
        }
        
        // Add sample data if table is empty
        $result = $conn->query("SELECT COUNT(*) as count FROM $tableName");
        $row = $result->fetch_assoc();
        
        if ($row['count'] == 0) {
            echo "<p>Adding sample data to $tableName...</p>";
            
            $addedCount = 0;
            foreach ($tableConfig['sample_data'] as $item) {
                $columnName = $tableConfig['column'];
                $insertSql = "INSERT INTO $tableName ($columnName) VALUES (?)";
                $insertStmt = $conn->prepare($insertSql);
                $insertStmt->bind_param("s", $item);
                
                if ($insertStmt->execute()) {
                    echo "<p style='color: green;'>✓ Added: " . htmlspecialchars($item) . "</p>";
                    $addedCount++;
                } else {
                    echo "<p style='color: red;'>✗ Error adding: " . htmlspecialchars($item) . "</p>";
                }
                $insertStmt->close();
            }
            
            echo "<p style='color: green;'>✓ Added $addedCount items to $tableName!</p>";
        } else {
            echo "<p style='color: blue;'>ℹ Table already has data, skipping sample data insertion.</p>";
        }
        
        // Show current data
        echo "<h4>Current data in $tableName:</h4>";
        echo "<table border='1' style='border-collapse: collapse; width: 100%; margin-bottom: 20px;'>";
        echo "<tr style='background-color: #f0f0f0;'><th>ID</th><th>" . ucfirst(str_replace('_', ' ', $tableConfig['column'])) . "</th><th>Created At</th></tr>";
        
        $result = $conn->query("SELECT * FROM $tableName ORDER BY {$tableConfig['column']} ASC");
        while ($row = $result->fetch_assoc()) {
            echo "<tr>";
            echo "<td>" . $row['id'] . "</td>";
            echo "<td>" . htmlspecialchars($row[$tableConfig['column']]) . "</td>";
            echo "<td>" . $row['created_at'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    echo "<h3>Setup Complete! 🎉</h3>";
    echo "<p>Your banker dropdown system is now ready. The Android app should be able to connect to:</p>";
    echo "<ul>";
    echo "<li><strong>GET:</strong> http://10.0.2.2/kfindb/get_vendor_bank_list.php</li>";
    echo "<li><strong>GET:</strong> http://10.0.2.2/kfindb/get_banker_designation_list.php</li>";
    echo "<li><strong>GET:</strong> http://10.0.2.2/kfindb/get_loan_type_list.php</li>";
    echo "<li><strong>GET:</strong> http://10.0.2.2/kfindb/get_branch_state_list.php</li>";
    echo "<li><strong>GET:</strong> http://10.0.2.2/kfindb/get_branch_location_list.php</li>";
    echo "</ul>";
    
    $conn->close();
    
} catch (Exception $e) {
    echo "<p style='color: red;'>✗ Error: " . $e->getMessage() . "</p>";
}
?> 