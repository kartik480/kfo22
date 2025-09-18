package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CBOAgentTeamActivity extends AppCompatActivity {

    private static final String TAG = "CBOAgentTeamActivity";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api";

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Team Agent elements
    private Spinner rbhUserSpinner;
    private LinearLayout agentListContainer;
    private Button showDataButton;
    private Button resetButton;

    // User data
    private String userName;
    private String userId;

    // Data
    private List<RbhUserItem> rbhUsers = new ArrayList<>();
    private List<AgentItem> agentList = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_agent_team);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadAgentTeamData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Team Agent elements
        rbhUserSpinner = findViewById(R.id.rbhUserSpinner);
        agentListContainer = findViewById(R.id.agentListContainer);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewAgent());

        // Show Data Button - Load agents based on selection
        showDataButton.setOnClickListener(v -> {
            int selectedPosition = rbhUserSpinner.getSelectedItemPosition();
            if (selectedPosition > 0) { // Skip the first item which is "Select User"
                RbhUserItem selectedUser = rbhUsers.get(selectedPosition - 1);
                loadAgentsForRbh(selectedUser.getId());
            } else {
                loadAllAgents(); // Load all agents if no user selected
            }
        });

        // Reset Button
        resetButton.setOnClickListener(v -> {
            rbhUserSpinner.setSelection(0); // Reset to "Select User"
            agentList.clear();
            displayAgents();
            Toast.makeText(this, "Selection reset", Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOAgentActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing agent team data...", Toast.LENGTH_SHORT).show();
        loadAgentTeamData();
    }

    private void addNewAgent() {
        Toast.makeText(this, "Add New Agent - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Agent activity
    }

    private void loadAgentTeamData() {
        loadRbhUsers(); // Load dropdown options first
        loadAllAgents(); // Then load all agents
    }

    private void loadRbhUsers() {
        executor.execute(() -> {
            try {
                String urlString = API_BASE_URL + "/fetch_sdsa_users_dropdown.php";
                Log.d(TAG, "Fetching RBH users from: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                StringBuilder response = new StringBuilder();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                }

                String responseBody = response.toString();
                Log.d(TAG, "RBH users response: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                
                runOnUiThread(() -> {
                    try {
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray users = jsonResponse.getJSONArray("users");
                            rbhUsers.clear();
                            
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject user = users.getJSONObject(i);
                                RbhUserItem rbhUser = new RbhUserItem(
                                    user.optString("id", ""),
                                    user.optString("username", ""),
                                    user.optString("fullName", ""),
                                    user.optString("designation_name", "")
                                );
                                rbhUsers.add(rbhUser);
                            }

                            // Update spinner
                            updateRbhSpinner();
                            
                            Toast.makeText(this, "Loaded " + rbhUsers.size() + " RBH users", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = jsonResponse.optString("message", "Unknown error");
                            Log.e(TAG, "API Error: " + errorMessage);
                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing RBH users response: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error fetching RBH users: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateRbhSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Select User");
        
        for (RbhUserItem user : rbhUsers) {
            spinnerItems.add(user.getFullName() + " (" + user.getUsername() + ")");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rbhUserSpinner.setAdapter(adapter);
    }

    private void loadAllAgents() {
        loadAllAgents(1); // Load first page by default
    }

    private void loadAllAgents(int page) {
        executor.execute(() -> {
            try {
                String urlString = API_BASE_URL + "/get_all_agents_data.php?page=" + page + "&limit=50";
                Log.d(TAG, "Fetching agents page " + page + " from: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000); // Reduced timeout to prevent hanging
                connection.setReadTimeout(10000); // Reduced timeout to prevent hanging
                connection.setRequestProperty("Accept-Encoding", "gzip"); // Enable compression

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                String responseString = "";
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Use InputStream directly to avoid StringBuilder memory issues
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8192); // Buffer size
                    
                    StringBuilder response = new StringBuilder(8192); // Pre-allocate buffer
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        // Check if response is getting too large
                        if (response.length() > 1024 * 1024) { // 1MB limit
                            Log.e(TAG, "Response too large, stopping read");
                            break;
                        }
                    }
                    reader.close();
                    inputStream.close();
                    responseString = response.toString();
                } else {
                    InputStream errorStream = connection.getErrorStream();
                    if (errorStream != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        errorStream.close();
                        responseString = response.toString();
                    }
                }

                connection.disconnect();

                Log.d(TAG, "Agents API response length: " + responseString.length());

                if (responseString != null && !responseString.isEmpty()) {
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    runOnUiThread(() -> {
                        try {
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray agentsArray = jsonResponse.getJSONArray("agents");
                                
                                // Clear list only on first page
                                if (page == 1) {
                                    agentList.clear();
                                }
                                
                                for (int i = 0; i < agentsArray.length(); i++) {
                                    JSONObject agent = agentsArray.getJSONObject(i);
                                    
                                    // Create AgentItem using constructor with all fields from tbl_agent_data
                                    AgentItem agentItem = new AgentItem(
                                        agent.optString("id", ""),
                                        agent.optString("full_name", ""),
                                        agent.optString("company_name", ""),
                                        agent.optString("Phone_number", ""),
                                        agent.optString("alternative_Phone_number", ""),
                                        agent.optString("email_id", ""),
                                        agent.optString("partnerType", ""),
                                        agent.optString("state", ""),
                                        agent.optString("location", ""),
                                        agent.optString("address", ""),
                                        agent.optString("visiting_card", ""),
                                        agent.optString("created_user", ""),
                                        agent.optString("createdBy", ""),
                                        agent.optString("status", ""),
                                        agent.optString("created_at", ""),
                                        agent.optString("updated_at", ""),
                                        agent.optString("createdBy", "") // Use createdBy as created_by_name since we removed the join
                                    );
                                    
                                    agentList.add(agentItem);
                                }
                                
                                // Get pagination info
                                JSONObject pagination = jsonResponse.optJSONObject("pagination");
                                if (pagination != null) {
                                    int totalCount = pagination.optInt("total_count", 0);
                                    int currentPage = pagination.optInt("current_page", 1);
                                    int totalPages = pagination.optInt("total_pages", 1);
                                    
                                    displayAgents();
                                    Toast.makeText(this, "Loaded " + agentList.size() + " of " + totalCount + " agents (Page " + currentPage + "/" + totalPages + ")", Toast.LENGTH_SHORT).show();
                                } else {
                                    displayAgents();
                                    Toast.makeText(this, "Loaded " + agentList.size() + " agents", Toast.LENGTH_SHORT).show();
                                }
                                
                            } else {
                                String errorMessage = jsonResponse.optString("message", "Unknown error");
                                Log.e(TAG, "API Error: " + errorMessage);
                                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing agents response: " + e.getMessage());
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No response from server", Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching agents: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadAgentsForRbh(String rbhUserId) {
        executor.execute(() -> {
            try {
                String urlString = API_BASE_URL + "/get_all_agents_with_joins.php?rbh_user_id=" + rbhUserId;
                Log.d(TAG, "Fetching agents for RBH from: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                StringBuilder response = new StringBuilder();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                }

                String responseBody = response.toString();
                Log.d(TAG, "Agents response: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                
                runOnUiThread(() -> {
                    try {
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray agents = jsonResponse.getJSONArray("agents");
                            agentList.clear();
                            
                            for (int i = 0; i < agents.length(); i++) {
                                JSONObject agent = agents.getJSONObject(i);
                                AgentItem agentItem = new AgentItem(
                                    agent.optString("id", ""),
                                    agent.optString("full_name", ""),
                                    agent.optString("company_name", ""),
                                    agent.optString("Phone_number", ""),
                                    agent.optString("alternative_Phone_number", ""),
                                    agent.optString("email_id", ""),
                                    agent.optString("partnerType", ""),
                                    agent.optString("branch_state_name", ""),
                                    agent.optString("branch_location", ""),
                                    agent.optString("address", ""),
                                    agent.optString("visiting_card", ""),
                                    agent.optString("created_user", ""),
                                    agent.optString("createdBy", ""),
                                    agent.optString("status", ""),
                                    agent.optString("created_at", ""),
                                    agent.optString("updated_at", ""),
                                    agent.optString("created_by_name", "")
                                );
                                agentList.add(agentItem);
                            }

                            displayAgents();
                            Toast.makeText(this, "Found " + agentList.size() + " agents", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = jsonResponse.optString("message", "Unknown error");
                            Log.e(TAG, "API Error: " + errorMessage);
                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            displayAgents(); // Clear the list
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing agents response: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                        displayAgents(); // Clear the list
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error fetching agents: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    displayAgents(); // Clear the list
                });
            }
        });
    }

    private void displayAgents() {
        agentListContainer.removeAllViews();
        
        if (agentList.isEmpty()) {
            // Create a simple empty state to reduce UI complexity
            TextView emptyText = new TextView(this);
            emptyText.setText("No agents found");
            emptyText.setTextSize(16);
            emptyText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            emptyText.setGravity(android.view.Gravity.CENTER);
            emptyText.setPadding(40, 60, 40, 60);
            
            agentListContainer.addView(emptyText);
            return;
        }

        // Limit display to prevent memory issues - show only first 50 items
        int displayLimit = Math.min(agentList.size(), 50);
        
        // Add agent rows with optimized styling
        for (int i = 0; i < displayLimit; i++) {
            AgentItem agent = agentList.get(i);
            
            // Create card-like row
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(16, 16, 16, 16);
            rowLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            // Alternate row colors
            if (i % 2 == 0) {
                rowLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                rowLayout.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            }
            
            // Add subtle border
            rowLayout.setBackgroundResource(android.R.drawable.list_selector_background);

            // ID
            TextView idText = createStyledTextView(agent.getId(), 1.0f);
            rowLayout.addView(idText);

            // Full Name
            TextView nameText = createStyledTextView(agent.getFullName(), 2.0f);
            nameText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            nameText.setTypeface(null, android.graphics.Typeface.BOLD);
            rowLayout.addView(nameText);

            // Company
            TextView companyText = createStyledTextView(agent.getCompanyName(), 1.5f);
            rowLayout.addView(companyText);

            // Phone
            TextView phoneText = createStyledTextView(agent.getPhoneNumber(), 1.5f);
            rowLayout.addView(phoneText);

            // Email
            TextView emailText = createStyledTextView(agent.getEmailId(), 1.5f);
            rowLayout.addView(emailText);

            // Status with colored badge
            TextView statusText = createStyledTextView(agent.getStatus(), 1.0f);
            statusText.setGravity(android.view.Gravity.CENTER);
            if ("Active".equals(agent.getStatus()) || "1".equals(agent.getStatus())) {
                statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                statusText.setText("● Active");
            } else {
                statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                statusText.setText("● Inactive");
            }
            rowLayout.addView(statusText);

            // Created By
            TextView createdByText = createStyledTextView(agent.getCreatedByName(), 1.5f);
            rowLayout.addView(createdByText);

            // Professional View Button
            Button viewButton = createStyledButton(agent);
            rowLayout.addView(viewButton);

            agentListContainer.addView(rowLayout);
        }
        
        // Add simple message if there are more items
        if (agentList.size() > displayLimit) {
            TextView moreItemsText = new TextView(this);
            moreItemsText.setText("Showing first " + displayLimit + " of " + agentList.size() + " agents");
            moreItemsText.setTextSize(12);
            moreItemsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            moreItemsText.setGravity(android.view.Gravity.CENTER);
            moreItemsText.setPadding(20, 20, 20, 20);
            
            agentListContainer.addView(moreItemsText);
        }
    }

    private TextView createStyledTextView(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text != null ? text : "-");
        textView.setTextSize(11); // Reduced font size
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setPadding(4, 2, 4, 2); // Reduced padding
        textView.setSingleLine(true);
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        textView.setLayoutParams(params);
        
        return textView;
    }

    private Button createStyledButton(AgentItem agent) {
        Button button = new Button(this);
        button.setText("View"); // Removed emoji to reduce rendering complexity
        button.setTextSize(10); // Reduced font size
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setPadding(8, 4, 8, 4); // Reduced padding
        
        // Use simple background instead of gradient
        button.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        button.setLayoutParams(params);
        
        button.setOnClickListener(v -> showAgentDetails(agent));
        
        return button;
    }

    private void showAgentDetails(AgentItem agent) {
        // Create a custom dialog with better styling
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("👤 Complete Agent Details");
        
        // Create a comprehensive details string with ALL tbl_agent_data columns
        StringBuilder details = new StringBuilder();
        details.append("🔹 ").append(agent.getFullName()).append("\n\n");
        
        details.append("📋 Basic Information:\n");
        details.append("• ID: ").append(agent.getId()).append("\n");
        details.append("• Full Name: ").append(agent.getFullName()).append("\n");
        details.append("• Company Name: ").append(agent.getCompanyName()).append("\n");
        details.append("• Partner Type: ").append(agent.getPartnerType()).append("\n");
        details.append("• Status: ").append(agent.getStatus()).append("\n\n");
        
        details.append("📞 Contact Information:\n");
        details.append("• Phone Number: ").append(agent.getPhoneNumber()).append("\n");
        details.append("• Alternative Phone: ").append(agent.getAlternativePhoneNumber()).append("\n");
        details.append("• Email ID: ").append(agent.getEmailId()).append("\n\n");
        
        details.append("📍 Location Information:\n");
        details.append("• State: ").append(agent.getState()).append("\n");
        details.append("• Location: ").append(agent.getLocation()).append("\n");
        details.append("• Address: ").append(agent.getAddress()).append("\n\n");
        
        details.append("📄 Document Information:\n");
        details.append("• Visiting Card: ").append(agent.getVisitingCard()).append("\n");
        details.append("• Created User: ").append(agent.getCreatedUser()).append("\n");
        details.append("• Created By: ").append(agent.getCreatedBy()).append("\n\n");
        
        details.append("👨‍💼 System Information:\n");
        details.append("• Created By Name: ").append(agent.getCreatedByName()).append("\n");
        details.append("• Created At: ").append(agent.getCreatedAt()).append("\n");
        details.append("• Updated At: ").append(agent.getUpdatedAt());
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("✅ Close", null);
        builder.setNegativeButton("📋 Copy Details", (dialog, which) -> {
            // Copy details to clipboard
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Agent Details", details.toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Agent details copied to clipboard!", Toast.LENGTH_SHORT).show();
        });
        
        // Create and show the dialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Style the dialog buttons
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // Data classes
    private static class RbhUserItem {
        private String id;
        private String username;
        private String fullName;
        private String designation;

        public RbhUserItem(String id, String username, String fullName, String designation) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.designation = designation;
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getDesignation() { return designation; }
    }

    private static class AgentItem {
        private String id;
        private String fullName;
        private String companyName;
        private String phoneNumber;
        private String alternativePhoneNumber;
        private String emailId;
        private String partnerType;
        private String state;
        private String location;
        private String address;
        private String visitingCard;
        private String createdUser;
        private String createdBy;
        private String status;
        private String createdAt;
        private String updatedAt;
        private String createdByName;

        public AgentItem(String id, String fullName, String companyName, String phoneNumber, 
                        String alternativePhoneNumber, String emailId, String partnerType, 
                        String state, String location, String address, String visitingCard, 
                        String createdUser, String createdBy, String status, String createdAt, 
                        String updatedAt, String createdByName) {
            this.id = id;
            this.fullName = fullName;
            this.companyName = companyName;
            this.phoneNumber = phoneNumber;
            this.alternativePhoneNumber = alternativePhoneNumber;
            this.emailId = emailId;
            this.partnerType = partnerType;
            this.state = state;
            this.location = location;
            this.address = address;
            this.visitingCard = visitingCard;
            this.createdUser = createdUser;
            this.createdBy = createdBy;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.createdByName = createdByName;
        }

        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public String getCompanyName() { return companyName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
        public String getEmailId() { return emailId; }
        public String getPartnerType() { return partnerType; }
        public String getState() { return state; }
        public String getLocation() { return location; }
        public String getAddress() { return address; }
        public String getVisitingCard() { return visitingCard; }
        public String getCreatedUser() { return createdUser; }
        public String getCreatedBy() { return createdBy; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getCreatedByName() { return createdByName; }
    }
} 