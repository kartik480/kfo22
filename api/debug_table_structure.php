<?php
header('Content-Type: text/html; charset=utf-8');
require_once 'db_config.php';

echo "<h2>🔍 Debug: tbl_user Table Structure & Data Analysis</h2>";
echo "<p><strong>Database:</strong> $dbname</p>";
echo "<p><strong>Host:</strong> $host</p>";
echo "<p><strong>Username:</strong> $username</p><br>";

try {
    global $conn;
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    echo "<h3>1. Database Connection Status</h3>";
    echo "✅ Connected to database successfully<br>";
    echo "Database: " . $conn->database . "<br>";
    echo "Server: " . $conn->server_info . "<br><br>";
    
    // 1. Check if tbl_user table exists
    echo "<h3>2. Table Existence Check</h3>";
    $tableCheck = $conn->query("SHOW TABLES LIKE 'tbl_user'");
    if ($tableCheck->num_rows > 0) {
        echo "✅ tbl_user table exists<br>";
    } else {
        echo "❌ tbl_user table does not exist<br>";
        echo "<p>Available tables:</p>";
        $tables = $conn->query("SHOW TABLES");
        while ($table = $tables->fetch_array()) {
            echo "- " . $table[0] . "<br>";
        }
        exit;
    }
    
    // 2. Show table structure
    echo "<h3>3. Table Structure (DESCRIBE tbl_user)</h3>";
    $structureQuery = "DESCRIBE tbl_user";
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
    
    // 3. Check total rows
    $totalQuery = "SELECT COUNT(*) as total FROM tbl_user";
    $totalResult = $conn->query($totalQuery);
    $totalCount = $totalResult->fetch_assoc()['total'];
    echo "<h3>4. Total Records</h3>";
    echo "Total records in tbl_user: <strong>$totalCount</strong><br><br>";
    
    if ($totalCount == 0) {
        echo "❌ No records found in tbl_user table. This explains the empty API response.<br>";
        echo "<p><strong>Solution:</strong> Add users to the table.</p>";
        exit;
    }
    
    // 4. Show all records with ALL columns
    echo "<h3>5. All Records in tbl_user (Complete Data)</h3>";
    $allRecordsQuery = "SELECT * FROM tbl_user ORDER BY id LIMIT 10";
    $allRecordsResult = $conn->query($allRecordsQuery);
    
    if ($allRecordsResult->num_rows > 0) {
        // Get column names
        $columns = [];
        while ($field = $allRecordsResult->fetch_field()) {
            $columns[] = $field->name;
        }
        
        echo "<table border='1' style='border-collapse: collapse; width: 100%; font-size: 12px;'>";
        echo "<tr>";
        foreach ($columns as $column) {
            echo "<th>" . $column . "</th>";
        }
        echo "</tr>";
        
        // Reset result pointer
        $allRecordsResult->data_seek(0);
        
        while($row = $allRecordsResult->fetch_assoc()) {
            echo "<tr>";
            foreach ($columns as $column) {
                $value = $row[$column];
                if ($value === null) {
                    echo "<td style='background: #ffebee;'>NULL</td>";
                } else if ($value === '') {
                    echo "<td style='background: #fff3e0;'>EMPTY</td>";
                } else {
                    echo "<td>" . htmlspecialchars($value) . "</td>";
                }
            }
            echo "</tr>";
        }
        echo "</table>";
        
        if ($totalCount > 10) {
            echo "<p><em>Showing first 10 records. Total: $totalCount</em></p>";
        }
    }
    
    // 5. Check specific columns that the API uses
    echo "<h3>6. API Column Analysis</h3>";
    
    // Check if firstName column exists
    $firstNameCheck = $conn->query("SHOW COLUMNS FROM tbl_user LIKE 'firstName'");
    if ($firstNameCheck->num_rows > 0) {
        echo "✅ firstName column exists<br>";
        
        // Check firstName data
        $firstNameData = $conn->query("SELECT firstName, COUNT(*) as count FROM tbl_user GROUP BY firstName");
        echo "firstName values:<br>";
        while ($row = $firstNameData->fetch_assoc()) {
            $value = $row['firstName'] ? $row['firstName'] : 'NULL/EMPTY';
            echo "- '$value': " . $row['count'] . " records<br>";
        }
    } else {
        echo "❌ firstName column does not exist<br>";
    }
    
    // Check if lastName column exists
    $lastNameCheck = $conn->query("SHOW COLUMNS FROM tbl_user LIKE 'lastName'");
    if ($lastNameCheck->num_rows > 0) {
        echo "✅ lastName column exists<br>";
        
        // Check lastName data
        $lastNameData = $conn->query("SELECT lastName, COUNT(*) as count FROM tbl_user GROUP BY lastName");
        echo "lastName values:<br>";
        while ($row = $lastNameData->fetch_assoc()) {
            $value = $row['lastName'] ? $row['lastName'] : 'NULL/EMPTY';
            echo "- '$value': " . $row['count'] . " records<br>";
        }
    } else {
        echo "❌ lastName column does not exist<br>";
    }
    
    // Check if status column exists
    $statusCheck = $conn->query("SHOW COLUMNS FROM tbl_user LIKE 'status'");
    if ($statusCheck->num_rows > 0) {
        echo "✅ status column exists<br>";
        
        // Check status data
        $statusData = $conn->query("SELECT status, COUNT(*) as count FROM tbl_user GROUP BY status");
        echo "status values:<br>";
        while ($row = $statusData->fetch_assoc()) {
            $value = $row['status'] ? $row['status'] : 'NULL/EMPTY';
            echo "- '$value': " . $row['count'] . " records<br>";
        }
    } else {
        echo "❌ status column does not exist<br>";
    }
    
    // 6. Test the exact API query
    echo "<h3>7. Testing API Query</h3>";
    
    // First, check if the columns exist
    $requiredColumns = ['firstName', 'lastName', 'status'];
    $missingColumns = [];
    
    foreach ($requiredColumns as $column) {
        $colCheck = $conn->query("SHOW COLUMNS FROM tbl_user LIKE '$column'");
        if ($colCheck->num_rows == 0) {
            $missingColumns[] = $column;
        }
    }
    
    if (!empty($missingColumns)) {
        echo "❌ Missing required columns: " . implode(', ', $missingColumns) . "<br>";
        echo "This explains why the API returns empty results.<br>";
    } else {
        echo "✅ All required columns exist<br>";
        
        // Test the API query (updated for integer status)
        $apiQuery = "SELECT id, firstName, lastName 
                     FROM tbl_user 
                     WHERE (status = 'Active' OR status = 1 OR status IS NULL OR status = '') 
                     AND firstName IS NOT NULL 
                     AND firstName != '' 
                     AND firstName != 'EMPTY'
                     AND lastName IS NOT NULL 
                     AND lastName != ''
                     ORDER BY firstName ASC, lastName ASC";
        
        $apiResult = $conn->query($apiQuery);
        
        if ($apiResult === false) {
            echo "❌ API query failed: " . $conn->error . "<br>";
        } else {
            echo "✅ API query successful<br>";
            echo "Records matching API criteria: <strong>" . $apiResult->num_rows . "</strong><br>";
            
            if ($apiResult->num_rows > 0) {
                echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
                echo "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Full Name</th></tr>";
                
                while($row = $apiResult->fetch_assoc()) {
                    $fullName = trim($row['firstName'] . ' ' . $row['lastName']);
                    echo "<tr>";
                    echo "<td>" . $row['id'] . "</td>";
                    echo "<td>" . $row['firstName'] . "</td>";
                    echo "<td>" . $row['lastName'] . "</td>";
                    echo "<td>" . $fullName . "</td>";
                    echo "</tr>";
                }
                echo "</table>";
            } else {
                echo "❌ No records match the API criteria<br>";
            }
        }
    }
    
    // 7. Recommendations
    echo "<h3>8. Recommendations</h3>";
    
    if (!empty($missingColumns)) {
        echo "<p style='color: red;'><strong>Issue:</strong> Missing required columns in tbl_user table.</p>";
        echo "<p><strong>Solution:</strong> Add the missing columns to your table:</p>";
        echo "<pre style='background: #f5f5f5; padding: 10px; border-radius: 5px;'>";
        foreach ($missingColumns as $column) {
            if ($column == 'firstName') {
                echo "ALTER TABLE tbl_user ADD COLUMN firstName VARCHAR(100);\n";
            } elseif ($column == 'lastName') {
                echo "ALTER TABLE tbl_user ADD COLUMN lastName VARCHAR(100);\n";
            } elseif ($column == 'status') {
                echo "ALTER TABLE tbl_user ADD COLUMN status ENUM('Active', 'Inactive') DEFAULT 'Active';\n";
            }
        }
        echo "</pre>";
    } elseif ($totalCount == 0) {
        echo "<p style='color: orange;'><strong>Issue:</strong> No users in the table.</p>";
        echo "<p><strong>Solution:</strong> Add users to the table.</p>";
    } else {
        echo "<p style='color: green;'><strong>Table structure looks good.</strong></p>";
        echo "<p>If API still returns empty, check the data values above.</p>";
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 