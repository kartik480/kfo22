<!DOCTYPE html>
<html>
<head>
    <title>Test Login API</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-group { margin: 10px 0; }
        label { display: inline-block; width: 100px; }
        input[type="text"], input[type="password"] { width: 200px; padding: 5px; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; cursor: pointer; }
        button:hover { background: #0056b3; }
        #result { margin-top: 20px; padding: 10px; border: 1px solid #ccc; min-height: 100px; }
    </style>
</head>
<body>
    <h2>Test Login API</h2>
    
    <div class="form-group">
        <label>Username:</label>
        <input type="text" id="username" placeholder="Enter username">
    </div>
    
    <div class="form-group">
        <label>Password:</label>
        <input type="password" id="password" placeholder="Enter password">
    </div>
    
    <div class="form-group">
        <button onclick="testLogin()">Test Login</button>
    </div>
    
    <div id="result"></div>

    <script>
        function testLogin() {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const resultDiv = document.getElementById('result');
            
            if (!username || !password) {
                resultDiv.innerHTML = '<p style="color: red;">Please enter both username and password</p>';
                return;
            }
            
            const data = {
                username: username,
                password: password
            };
            
            fetch('login.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
            .then(response => {
                console.log('Response status:', response.status);
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    resultDiv.innerHTML = '<p style="color: green;">✅ Login Successful!</p><pre>' + JSON.stringify(data, null, 2) + '</pre>';
                } else {
                    resultDiv.innerHTML = '<p style="color: red;">❌ Login Failed</p><pre>' + JSON.stringify(data, null, 2) + '</pre>';
                }
            })
            .catch(error => {
                resultDiv.innerHTML = '<p style="color: red;">❌ Error: ' + error.message + '</p>';
            });
        }
    </script>
</body>
</html> 