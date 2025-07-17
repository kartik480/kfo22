<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>🔍 Debug: Employee Panel Database Analysis</h2>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h3>1. Database Connection Status</h3>";
    echo "✅ Database connected successfully<br>";
    echo "Database: " . $conn->database . "<br><br>";
    
    // 1. Check if tbl_designation table exists
    echo "<h3>2. Designation Table Check</h3>";
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_designation'");
    if ($tableCheck->num_rows > 0) {
        echo "✅ tbl_designation table exists<br>";
    } else {
        echo "❌ tbl_designation table does not exist<br>";
        echo "This is the root cause - the table doesn't exist!<br>";
        exit;
    }
    
    // 2. Check tbl_designation structure
    echo "<h3>3. tbl_designation Table Structure</h3>";
    $structureQuery = "DESCRIBE tbl_designation";
    $structureResult = $conn->query($structureQuery);
    
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
    while($row = $structureResult->fetch_assoc()) {
        echo "<tr>";
        echo "<td>" . $row['Field'] . "</td>";
        echo "<td>" . $row['Type'] . "</td>";
        echo "<td>" . $row['Null'] . "</td>";
        echo "<td>" . $row['Key'] . "</td>";
        echo "<td>" . $row['Default'] . "</td>";
        echo "<td>" . $row['Extra'] . "</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
    // 3. Check all designations in tbl_designation
    echo "<h3>4. All Designations in tbl_designation</h3>";
    $designationQuery = "SELECT id, designation_name FROM tbl_designation ORDER BY designation_name";
    $designationResult = $conn->query($designationQuery);
    
    if ($designationResult->num_rows > 0) {
        echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
        echo "<tr><th>ID</th><th>Designation Name</th><th>Target Match</th></tr>";
        
        $targetDesignations = ['Chief Business Officer', 'Regional Business Head', 'Director'];
        
        while($row = $designationResult->fetch_assoc()) {
            $isTarget = in_array($row['designation_name'], $targetDesignations) ? '✅ YES' : '❌ NO';
            echo "<tr>";
            echo "<td>" . $row['id'] . "</td>";
            echo "<td>" . $row['designation_name'] . "</td>";
            echo "<td>" . $isTarget . "</td>";
            echo "</tr>";
        }
        echo "</table><br>";
    } else {
        echo "❌ No designations found in tbl_designation table<br>";
    }
    
    // 4. Check if tbl_user table has designation_id column
    echo "<h3>5. tbl_user Table Structure Check</h3>";
    $userStructureQuery = "DESCRIBE tbl_user";
    $userStructureResult = $conn->query($userStructureQuery);
    
    $hasDesignationId = false;
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
    while($row = $userStructureResult->fetch_assoc()) {
        if ($row['Field'] == 'designation_id') {
            $hasDesignationId = true;
        }
        echo "<tr>";
        echo "<td>" . $row['Field'] . "</td>";
        echo "<td>" . $row['Type'] . "</td>";
        echo "<td>" . $row['Null'] . "</td>";
        echo "<td>" . $row['Key'] . "</td>";
        echo "<td>" . $row['Default'] . "</td>";
        echo "<td>" . $row['Extra'] . "</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
    if ($hasDesignationId) {
        echo "✅ tbl_user table has designation_id column<br>";
    } else {
        echo "❌ tbl_user table does NOT have designation_id column<br>";
        echo "This is a problem - we need this column to join with tbl_designation!<br>";
    }
    
    // 5. Check sample users with their designation_id
    if ($hasDesignationId) {
        echo "<h3>6. Sample Users with Designation IDs</h3>";
        $userQuery = "SELECT id, firstName, lastName, designation_id FROM tbl_user WHERE designation_id IS NOT NULL LIMIT 10";
        $userResult = $conn->query($userQuery);
        
        if ($userResult->num_rows > 0) {
            echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
            echo "<tr><th>User ID</th><th>Name</th><th>Designation ID</th><th>Designation Name</th></tr>";
            
            while($row = $userResult->fetch_assoc()) {
                // Get designation name for this ID
                $desigQuery = "SELECT designation_name FROM tbl_designation WHERE id = " . $row['designation_id'];
                $desigResult = $conn->query($desigQuery);
                $desigName = "Unknown";
                if ($desigResult->num_rows > 0) {
                    $desigRow = $desigResult->fetch_assoc();
                    $desigName = $desigRow['designation_name'];
                }
                
                echo "<tr>";
                echo "<td>" . $row['id'] . "</td>";
                echo "<td>" . $row['firstName'] . " " . $row['lastName'] . "</td>";
                echo "<td>" . $row['designation_id'] . "</td>";
                echo "<td>" . $desigName . "</td>";
                echo "</tr>";
            }
            echo "</table><br>";
        } else {
            echo "❌ No users found with designation_id<br>";
        }
    }
    
    // 6. Test the actual query
    echo "<h3>7. Testing the Employee Panel Query</h3>";
    $testQuery = "SELECT 
                    u.id,
                    u.firstName,
                    u.lastName,
                    u.employee_no,
                    u.mobile,
                    u.email_id,
                    d.designation_name
                FROM tbl_user u
                INNER JOIN tbl_designation d ON u.designation_id = d.id
                WHERE d.designation_name IN ('Chief Business Officer', 'Regional Business Head', 'Director')
                AND (u.status = 'Active' OR u.status = 1 OR u.status IS NULL OR u.status = '')
                AND u.firstName IS NOT NULL 
                AND u.firstName != ''
                ORDER BY u.firstName ASC, u.lastName ASC";
    
    $testResult = $conn->query($testQuery);
    
    if ($testResult === false) {
        echo "<p style='color: red;'>❌ Query failed: " . $conn->error . "</p>";
    } else {
        echo "<p style='color: green;'>✅ Query successful</p>";
        echo "Records found: <strong>" . $testResult->num_rows . "</strong><br><br>";
        
        if ($testResult->num_rows > 0) {
            echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
            echo "<tr><th>ID</th><th>Name</th><th>Employee No</th><th>Mobile</th><th>Email</th><th>Designation</th></tr>";
            
            while($row = $testResult->fetch_assoc()) {
                echo "<tr>";
                echo "<td>" . $row['id'] . "</td>";
                echo "<td>" . $row['firstName'] . " " . $row['lastName'] . "</td>";
                echo "<td>" . ($row['employee_no'] ? $row['employee_no'] : 'N/A') . "</td>";
                echo "<td>" . ($row['mobile'] ? $row['mobile'] : 'N/A') . "</td>";
                echo "<td>" . ($row['email_id'] ? $row['email_id'] : 'N/A') . "</td>";
                echo "<td>" . $row['designation_name'] . "</td>";
                echo "</tr>";
            }
            echo "</table>";
        } else {
            echo "<p style='color: orange;'>⚠️ No users found with the target designations</p>";
        }
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>❌ Error: " . $e->getMessage() . "</p>";
}
?> 