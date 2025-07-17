<?php
// Simple test file to verify agent filtering system
header('Content-Type: text/html; charset=utf-8');
?>

<!DOCTYPE html>
<html>
<head>
    <title>Agent Filter Test</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 1200px; margin: 0 auto; }
        .form-group { margin: 10px 0; }
        label { display: inline-block; width: 120px; }
        select, button { padding: 8px; margin: 5px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .result { margin-top: 20px; padding: 10px; background-color: #f9f9f9; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Agent Filter Test</h1>
        
        <form id="filterForm">
            <div class="form-group">
                <label>State:</label>
                <select id="stateSelect">
                    <option value="0">All States</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Location:</label>
                <select id="locationSelect">
                    <option value="0">All Locations</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Partner Type:</label>
                <select id="partnerTypeSelect">
                    <option value="0">All Partner Types</option>
                </select>
            </div>
            
            <button type="button" onclick="loadFilterOptions()">Load Options</button>
            <button type="button" onclick="filterAgents()">Filter Agents</button>
        </form>
        
        <div id="result" class="result"></div>
    </div>

    <script>
        // Load filter options
        function loadFilterOptions() {
            fetch('https://pznstudio.shop/kfinone/fetch_filter_options.php')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        populateSelect('stateSelect', data.data.states, 'branch_state_name');
                        populateSelect('locationSelect', data.data.locations, 'branch_location');
                        populateSelect('partnerTypeSelect', data.data.partner_types, 'partner_type');
                        document.getElementById('result').innerHTML = '<p>Filter options loaded successfully!</p>';
                    } else {
                        document.getElementById('result').innerHTML = '<p>Error: ' + data.message + '</p>';
                    }
                })
                .catch(error => {
                    document.getElementById('result').innerHTML = '<p>Error loading options: ' + error.message + '</p>';
                });
        }

        function populateSelect(selectId, data, nameKey) {
            const select = document.getElementById(selectId);
            // Keep the first "All" option
            select.innerHTML = '<option value="0">All ' + selectId.replace('Select', '') + '</option>';
            
            data.forEach(item => {
                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = item[nameKey];
                select.appendChild(option);
            });
        }

        // Filter agents
        function filterAgents() {
            const stateId = document.getElementById('stateSelect').value;
            const locationId = document.getElementById('locationSelect').value;
            const partnerTypeId = document.getElementById('partnerTypeSelect').value;

            const filters = {
                state_id: stateId,
                location_id: locationId,
                partner_type_id: partnerTypeId
            };

            fetch('https://pznstudio.shop/kfinone/filter_agents.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(filters)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    displayAgents(data.data);
                } else {
                    document.getElementById('result').innerHTML = '<p>Error: ' + data.message + '</p>';
                }
            })
            .catch(error => {
                document.getElementById('result').innerHTML = '<p>Error filtering agents: ' + error.message + '</p>';
            });
        }

        function displayAgents(agents) {
            let html = '<h3>Found ' + agents.length + ' agents:</h3>';
            
            if (agents.length > 0) {
                html += '<table>';
                html += '<tr><th>Full Name</th><th>Company</th><th>Phone</th><th>Partner Type</th><th>State</th><th>Location</th><th>Created By</th></tr>';
                
                agents.forEach(agent => {
                    html += '<tr>';
                    html += '<td>' + (agent.full_name || '') + '</td>';
                    html += '<td>' + (agent.company_name || '') + '</td>';
                    html += '<td>' + (agent.Phone_number || '') + '</td>';
                    html += '<td>' + (agent.partnerType || '') + '</td>';
                    html += '<td>' + (agent.state || '') + '</td>';
                    html += '<td>' + (agent.location || '') + '</td>';
                    html += '<td>' + (agent.createdBy || '') + '</td>';
                    html += '</tr>';
                });
                
                html += '</table>';
            } else {
                html += '<p>No agents found matching the selected filters.</p>';
            }
            
            document.getElementById('result').innerHTML = html;
        }

        // Load options when page loads
        window.onload = function() {
            loadFilterOptions();
        };
    </script>
</body>
</html> 