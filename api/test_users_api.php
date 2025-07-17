<!DOCTYPE html>
<html>
<head>
    <title>Test Users API</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .test-section { margin: 20px 0; padding: 15px; border: 1px solid #ccc; }
        .result { background: #f5f5f5; padding: 10px; margin: 10px 0; border-radius: 5px; }
        button { padding: 10px 20px; margin: 5px; cursor: pointer; }
    </style>
</head>
<body>
    <h1>Test Users API</h1>
    
    <div class="test-section">
        <h3>Test Get Active Users</h3>
        <button onclick="testActiveUsers()">Test Active Users API</button>
        <div id="activeResult" class="result"></div>
    </div>
    
    <div class="test-section">
        <h3>Test Get Inactive Users</h3>
        <button onclick="testInactiveUsers()">Test Inactive Users API</button>
        <div id="inactiveResult" class="result"></div>
    </div>
    
    <div class="test-section">
        <h3>Test Update User Status</h3>
        <input type="number" id="userId" placeholder="User ID" style="padding: 5px; margin: 5px;">
        <button onclick="testUpdateStatus()">Test Update Status API</button>
        <div id="updateResult" class="result"></div>
    </div>

    <script>
        function testActiveUsers() {
            fetch('get_all_users.php')
                .then(response => response.text())
                .then(data => {
                    document.getElementById('activeResult').innerHTML = '<strong>Response:</strong><br>' + data;
                    try {
                        const json = JSON.parse(data);
                        document.getElementById('activeResult').innerHTML += '<br><strong>Parsed JSON:</strong><br>' + JSON.stringify(json, null, 2);
                    } catch(e) {
                        document.getElementById('activeResult').innerHTML += '<br><strong>JSON Parse Error:</strong> ' + e.message;
                    }
                })
                .catch(error => {
                    document.getElementById('activeResult').innerHTML = '<strong>Error:</strong> ' + error.message;
                });
        }
        
        function testInactiveUsers() {
            fetch('get_inactive_users.php')
                .then(response => response.text())
                .then(data => {
                    document.getElementById('inactiveResult').innerHTML = '<strong>Response:</strong><br>' + data;
                    try {
                        const json = JSON.parse(data);
                        document.getElementById('inactiveResult').innerHTML += '<br><strong>Parsed JSON:</strong><br>' + JSON.stringify(json, null, 2);
                    } catch(e) {
                        document.getElementById('inactiveResult').innerHTML += '<br><strong>JSON Parse Error:</strong> ' + e.message;
                    }
                })
                .catch(error => {
                    document.getElementById('inactiveResult').innerHTML = '<strong>Error:</strong> ' + error.message;
                });
        }
        
        function testUpdateStatus() {
            const userId = document.getElementById('userId').value;
            if (!userId) {
                alert('Please enter a User ID');
                return;
            }
            
            fetch('update_user_status.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: userId,
                    status: 'Inactive'
                })
            })
            .then(response => response.text())
            .then(data => {
                document.getElementById('updateResult').innerHTML = '<strong>Response:</strong><br>' + data;
                try {
                    const json = JSON.parse(data);
                    document.getElementById('updateResult').innerHTML += '<br><strong>Parsed JSON:</strong><br>' + JSON.stringify(json, null, 2);
                } catch(e) {
                    document.getElementById('updateResult').innerHTML += '<br><strong>JSON Parse Error:</strong> ' + e.message;
                }
            })
            .catch(error => {
                document.getElementById('updateResult').innerHTML = '<strong>Error:</strong> ' + error.message;
            });
        }
    </script>
</body>
</html> 