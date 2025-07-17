<!DOCTYPE html>
<html>
<head>
    <title>Test Agent API</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .success { color: green; }
        .error { color: red; }
        .data { background: #f5f5f5; padding: 10px; margin: 10px 0; border-radius: 5px; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <h1>Test Agent API</h1>
    
    <?php
    // Include the fetch_agent_data.php file
    ob_start();
    include 'fetch_agent_data.php';
    $output = ob_get_clean();
    
    // Parse the JSON response
    $response = json_decode($output, true);
    
    if ($response) {
        if ($response['status'] === 'success') {
            echo "<div class='success'>✅ API Response: Success</div>";
            echo "<div class='data'>Total Agents: " . $response['count'] . "</div>";
            
            if ($response['count'] > 0) {
                echo "<h3>Agent Data:</h3>";
                echo "<table>";
                echo "<tr>
                        <th>Full Name</th>
                        <th>Company</th>
                        <th>Phone</th>
                        <th>Alt Phone</th>
                        <th>Email</th>
                        <th>Partner Type</th>
                        <th>State</th>
                        <th>Location</th>
                        <th>Address</th>
                        <th>Created By</th>
                      </tr>";
                
                foreach ($response['data'] as $agent) {
                    echo "<tr>";
                    echo "<td>" . htmlspecialchars($agent['full_name']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['company_name']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['Phone_number']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['alternative_Phone_number']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['email_id']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['partnerType']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['state']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['location']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['address']) . "</td>";
                    echo "<td>" . htmlspecialchars($agent['createdBy']) . "</td>";
                    echo "</tr>";
                }
                echo "</table>";
            } else {
                echo "<div class='error'>No agent data found in the database.</div>";
            }
        } else {
            echo "<div class='error'>❌ API Error: " . $response['message'] . "</div>";
        }
    } else {
        echo "<div class='error'>❌ Failed to parse JSON response</div>";
        echo "<div class='data'>Raw output: " . htmlspecialchars($output) . "</div>";
    }
    ?>
    
    <h3>Raw JSON Response:</h3>
    <pre><?php echo htmlspecialchars($output); ?></pre>
</body>
</html> 