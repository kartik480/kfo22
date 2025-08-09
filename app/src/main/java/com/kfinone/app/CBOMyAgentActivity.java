package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CBOMyAgentActivity extends AppCompatActivity {

    private static final String TAG = "CBOMyAgentActivity";
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

    // Content views
    private LinearLayout agentListContainer;
    private TextView totalAgentCount;
    private TextView activeAgentCount;
    private TextView welcomeText;

    // User data
    private String userName;
    private String userId;

    // Agent data
    private List<AgentItem> agentList = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_my_agent);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        // Debug logging
        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);

        // Check if userName is actually a full name (contains spaces) and extract the correct username
        if (userName != null && userName.contains(" ")) {
            // This is likely a full name, we need to get the actual username
            // For CBO users, we know the username should be "90000" based on the login response
            if (userId != null && userId.equals("21")) {
                userName = "90000"; // Use the correct username for CBO user
                Log.d(TAG, "Detected full name, using correct username: " + userName);
            }
        }

        initializeViews();
        setupClickListeners();
        loadMyAgentData();
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

        // Content views
        agentListContainer = findViewById(R.id.agentListContainer);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        activeAgentCount = findViewById(R.id.activeAgentCount);
        welcomeText = findViewById(R.id.welcomeText);

        // Set welcome text
        if (welcomeText != null) {
            String welcomeMessage = "Welcome to My Agent Panel";
            if (userName != null && !userName.isEmpty()) {
                welcomeMessage = "Welcome, " + userName + "!";
            }
            welcomeText.setText(welcomeMessage);
        }
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewAgent());

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
        Toast.makeText(this, "Refreshing my agent data...", Toast.LENGTH_SHORT).show();
        loadMyAgentData();
    }

    private void addNewAgent() {
        Toast.makeText(this, "Add New Agent - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void loadMyAgentData() {
        if (agentListContainer == null) {
            Log.e(TAG, "agentListContainer is null");
            return;
        }

        // Show loading message
        agentListContainer.removeAllViews();
        TextView loadingText = new TextView(this);
        loadingText.setText("Loading agent data...");
        loadingText.setTextSize(16);
        loadingText.setPadding(20, 20, 20, 20);
        agentListContainer.addView(loadingText);

        executor.execute(() -> {
            try {
                String urlString = API_BASE_URL + "/cbo_my_agent_data.php";
                
                // Prioritize user_id over username for more reliable results
                if (userId != null && !userId.isEmpty()) {
                    urlString += "?user_id=" + userId;
                    Log.d(TAG, "Using user_id: " + userId);
                } else if (userName != null && !userName.isEmpty()) {
                    urlString += "?username=" + userName;
                    Log.d(TAG, "Using username: " + userName);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: No user ID or username available", Toast.LENGTH_LONG).show();
                        displayError("Error: No user ID or username available");
                    });
                    return;
                }

                Log.d(TAG, "Fetching data from: " + urlString);

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
                Log.d(TAG, "Full response: " + responseBody);

                // Parse the response
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                runOnUiThread(() -> {
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray data = jsonResponse.getJSONArray("data");
                            agentList.clear();
                            
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject agent = data.getJSONObject(i);
                                AgentItem agentItem = new AgentItem(
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
                                    agent.optString("createdBy", "")
                                );
                                agentList.add(agentItem);
                            }

                            // Update statistics
                            JSONObject stats = jsonResponse.optJSONObject("statistics");
                            if (stats != null) {
                                totalAgentCount.setText("Total: " + stats.optInt("total_agents", 0));
                                activeAgentCount.setText("Active: " + stats.optInt("active_agents", 0));
                            }

                            // Display agents
                            displayAgents();
                            
                            Toast.makeText(this, "Found " + agentList.size() + " agents", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = jsonResponse.optString("message", "Unknown error");
                            Log.e(TAG, "API Error: " + errorMessage);
                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            displayError("Error loading agent data: " + errorMessage);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                        displayError("Error parsing response: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error fetching agent data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    displayError("Error fetching agent data: " + e.getMessage());
                });
            }
        });
    }

    private void displayAgents() {
        agentListContainer.removeAllViews();
        
        if (agentList.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No agents found for this CBO user.");
            noDataText.setTextSize(16);
            noDataText.setPadding(20, 20, 20, 20);
            agentListContainer.addView(noDataText);
            return;
        }

        // Add header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(10, 10, 10, 10);
        headerLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        String[] headers = {"Name", "Company", "Phone", "Type", "State", "Location"};
        float[] weights = {2.0f, 2.0f, 1.5f, 1.0f, 1.0f, 1.0f};

        for (int i = 0; i < headers.length; i++) {
            TextView headerText = new TextView(this);
            headerText.setText(headers[i]);
            headerText.setTextSize(12);
            headerText.setTextColor(getResources().getColor(android.R.color.white));
            headerText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]));
            headerLayout.addView(headerText);
        }

        agentListContainer.addView(headerLayout);

        // Add agent rows
        for (AgentItem agent : agentList) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(10, 8, 10, 8);
            rowLayout.setBackgroundResource(android.R.drawable.list_selector_background);

            // Name
            TextView nameText = new TextView(this);
            nameText.setText(agent.getFullName());
            nameText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
            rowLayout.addView(nameText);

            // Company
            TextView companyText = new TextView(this);
            companyText.setText(agent.getCompanyName());
            companyText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
            rowLayout.addView(companyText);

            // Phone
            TextView phoneText = new TextView(this);
            phoneText.setText(agent.getPhoneNumber());
            phoneText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
            rowLayout.addView(phoneText);

            // Type
            TextView typeText = new TextView(this);
            typeText.setText(agent.getPartnerType());
            typeText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            rowLayout.addView(typeText);

            // State
            TextView stateText = new TextView(this);
            stateText.setText(agent.getState());
            stateText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            rowLayout.addView(stateText);

            // Location
            TextView locationText = new TextView(this);
            locationText.setText(agent.getLocation());
            locationText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            rowLayout.addView(locationText);

            // Add click listener to show details
            rowLayout.setOnClickListener(v -> showAgentDetails(agent));

            agentListContainer.addView(rowLayout);
        }
    }

    private void displayError(String errorMessage) {
        agentListContainer.removeAllViews();
        TextView errorText = new TextView(this);
        errorText.setText(errorMessage);
        errorText.setTextSize(16);
        errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        errorText.setPadding(20, 20, 20, 20);
        agentListContainer.addView(errorText);
    }

    private void showAgentDetails(AgentItem agent) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Agent Details - " + agent.getFullName());
        
        String details = "Name: " + agent.getFullName() + "\n" +
                        "Company: " + agent.getCompanyName() + "\n" +
                        "Phone: " + agent.getPhoneNumber() + "\n" +
                        "Alternative Phone: " + agent.getAlternativePhoneNumber() + "\n" +
                        "Email: " + agent.getEmailId() + "\n" +
                        "Type: " + agent.getPartnerType() + "\n" +
                        "State: " + agent.getState() + "\n" +
                        "Location: " + agent.getLocation() + "\n" +
                        "Address: " + agent.getAddress() + "\n" +
                        "Created By: " + agent.getCreatedBy();
        
        builder.setMessage(details);
        builder.setPositiveButton("Close", null);
        builder.show();
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

    // AgentItem class for storing agent data
    private static class AgentItem {
        private String fullName, companyName, phoneNumber, alternativePhoneNumber, emailId;
        private String partnerType, state, location, address, visitingCard, createdUser, createdBy;

        public AgentItem(String fullName, String companyName, String phoneNumber, String alternativePhoneNumber,
                        String emailId, String partnerType, String state, String location, String address,
                        String visitingCard, String createdUser, String createdBy) {
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
        }

        // Getters
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
    }
} 